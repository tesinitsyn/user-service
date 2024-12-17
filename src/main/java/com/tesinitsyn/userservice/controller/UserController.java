package com.tesinitsyn.userservice.controller;

import com.tesinitsyn.userservice.service.MyUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    MyUserDetailsService myUserDetailsService;

    public UserController(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @GetMapping("/find-user/{username}")
    public ResponseEntity<Boolean> signUp(@PathVariable String username){
        return ResponseEntity.ok(myUserDetailsService.findUser(username));
    }
}
