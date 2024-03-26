package com.cloudcomputing.csye6225.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Entity
@DynamicUpdate
public class User {

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @NotNull
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @NotNull
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address")
    private String username;

    @NotBlank
    @NotNull
    private String password;

    @Column(name = "account_created", updatable = false)
    @JsonProperty("account_created")
    private String accountCreated;

    @JsonProperty("account_updated")
    private String accountUpdated;

    @JsonProperty("user_verified")
    private boolean userVerified;

    @NotNull
    @Column(name = "user_token", updatable = false)
    private String userToken;

    @Column(name = "email_sent_time", updatable = false)
    @JsonProperty("email_sent_time")
    private String emailSentTime;

    public User() {
        this.userToken = UUID.randomUUID().toString(); // Generate a random UUID for user token
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(String accountCreated) {
        this.accountCreated = accountCreated;
    }

    public String getAccountUpdated() {
        return accountUpdated;
    }

    public void setAccountUpdated(String accountUpdated) {
        this.accountUpdated = accountUpdated;
    }

    public boolean getUserVerified() { return userVerified; }

    public void setUserVerified(boolean userVerified) { this.userVerified = userVerified; }

    public String getUserToken() { return userToken; }

    public void setUserToken(String userToken) { this.userToken = userToken; }

    public String getEmailSentTime() { return emailSentTime; }

    public void setEmailSentTime(String emailSentTime) { this.emailSentTime = emailSentTime; }

}
