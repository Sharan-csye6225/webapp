package com.cloudcomputing.csye6225.serviceImpl;

import com.cloudcomputing.csye6225.service.DatabaseHealthCheckService;
import com.cloudcomputing.csye6225.utils.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DatabaseHealthCheckServiceImpl implements DatabaseHealthCheckService {

   // private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheckServiceImpl.class);
   Logger logger = LoggerFactory.getLogger("jsonLogger");

    @Autowired
    private DataSource dataSource;

    @Override
    public ResponseEntity<Void> checkDatabaseConnection(HttpServletRequest request) {

        logger.error("**** (error) Inside the checkDatabaseConnection - DatabaseHealthCheckServiceImpl ****");
        logger.info("**** (error) Inside the checkDatabaseConnection - DatabaseHealthCheckServiceImpl ****");
        logger.warn("**** (warn) Inside the checkDatabaseConnection - DatabaseHealthCheckServiceImpl ****");
        logger.debug("**** (debug) Inside the checkDatabaseConnection - DatabaseHealthCheckServiceImpl ****");
        logger.trace("**** (trace) Inside the checkDatabaseConnection - DatabaseHealthCheckServiceImpl ****");

        // Check if the request method is not GET
        if (!RequestMethod.GET.name().equals(request.getMethod())) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(CommonUtil.setHeaders()).body(null);
        }

        // Check if the request includes a payload or query parameter, throw "400 Bad Request"
        if (request.getContentLengthLong() > 0 || request.getQueryString() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(CommonUtil.setHeaders()).body(null);
        }

        return this.isDatabaseConnected();

    }

    @Override
    public ResponseEntity<Void> isDatabaseConnected() {
        // Check Database Connection using dataSource
        try (Connection connection = dataSource.getConnection()) {
            if (null != connection)
                return ResponseEntity.status(HttpStatus.OK).headers(CommonUtil.setHeaders()).body(null);
            else
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        } catch (SQLException e) {
            logger.error("SQLException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(CommonUtil.setHeaders()).body(null);
        }
    }
}
