package com.gardenlink.authentication.service;

import com.gardenlink.authentication.repository.UserRepository;
import com.gardenlink.authentication.service.mailer.SendNewsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsletterService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SendNewsletter sendNewsletter;

    public void sendNewsletter(String content, String title){
        userRepository.findAll().forEach(authUser -> {
            if(authUser.getNewsletter()!=null && authUser.getNewsletter()){
                sendNewsletter.prepareAndSend(content, title, authUser);
            }
        });
    }

}
