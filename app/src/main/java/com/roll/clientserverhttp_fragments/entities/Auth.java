package com.roll.clientserverhttp_fragments.entities;

/**
 * Created by RDL on 26/02/2017.
 */

public class Auth {
    private String email, password;

    public Auth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Auth() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
