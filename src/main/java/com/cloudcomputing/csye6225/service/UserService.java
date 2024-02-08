package com.cloudcomputing.csye6225.service;

import com.cloudcomputing.csye6225.dtos.UserDetailsResponseDto;
import com.cloudcomputing.csye6225.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> createNewUser(User user, HttpServletRequest request) throws Exception;

    ResponseEntity<UserDetailsResponseDto> getUserDetails(HttpServletRequest request) throws Exception;

    ResponseEntity<String> updateUserDetails(User user, HttpServletRequest request) throws Exception;



}
