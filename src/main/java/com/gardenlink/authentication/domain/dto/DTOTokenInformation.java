package com.gardenlink.authentication.domain.dto;

import java.util.Date;

public class DTOTokenInformation {

    public String token;

    public Boolean isAdmin;

    public String uuid;

    public String emitter;

    public String tokenId;

    public String username;

    public Date expirationTime;

    public DTOTokenInformation(){
        //ign
    }
}
