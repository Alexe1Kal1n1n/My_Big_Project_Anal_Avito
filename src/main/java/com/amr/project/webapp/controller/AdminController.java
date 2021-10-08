package com.amr.project.webapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping({"/admin"})
    public String showAdminPage(Model model, Authentication authentication) {
        model.addAttribute("admin",authentication.getPrincipal());
        return "adminPage/adminPage";
    }
}
