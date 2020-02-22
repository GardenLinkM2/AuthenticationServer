package com.gardenlink.authentication.domain.dto;


public class DTOAuthToken {

    private String clientId;
    private String email;
    private String password;


    public DTOAuthToken(){
        //ign
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
