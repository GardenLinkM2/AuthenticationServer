package com.gardenlink.authentication.domain.dto;


public class DTOAuthToken {

    private String clientId;
    private String username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
