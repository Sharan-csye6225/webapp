package com.cloudcomputing.csye6225;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Csye6225Application {

    static Logger logger = LoggerFactory.getLogger("org.springframework.boot");

    public static void main(String[] args) {
        SpringApplication.run(Csye6225Application.class, args);
    }

}
