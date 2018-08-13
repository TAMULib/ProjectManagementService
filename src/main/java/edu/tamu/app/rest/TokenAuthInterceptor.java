package edu.tamu.app.rest;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class TokenAuthInterceptor implements ClientHttpRequestInterceptor {

    private String token;

    public TokenAuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, tokenBearer(token));
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    public static String tokenBearer(String token) {
        return "Bearer " + token;
    }

}