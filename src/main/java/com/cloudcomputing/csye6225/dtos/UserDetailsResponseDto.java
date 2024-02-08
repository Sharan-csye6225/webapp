package com.cloudcomputing.csye6225.dtos;

import lombok.Data;


@Data
public class UserDetailsResponseDto {

    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String accountCreated;
    private String accountUpdated;

    public UserDetailsResponseDto(String id, String firstName, String lastName, String username, String accountCreated, String accountUpdated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.accountCreated = accountCreated;
        this.accountUpdated = accountUpdated;
    }

}
