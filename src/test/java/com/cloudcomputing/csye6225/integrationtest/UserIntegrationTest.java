package com.cloudcomputing.csye6225.integrationtest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/v1/user";

    @Test
    public void testCreateUserDetailsAndValidateUserDetails() {

        // Create an User record
        given()
                .contentType("application/json")
                .body("{ \"first_name\": \"Jane\", \"last_name\": \"Doe\", \"password\": \"test\", \"username\": \"jane.doe@example.com\" }")
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201);

        // Validate User details existence
        given()
                .auth().preemptive().basic("jane.doe@example.com", "test")
                .when()
                .get(BASE_URL + "/self")
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
                .put(BASE_URL + "/self")
                .then()
                .statusCode(204);

        // Validate updated User details
        given()
                .auth().preemptive().basic("jane.doe@example.com", "test_changed")
                .when()
                .get(BASE_URL + "/self")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Jane_changed"))
                .body("lastName", equalTo("Doe_changed"))
                .body("username", equalTo("jane.doe@example.com"));
    }
}

