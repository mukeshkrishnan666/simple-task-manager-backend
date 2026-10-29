package com.mukesh.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AngularRoutingController {

    // Spring Boot 3 compatible pattern: catches top-level routes without dots (e.g., /tasks)
    @RequestMapping("/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}
