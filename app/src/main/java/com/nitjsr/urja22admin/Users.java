package com.nitjsr.urja22admin;

public class Users {
    String email,password;

    public Users(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public Users(){}

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
