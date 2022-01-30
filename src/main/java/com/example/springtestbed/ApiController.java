package com.example.springtestbed;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("/greeting")
    public String greeting() {
        var context = SecurityContextHolder.getContext();
        return "Hello there, " + context.getAuthentication().getName() + "!";
    }
}
