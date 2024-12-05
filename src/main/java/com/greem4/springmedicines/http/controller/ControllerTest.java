package com.greem4.springmedicines.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ControllerTest {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
