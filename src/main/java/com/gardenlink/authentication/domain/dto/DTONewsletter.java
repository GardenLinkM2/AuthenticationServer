package com.gardenlink.authentication.domain.dto;


public class DTONewsletter {

    private String content;

    private String title;

    public DTONewsletter(){
        //ign
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
