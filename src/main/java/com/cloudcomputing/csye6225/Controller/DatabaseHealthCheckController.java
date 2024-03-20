package com.cloudcomputing.csye6225.controller;

import com.cloudcomputing.csye6225.service.DatabaseHealthCheckService;
import com.cloudcomputing.csye6225.utils.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseHealthCheckController {

    Logger logger = LoggerFactory.getLogger("jsonLogger");

    @Autowired
    private DatabaseHealthCheckService databaseHealthCheckService;

    // Method to handle "/healthz" API endpoint, throw "200 OK (Success)" || "503 Service Not Available (Failure)"
    @RequestMapping("/healthz")
    public ResponseEntity<Void> databaseHealthCheckApi(HttpServletRequest request) {
        return databaseHealthCheckService.checkDatabaseConnection(request);
    }

    // Method to handle other API endpoints other than "/healthz" API endpoint, throw "404 Not Found"
    @RequestMapping("/**")
    public ResponseEntity<Void> defaultApi() {
        logger.error("DatabaseHealthCheckController:defaultApi - Request contains payload or query parameter  [ {} ]", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(CommonUtil.setHeaders()).body(null);
    }

    // Method to handle request method types other than "GET", throw "405 Method Not Allowed"
    @RequestMapping(path = "/**", method = {RequestMethod.OPTIONS, RequestMethod.HEAD})
    public ResponseEntity<Void> databaseHealthCheckOptionsApi(HttpServletRequest request) {
        return databaseHealthCheckService.checkDatabaseConnection(request);
    }


}
