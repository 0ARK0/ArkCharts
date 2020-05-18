package com.ark.arkcharts.controller;

import com.ark.arkcharts.entity.Chart;
import com.ark.arkcharts.entity.User;
import com.ark.arkcharts.service.ChartService;
import com.ark.arkcharts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * @author Ark
 * @date 2020/05/16 9:45
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    ChartService chartService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        model.addAttribute("account", account);
        model.addAttribute("password", password);
        if("".equals(account) || "".equals(password)){
            model.addAttribute("errorMsg", "账号和密码不能为空");
            return "login";
        }
        User user = userService.findUserByAccountAndPassword(account, password);
        if(user == null){
            model.addAttribute("errorMsg", "账号或密码错误，请重试");
            return "login";
        }
        request.getSession().setAttribute("user", user);
        // 查找该用户的所有图表，并返回
        List<Chart> chartList = chartService.getAllChartsByUser(user);
        model.addAttribute("chartList", chartList);
        return "start";
    }

    @RequestMapping("/register")
    public String register(HttpServletRequest request, Model model){
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String userName = request.getParameter("userName");
        model.addAttribute("account", account);
        model.addAttribute("password", password);
        model.addAttribute("userName", userName);
        if("".equals(account) || "".equals(password) || "".equals(userName)){
            model.addAttribute("errorMsg", "账号/密码/用户名不能为空");
            return "register";
        }
        if(account.length() < 6 || account.length() > 20){
            model.addAttribute("errorMsg", "账号长度不能为小于6或超过20");
            return "register";
        }
        if(password.length() < 4 || password.length() > 20){
            model.addAttribute("errorMsg", "密码长度不能为小于4或超过20");
            return "register";
        }
        if(userName.length() > 10){
            model.addAttribute("errorMsg", "用户名长度不能超过10");
            return "register";
        }
        User user = userService.findUserByAccount(account);
        if(user != null){
            model.addAttribute("errorMsg", "该账号已经存在，换一个试试");
            return "register";
        }
        User user1 = new User();
        user1.setUserId(UUID.randomUUID().toString());
        user1.setAccount(account);
        user1.setPassword(password);
        user1.setUserName(userName);
        user1.setLevel("普通用户");
        userService.saveNewUser(user1);
        model.addAttribute("successMsg", "注册成功，请输入密码登录");
        return "login";
    }
}
