package edu.tamu.app.rest;

import java.util.Collections;

import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class TokenAuthRestTemplate extends RestTemplate {

    private String token;

    public TokenAuthRestTemplate(String token) {
        super();
        this.token = token;

        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("Token is mandatory for Token Auth");
        }

        setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(), Collections.singletonList(new TokenAuthInterceptor(token))));
    }

    public String getToken() {
        return token;
    }

}
