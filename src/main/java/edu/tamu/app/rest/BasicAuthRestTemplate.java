package edu.tamu.app.rest;

import java.util.Collections;

import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class BasicAuthRestTemplate extends RestTemplate {

    private final String username;

    private final String password;

    public BasicAuthRestTemplate(String username, String password) {
        super();
        this.username = username;
        this.password = password;

        if (StringUtils.isEmpty(username)) {
            throw new RuntimeException("Username is mandatory for Basic Auth");
        }

        setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(), Collections.singletonList(new BasicAuthInterceptor(username, password))));
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
