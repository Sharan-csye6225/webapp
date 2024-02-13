package com.cloudcomputing.csye6225.utils;

import org.springframework.http.HttpHeaders;

public class CommonUtil {
//test comment
    // Static utility method to set all the header variables for the API response
    public static HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.setDate(System.currentTimeMillis());
        return headers;
    }

}
