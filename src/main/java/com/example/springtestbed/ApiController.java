package com.example.springtestbed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello there!";
    }
}
