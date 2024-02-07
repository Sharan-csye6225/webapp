package com.cloudcomputing.csye6225.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


public interface DatabaseHealthCheckService {

    ResponseEntity<Void> checkDatabaseConnection(HttpServletRequest request);

}
