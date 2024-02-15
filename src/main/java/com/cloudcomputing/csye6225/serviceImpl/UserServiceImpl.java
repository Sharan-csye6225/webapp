package com.cloudcomputing.csye6225.serviceImpl;

import com.cloudcomputing.csye6225.dtos.UserDetailsResponseDto;
import com.cloudcomputing.csye6225.model.User;
import com.cloudcomputing.csye6225.repository.UserRepository;
import com.cloudcomputing.csye6225.service.DatabaseHealthCheckService;
import com.cloudcomputing.csye6225.service.UserService;
import com.cloudcomputing.csye6225.utils.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    DatabaseHealthCheckService databaseHealthCheckService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Validator validator;

    @Override
    public ResponseEntity<?> createNewUser(User user, HttpServletRequest request) {

        if (request.getQueryString() != null) {
            logger.info("The POST request has request parameters which is not allowed!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        if (databaseHealthCheckService.isDatabaseConnected().getStatusCode() != HttpStatus.OK) {
            logger.info("The Database connection Failed!");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (violations.isEmpty() && StringUtils.isAllEmpty(user.getAccountCreated(), user.getAccountUpdated(), user.getId())) {
            if (!userRepository.existsByUsername(user.getUsername())) {
                // Generate and set timestamps
                user.setAccountCreated(LocalDateTime.now().toString());
                user.setAccountUpdated(LocalDateTime.now().toString());
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                // Save the user to the database
                try {
                    User createdUser = userRepository.save(user);
                    UserDetailsResponseDto userCreationResponseDto = new UserDetailsResponseDto(createdUser.getId(), createdUser.getFirstName(), createdUser.getLastName(),
                            createdUser.getUsername(), createdUser.getAccountCreated(), createdUser.getAccountUpdated());
                    return ResponseEntity.status(HttpStatus.CREATED).headers(CommonUtil.setHeaders()).body(userCreationResponseDto);
                } catch (Exception e) {
                    logger.error("Exception Occurred while creating and storing user in the DB: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
                }
            } else {
                logger.info("User already exists in the Database.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body("User already exists in the Database.");
            }
        } else {
            logger.info("The Request doesn't have the required or has incorrect values.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

    }

    @Override
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {

        if (request.getContentLengthLong() > 0 || request.getQueryString() != null) {
            logger.info("The GET request had payload or request parameters which is not allowed!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        if (databaseHealthCheckService.isDatabaseConnected().getStatusCode() != HttpStatus.OK) {
            logger.info("The Database connection Failed!");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        User userFromDb = this.isBasicAuthenticated(request);
        if (null != userFromDb) {
            try {
                UserDetailsResponseDto userCreationResponseDto = new UserDetailsResponseDto(userFromDb.getId(), userFromDb.getFirstName(), userFromDb.getLastName(),
                        userFromDb.getUsername(), userFromDb.getAccountCreated(), userFromDb.getAccountUpdated());
                return ResponseEntity.status(HttpStatus.OK).headers(CommonUtil.setHeaders()).body(userCreationResponseDto);
            } catch (Exception e) {
                logger.error("Exception Occurred while retrieving user data from the DB: {}" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
            }
        } else {
            logger.info("The Authentication failed.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(CommonUtil.setHeaders()).body("Username / Password is incorrect");
        }
    }

    @Override
    public ResponseEntity<String> updateUserDetails(User user, HttpServletRequest request) throws Exception {

        if (request.getQueryString() != null) {
            logger.info("The PUT request has request parameters which is not allowed!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        if (databaseHealthCheckService.isDatabaseConnected().getStatusCode() != HttpStatus.OK) {
            logger.info("The Database connection Failed!");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }

        User userFromDb = this.isBasicAuthenticated(request);

        if (null != userFromDb) {
            if (StringUtils.isAllEmpty(user.getUsername(), user.getAccountCreated(), user.getAccountUpdated()) && !user.getFirstName().isBlank() && !user.getLastName().isBlank() && !user.getPassword().isBlank()) {
                try {
                    //userRepository.updateUserDetailsByUsername(user.getFirstName(), user.getLastName(), passwordEncoder.encode(user.getPassword()), userFromDb.getUsername());
                    if (!StringUtils.isEmpty(user.getFirstName()))
                        userFromDb.setFirstName(user.getFirstName());
                    if (!StringUtils.isEmpty(user.getLastName()))
                        userFromDb.setLastName(user.getLastName());
                    if (!StringUtils.isEmpty(user.getPassword()))
                        userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
                    userFromDb.setAccountUpdated(LocalDateTime.now().toString());
                    userRepository.save(userFromDb);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(CommonUtil.setHeaders()).body(null);
                } catch (Exception e) {
                    logger.error("Exception Occurred while updating user data in the DB: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
                }
            } else {
                logger.info("Invalid Request Body - Restricted field values are tried to be updated.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body("Invalid Request Body.");
            }
        } else {
            logger.info("The Authentication failed.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(CommonUtil.setHeaders()).body("Username / Password is incorrect");
        }

    }

    private User isBasicAuthenticated(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Basic ")) {
            String base64Credentials = header.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            final String[] values = credentials.split(":", 2);

            String username = values[0];
            String password = values[1];

            User userFromDb = userRepository.findByUsername(username);
            if (null != userFromDb) {
                if (passwordEncoder.matches(password, userFromDb.getPassword())) {
                    return userFromDb;
                }
            }
        }
        return null;
    }

}