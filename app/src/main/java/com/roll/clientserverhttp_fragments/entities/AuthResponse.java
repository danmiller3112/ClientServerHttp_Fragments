package com.roll.clientserverhttp_fragments.entities;

/**
 * Created by RDL on 26/02/2017.
 */

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
