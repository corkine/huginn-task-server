package com.mazhangjing.fast.task_server.web;

import com.mazhangjing.fast.task_server.service.LoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@Controller
public class LoginController {

    private final LoginService service;

    @Autowired
    public LoginController(LoginService service) {
        this.service = service;
    }

    /**
     * /login 用于开发测试，实际 Shiro 通过 /nologin 返回 JSON，用户通过 /login POST 进行登录；
     * @return 登录页面
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() { return "login.html"; }

    /**
     * /nologin 返回当前未登录状态，由 Shiro 负责拦截未授权请求并调用
     * @return 未登录的 JSON 信息
     */
    @GetMapping("/nologin")
    @ResponseBody
    public Object noLogin(
            @RequestParam(required = false, defaultValue = "NO_REASON", name = "type") String type) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("errorCode",3324);
        map.put("reason",type);
        return map;
    }

    /**
     * 发送 POST 请求到 /login 以进行登录
     * @param userName 用户名
     * @param passWord 密码
     * @return 登录结果，JSON 格式，登录成功后不进行跳转，但是允许受限制的 API 调用
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public String handleLoginPost(@RequestParam("userName") String userName,
                                  @RequestParam("passWord") String passWord) {
        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(userName, passWord);
            subject.login(token);
        } catch (Exception e) {
            return "No auth by " + userName + ", " + e.getCause();
        }
        return "Welcome, " + userName + ", " + service.getRole(userName) + ". Go <a href='/'>HOME</a>";
    }

    /**
     * 调用 /logout 以登出系统
     * @return 登出的 JSON 信息
     */
    @RequestMapping("logout")
    @ResponseBody
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "Logout success, go <a href='/'>HOME</a>";
    }
}
