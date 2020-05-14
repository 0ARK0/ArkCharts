package com.ark.arkcharts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ark
 * @date 2020/05/14 19:44
 */
@Controller
public class WelcomeController {

    @RequestMapping("/")
    public String toStart(){
        return "start";
    }

}
