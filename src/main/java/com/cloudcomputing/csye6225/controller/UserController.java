package com.cloudcomputing.csye6225.controller;

import com.cloudcomputing.csye6225.dtos.UserDetailsResponseDto;
import com.cloudcomputing.csye6225.model.User;
import com.cloudcomputing.csye6225.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/v1/user")
    public ResponseEntity<?> createUserApi(@RequestBody User user, HttpServletRequest request) throws Exception {
        return userService.createNewUser(user, request);
    }

    @GetMapping(path = "/v1/user/self")
    public ResponseEntity<UserDetailsResponseDto> getUserDetailsApi(HttpServletRequest request) throws Exception {
        return userService.getUserDetails(request);
    }

    @PutMapping(path = "/v1/user/self")
    public ResponseEntity<String> updateUserApi(@RequestBody User user, HttpServletRequest request) throws Exception {
        return userService.updateUserDetails(user, request);
    }
}
