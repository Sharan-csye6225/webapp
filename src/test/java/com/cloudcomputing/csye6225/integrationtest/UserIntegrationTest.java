package com.cloudcomputing.csye6225.integrationtest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    public void testCreateUserDetailsAndValidateUserDetails() {

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // Create a User record
        given()
                .contentType("application/json")
                .body("{ \"first_name\": \"Jane\", \"last_name\": \"Doe\", \"password\": \"test\", \"username\": \"jane.doe@example.com\" }")
                .when()
                .post("/v1/user")
                .then()
                .statusCode(201);

        // Validate User details existence
        given()
                .auth().preemptive().basic("jane.doe@example.com", "test")
                .when()
                .get("/v1/user/self")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Doe"))
                .body("username", equalTo("jane.doe@example.com"));
    }

    @Test
    public void testUpdateUserDetailsAndValidateUpdateUserDetails() {

        // Update the User record
        given()
                .auth().preemptive().basic("jane.doe@example.com", "test")
                .contentType("application/json")
                .body("{ \"first_name\": \"Jane_changed\", \"last_name\": \"Doe_changed\", \"password\": \"test_changed\" }")
                .when()
                .put("/v1/user/self")
                .then()
                .statusCode(204);

        // Validate updated User details
        given()
                .auth().preemptive().basic("jane.doe@example.com", "test_changed")
                .when()
                .get("/v1/user/self")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Jane_changed"))
                .body("lastName", equalTo("Doe_changed"))
                .body("username", equalTo("jane.doe@example.com"));
    }
}


