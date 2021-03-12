package com.cdad.project.executionservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
public class TestController {
    @GetMapping("{name}")
    @ResponseBody
    public String helloMessage(@PathVariable String name) {
        return "<h1>Hello " + name + "</h1>";
    }
}
