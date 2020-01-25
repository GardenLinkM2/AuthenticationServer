package com.gardenlink.authentication.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class AuthClient {

    @Id
    private String id = UUID.randomUUID().toString();

    private String clientId;

    @JsonIgnore
    private String clientSecret;

    private String clientName;

    private String clientBaseURL;

    public AuthClient(){
        //Ign
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientBaseURL() {
        return clientBaseURL;
    }

    public void setClientBaseURL(String clientBaseURL) {
        this.clientBaseURL = clientBaseURL;
    }
}
