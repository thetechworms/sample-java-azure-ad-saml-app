package com.example.saml;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Home page — accessible to authenticated users.
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * Profile page — displays SAML attributes returned by Azure AD.
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
        model.addAttribute("name", principal.getName());
        model.addAttribute("attributes", principal.getAttributes());
        return "profile";
    }
}
