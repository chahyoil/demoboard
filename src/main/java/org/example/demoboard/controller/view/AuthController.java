package org.example.demoboard.controller.view;

import org.example.demoboard.controller.api.ApiPublicPostController;
import org.example.demoboard.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final String anonymousUserId = "anonymousUser";

    private static final Logger logger = LoggerFactory.getLogger(ApiPublicPostController.class);

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/sign/login")
    public String login() {

        String authorities = authService.getAuthorities();
        String userId = authService.getUserName();

        if(authorities != null && userId != null && !userId.equals(anonymousUserId)) {
            return "redirect:/index";
        }

        return "sign/login";
    }

    @GetMapping("/sign/signup")
    public String register() {

        String authorities = authService.getAuthorities();
        String userId = authService.getUserName();

        if(authorities != null && userId != null && !userId.equals(anonymousUserId)) {
            return "redirect:/index";
        }

        return "sign/signup";
    }

}
