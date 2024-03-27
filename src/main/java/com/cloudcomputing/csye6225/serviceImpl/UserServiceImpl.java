package com.cloudcomputing.csye6225.serviceImpl;

import com.cloudcomputing.csye6225.dtos.UserDetailsResponseDto;
import com.cloudcomputing.csye6225.model.User;
import com.cloudcomputing.csye6225.repository.UserRepository;
import com.cloudcomputing.csye6225.service.DatabaseHealthCheckService;
import com.cloudcomputing.csye6225.service.UserService;
import com.cloudcomputing.csye6225.utils.CommonUtil;
import com.cloudcomputing.csye6225.utils.PubSubMessageHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = (Logger) LoggerFactory.getLogger("jsonLogger");

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    DatabaseHealthCheckService databaseHealthCheckService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Validator validator;
    @Value("${gcp.project.name}")
    private String gcpProjectName;
    @Value("${gcp.pubsub.topic.name}")
    private String gcpPubSubTopicName;

    @Override
    public ResponseEntity<?> createNewUser(User user, HttpServletRequest request) {

        logger.debug("**** UserServiceImpl:createNewUser - 'IN' ****");

        if (request.getQueryString() != null) {
            logger.error("UserServiceImpl:createNewUser - POST request should not have query params [ {} ]", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        if (databaseHealthCheckService.isDatabaseConnected().getStatusCode() != HttpStatus.OK) {
            logger.error("UserServiceImpl:createNewUser - The Database connection Failed! [ {} ]", HttpStatus.SERVICE_UNAVAILABLE);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (violations.isEmpty() && StringUtils.isAllEmpty(user.getAccountCreated(), user.getAccountUpdated(), user.getId())) {
            if (!userRepository.existsByUsername(user.getUsername())) {
                // Generate and set timestamps
                user.setAccountCreated(LocalDateTime.now(ZoneOffset.UTC).toString());
                user.setAccountUpdated(LocalDateTime.now(ZoneOffset.UTC).toString());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setUserVerified(false);

                // Save the user to the database
                try {
                    User createdUser = userRepository.save(user);
                    PubSubMessageHandler.publishMessage(gcpProjectName, gcpPubSubTopicName, createdUser.getUsername(), createdUser.getUserToken());
                            UserDetailsResponseDto userCreationResponseDto = new UserDetailsResponseDto(createdUser.getId(), createdUser.getFirstName(), createdUser.getLastName(),
                                    createdUser.getUsername(), createdUser.getAccountCreated(), createdUser.getAccountUpdated());
                    logger.info("UserServiceImpl:createNewUser - User details created and stored in the DB:  [ {} ]", HttpStatus.CREATED);
                    return ResponseEntity.status(HttpStatus.CREATED).headers(CommonUtil.setHeaders()).body(userCreationResponseDto);
                } catch (Exception e) {
                    logger.error("UserServiceImpl:createNewUser - Exception Occurred while creating and storing user in the DB: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
                }
            } else {
                logger.error("UserServiceImpl:createNewUser - User already exists in the Database. [ {} ]", HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "User already exists in the Database."));
            }
        } else {
            if (!StringUtils.isAllEmpty(user.getAccountCreated(), user.getAccountUpdated()))
                logger.warn("UserServiceImpl:createNewUser - Account creation / update time need not be given.");
            logger.error("UserServiceImpl:createNewUser - The Request doesn't have the required or has incorrect values. [ {} ]", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

    }

    @Override
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) throws Exception {

        logger.debug("**** UserServiceImpl:getUserDetails - 'IN' ****");

        if (request.getContentLengthLong() > 0 || request.getQueryString() != null) {
            logger.error("UserServiceImpl:getUserDetails - The GET request had payload or request parameters which is not allowed! [ {} ]", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        if (databaseHealthCheckService.isDatabaseConnected().getStatusCode() != HttpStatus.OK) {
            logger.error("UserServiceImpl:getUserDetails - The Database connection Failed! [ {} ]", HttpStatus.SERVICE_UNAVAILABLE);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        User userFromDb = this.isBasicAuthenticated(request);
        if (null != userFromDb) {

            if (!CommonUtil.isUserVerified(userFromDb))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "User not verified"));

            try {
                UserDetailsResponseDto userCreationResponseDto = new UserDetailsResponseDto(userFromDb.getId(), userFromDb.getFirstName(), userFromDb.getLastName(),
                        userFromDb.getUsername(), userFromDb.getAccountCreated(), userFromDb.getAccountUpdated());
                logger.info("UserServiceImpl:getUserDetails - User details are fetched from the DB:  [ {} ]", HttpStatus.OK);
                return ResponseEntity.status(HttpStatus.OK).headers(CommonUtil.setHeaders()).body(userCreationResponseDto);
            } catch (Exception e) {
                logger.error("UserServiceImpl:getUserDetails - Exception Occurred while retrieving user data from the DB: {}" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
            }
        } else {
            logger.error("UserServiceImpl:getUserDetails - The Username / Password is incorrect. If new user, please create the user. [ {} ]", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "Username / Password is incorrect"));
        }
    }

    @Override
    public ResponseEntity<?> updateUserDetails(User user, HttpServletRequest request) throws Exception {

        logger.debug("**** UserServiceImpl:updateUserDetails - 'IN' ****");

        if (request.getQueryString() != null) {
            logger.error("UserServiceImpl:updateUserDetails - The PUT request has request parameters which is not allowed! [ {} ]", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        if (databaseHealthCheckService.isDatabaseConnected().getStatusCode() != HttpStatus.OK) {
            logger.error("UserServiceImpl:updateUserDetails - The Database connection Failed! [ {} ]", HttpStatus.SERVICE_UNAVAILABLE);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }

        User userFromDb = this.isBasicAuthenticated(request);

        if (null != userFromDb) {

            if (!CommonUtil.isUserVerified(userFromDb))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "User not verified"));

            if (StringUtils.isAllEmpty(user.getUsername(), user.getAccountCreated(), user.getAccountUpdated()) && !user.getFirstName().isBlank() && !user.getLastName().isBlank() && !user.getPassword().isBlank()) {
                try {
                    if (!StringUtils.isEmpty(user.getFirstName()))
                        userFromDb.setFirstName(user.getFirstName());
                    if (!StringUtils.isEmpty(user.getLastName()))
                        userFromDb.setLastName(user.getLastName());
                    if (!StringUtils.isEmpty(user.getPassword()))
                        userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
                    userFromDb.setAccountUpdated(LocalDateTime.now().toString());
                    userRepository.save(userFromDb);
                    logger.info("UserServiceImpl:updateUserDetails - User details updated in the DB: {}", HttpStatus.NO_CONTENT);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(CommonUtil.setHeaders()).body(null);
                } catch (Exception e) {
                    logger.error("UserServiceImpl:updateUserDetails - Exception Occurred while updating user data in the DB: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
                }
            } else {
                if (!StringUtils.isEmpty(user.getUsername()))
                    logger.warn("UserServiceImpl:updateUserDetails - The Username can not be updated. [ {} ]", HttpStatus.BAD_REQUEST);
                logger.error("UserServiceImpl:updateUserDetails -  Invalid Request Body - Restricted field values are tried to be updated. [ {} ]", HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "Invalid Request Body."));
            }
        } else {
            logger.error("UserServiceImpl:updateUserDetails - The Username / Password is incorrect. If new user, please create the user. [ {} ]", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "Username / Password is incorrect"));
        }

    }

    @Override
    public ResponseEntity<?> getUserVerificationInformation(HttpServletRequest request) throws Exception {
        if (null != request && null != request.getQueryString()) {
            String[] queryParams = request.getQueryString().split("&");
            String username = queryParams[0];
            String userToken = queryParams[1];
            User userFromDb = userRepository.findByUsername(username);
            if (null != userFromDb) {
                long minutesDiff = Duration.between(LocalDateTime.parse(userFromDb.getAccountCreated()), LocalDateTime.parse(userFromDb.getEmailSentTime())).toMinutes();
                if (minutesDiff <= 2 && userFromDb.getUserToken().equalsIgnoreCase(userToken)) {
                    userFromDb.setUserVerified(true);
                    userRepository.save(userFromDb);
                    logger.info("UserServiceImpl:getUserVerificationInformation - The Username details are verified. [ {} ]", HttpStatus.OK);
                    return ResponseEntity.status(HttpStatus.OK).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "The User details are verified"));
                } else {
                    logger.error("UserServiceImpl:getUserVerificationInformation - The link has expired, hence the user details are not verified. Please contact the administrator [ {} ]", HttpStatus.BAD_REQUEST);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "The link has expired, hence the user details are not verified. Please contact the administrator"));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(Collections.singletonMap("status", "The link has expired, hence the user details are not verified. Please contact the administrator"));
    }

    private User isBasicAuthenticated(HttpServletRequest request) {

        logger.debug("**** UserServiceImpl:isBasicAuthenticated - 'IN' ****");

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Basic ")) {
            String base64Credentials = header.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            final String[] values = credentials.split(":", 2);

            String username = values[0];
            String password = values[1];

            User userFromDb = userRepository.findByUsername(username);
            if (null != userFromDb) {
                if (passwordEncoder.matches(password, userFromDb.getPassword())) return userFromDb;
                else
                    logger.warn("UserServiceImpl:isBasicAuthenticated - The Username & Password doesn't match.");
            }
        }
        return null;
    }

}