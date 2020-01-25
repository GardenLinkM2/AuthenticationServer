package com.gardenlink.authentication.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RepudiatedToken {
    @Id
    private String id;

    public RepudiatedToken(){
        //ign
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
