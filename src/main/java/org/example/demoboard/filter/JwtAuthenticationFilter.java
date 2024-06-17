package org.example.demoboard.filter;

import org.example.demoboard.entity.RefreshToken;
import org.example.demoboard.security.CustomUserDetailsService;
import org.example.demoboard.security.JwtAuthenticationToken;
import org.example.demoboard.security.JwtTokenProvider;
import org.example.demoboard.security.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

import static org.example.demoboard.constants.JwtConstants.*;

/*
    * JwtAuthenticationFilter class is responsible for authenticating JWT tokens.
 */
@Component
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final String[] permitAll = {"/auth/", "/public/", // "/api/public/",
                            "/api/auth/login", "/api/auth/register",
                            "/css/", "/js/", "/favicon.ico",
                            "/post/", "/index", "/sign/"};
//    private final String[] needJWT = {"/api/public/"};

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService, RefreshTokenService refreshTokenService
    ) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        boolean shouldNotFilter = Arrays.stream(permitAll).anyMatch(path::startsWith);

        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        logger.info("doFilterInternal Before Request Path : " + path);

        for (String url : permitAll) {
            if(path.startsWith(url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        logger.info("doFilterInternal After Request Path : " + path);

        boolean IS_ACCESS_TOKEN = true;
        String access_token = getJwtFromRequest(request, IS_ACCESS_TOKEN);
        JwtAuthenticationToken authentication = null;

        // Access 토큰이 유효한 경우
        if (access_token != null && tokenProvider.validateToken(access_token)) {
            String userId = tokenProvider.getUserIdFromJWT(access_token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(userId));

            if (userDetails != null) {
                authentication = new JwtAuthenticationToken(userDetails, access_token, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            // Access 토큰이 만료되었을 경우 Refresh 토큰을 사용하여 새로운 Access 토큰 발급
            IS_ACCESS_TOKEN = false;
            String refreshToken = getJwtFromRequest(request, IS_ACCESS_TOKEN);
            if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
                String userName = tokenProvider.getUserIdFromJWT(refreshToken);
                Optional<RefreshToken> savedRefreshToken = refreshTokenService.findByToken(refreshToken);
                if(savedRefreshToken.isPresent() && savedRefreshToken.get().getUsername().equals(userName)) {

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
                    String authorities = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));

                    String newAccessToken = tokenProvider.createAccessToken(userName, authorities);

                    // 응답 헤더에 새로운 Access 토큰 추가
                    response.setHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + newAccessToken);

                    // 새로운 Access 토큰으로 인증 객체 설정
                    authentication = new JwtAuthenticationToken(userDetails, newAccessToken, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 수정됨
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 수정됨
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request, boolean isAccessToken) {

        if(isAccessToken) {
            String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
            if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
                return bearerToken.substring(TOKEN_PREFIX.length());
            }
        } else {
            String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
            if (refreshToken != null && refreshToken.startsWith(TOKEN_PREFIX)) {
                return refreshToken.substring(TOKEN_PREFIX.length());
            }
        }

        return null;
    }
}
