package com.cloudcomputing.csye6225.utils;

import com.cloudcomputing.csye6225.model.User;
import org.springframework.http.HttpHeaders;

public class CommonUtil {

    // Static utility method to set all the header variables for the API response
    public static HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.setDate(System.currentTimeMillis());
        return headers;
    }

    public static boolean isUserVerified (User userFromDb) {
        boolean isVerified = false;
        if (null != userFromDb) {
            isVerified = userFromDb.getUserVerified();
        }
        return isVerified;
    }

}
