package com.newcoder.community.controller;

import com.newcoder.community.entity.User;
import com.newcoder.community.util.CommonUtil;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){return "<h1>hello SpringBoot<h1>";}

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        response.setContentType("text/html");

    }

    // /student?current=1&limit=20 ->查询所有student的信息，当前是第1页，需要20条信息
    @RequestMapping(path = "/student", method = RequestMethod.GET)
    @ResponseBody
    public String student(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current){
        return "current is" + Integer.toString(current);
    }

    @RequestMapping(path = "/student2/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String student2(@PathVariable(name = "id")int id){
        return Integer.toString(id);
    }
    @PostMapping(path = "/student")
    @ResponseBody
    public String student3(String name, int num){ //postMapping的参数不用声明可以为空，如果没传入则
        System.out.println(name);
        System.out.println(num);
        return "success";
    }
    @GetMapping(path = "/info")
    @ResponseBody
    public User info(){
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "yky");
        map.put("age", 20);
        result.add(map);
        map = new HashMap<>();
        map.put("name", "cyl");
        map.put("age", 20);
        result.add(map);
        User user = new User();
        user.setEmail("1123@qq.com");
        user.setSalt("12345");
        user.setPassword("123");
        user.setUsername("ykyy");
        user.setHeaderUrl("dsf");
        map = new HashMap<>();
        map.put("user",user);
        result.add(map);
        return user;
    }

    @PostMapping(path = "/testUser")
    @ResponseBody
    public String testUser(User user){
        System.out.println(user);
        return "success";
    }

    @GetMapping(path = "/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommonUtil.generateUUID());
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60*10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @GetMapping(path = "/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id", 1);
        session.setAttribute("name", "test");
        return "set session";
    }

    @GetMapping(path = "/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }
}
