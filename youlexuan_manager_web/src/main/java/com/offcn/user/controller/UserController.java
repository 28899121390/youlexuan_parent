package com.offcn.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @RequestMapping("showName")
    public Map<String,Object> showLoginName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        Map<String,Object> hashMap = new HashMap();
        hashMap.put("UserName", name);
        return hashMap;
    }
}
