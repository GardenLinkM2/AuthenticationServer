package com.gardenlink.authentication.service;

import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.dto.DTOAuthUser;
import com.gardenlink.authentication.repository.UserRepository;
import com.gardenlink.authentication.service.mailer.SendLostPasswordMail;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    final UserRepository userRepository;
    final SendLostPasswordMail sendLostPasswordMail;

    public UserService(UserRepository userRepository, SendLostPasswordMail sendLostPasswordMail) {
        this.userRepository = userRepository;
        this.sendLostPasswordMail = sendLostPasswordMail;
    }


    public AuthUser updateUser(String id, DTOAuthUser dtoAuthUser){
        AuthUser authUser = getById(id);
        if(authUser==null){
            return null;
        }

        if(dtoAuthUser.avatar!=null && !dtoAuthUser.avatar.isEmpty()) {
            authUser.setAvatar(dtoAuthUser.avatar);
        }
        if(dtoAuthUser.password!=null && !dtoAuthUser.password.isEmpty()){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            authUser.setPassword(passwordEncoder.encode(dtoAuthUser.password));
        }
        if(dtoAuthUser.phone!=null && !dtoAuthUser.phone.isEmpty()){
            authUser.setPhone(dtoAuthUser.phone);
        }

        return userRepository.save(authUser);
    }

    public void promote(String id){
        AuthUser authUser = getById(id);
        if(authUser!=null){
            authUser.setAdmin(true);
            userRepository.save(authUser);
        }
    }

    public void demote(String id){
        AuthUser authUser = getById(id);
        if(authUser!=null){
            authUser.setAdmin(false);
            userRepository.save(authUser);
        }
    }

    public void delete(String id){
        AuthUser authUser = getById(id);

        if(authUser!=null){
            userRepository.delete(authUser);
        }
    }

    public AuthUser getByUsername(String username){
        return userRepository.getByUsername(username).orElse(null);
    }

    public AuthUser getByEmail(String email){
        return userRepository.getByEmail(email).orElse(null);
    }



    public Page<AuthUser> getUsers(Pageable page){
        return userRepository.findAll(page);
    }

    public AuthUser getById(String id){
        return userRepository.getById(id).orElse(null);
    }

    public AuthUser create(DTOAuthUser dtoAuthUser){
        if(dtoAuthUser.username==null || dtoAuthUser.username.isEmpty()) {
            return null;
        }
        if(dtoAuthUser.firstName==null || dtoAuthUser.firstName.isEmpty()) {
            return null;
        }
        if(dtoAuthUser.lastName==null || dtoAuthUser.lastName.isEmpty()){
            return null;
        }
        if(dtoAuthUser.email==null || dtoAuthUser.email.isEmpty()){
            return null;
        }
        if(dtoAuthUser.password==null || dtoAuthUser.password.isEmpty()){
            return null;
        }
        if(dtoAuthUser.phone==null || dtoAuthUser.phone.isEmpty()) {
            return null;
        }

        //Check email + username unique
        if(getByEmail(dtoAuthUser.email)!=null){
            return null;
        }
        if(getByUsername(dtoAuthUser.username)!=null){
            return null;
        }

        AuthUser authUser = new AuthUser();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        authUser.setPassword(passwordEncoder.encode(dtoAuthUser.password));
        authUser.setAdmin(false);

        if ((dtoAuthUser.avatar == null)) {
            authUser.setAvatar("");
        } else {
            authUser.setAvatar(dtoAuthUser.avatar);
        }

        authUser.setUsername(dtoAuthUser.username);
        authUser.setFirstName(dtoAuthUser.firstName);
        authUser.setLastName(dtoAuthUser.lastName);
        authUser.setEmail(dtoAuthUser.email);
        authUser.setPhone(dtoAuthUser.phone);

        return userRepository.save(authUser);
    }

    public Boolean sendPasswordResetMail(String email){
        AuthUser authUser = userRepository.getByEmail(email).orElse(null);
        if(authUser==null || authUser.getEmail()==null || authUser.getEmail().isEmpty()){
            return false;
        }

        authUser.setResetToken(RandomString.make(32));
        userRepository.save(authUser);

        //Send mail
        sendLostPasswordMail.prepareAndSend(authUser);
        return true;
    }

    public Boolean newPassword(String token, DTOAuthUser dtoAuthUser){
        if(token==null || token.isEmpty() || dtoAuthUser.password==null || dtoAuthUser.password.isEmpty()){
            return false;
        }

        AuthUser authUser = userRepository.getByResetToken(token).orElse(null);
        if(authUser==null){
            return false;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        authUser.setPassword(passwordEncoder.encode(dtoAuthUser.password));
        authUser.setResetToken("");

        userRepository.save(authUser);
        return true;

    }
}
