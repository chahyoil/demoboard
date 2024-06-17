package org.example.demoboard.controller.view;

import org.example.demoboard.controller.api.ApiPublicPostController;
import org.example.demoboard.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/")
public class HomeController {

    @Autowired
    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(ApiPublicPostController.class);

    public HomeController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("index")
    public String home(Model model) {
//
//        String authorities = authService.getAuthorities();
//        String userId = authService.getUserId();
//
//        // 로그인 여부를 판단하는 부분
//        boolean isAuthenticated = userId != null && !userId.equals("anonymousUser");
//
//        logger.info("authorities: {}", authorities);
//        logger.info("userId: {}", userId);
//        logger.info("isAuthenticated: {}", isAuthenticated);
//
//        // 미 로그인시 userId : anonymousUser, authorities: ROLE_ANONYMOUS
//        model.addAttribute("authorities", authorities);
//        model.addAttribute("userId", userId);
//        model.addAttribute("isAuthenticated", isAuthenticated);

        return "index";
    }

    @GetMapping("side")
    public String showSidePage() {
        return "comm/side";  // templates/views/side.html 파일을 렌더링
    }

    @GetMapping("head")
    public String showHeadPage() {
        return "comm/head";  // templates/views/head.html 파일을 렌더링
    }

    @GetMapping("nav")
    public String showNavPage() {
        return "comm/nav";  // templates/views/nav.html 파일을 렌더링
    }


}
