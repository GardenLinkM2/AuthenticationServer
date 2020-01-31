package com.gardenlink.authentication.service;

import com.gardenlink.authentication.Constants;
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

        if(dtoAuthUser.getAvatar()!=null && !dtoAuthUser.getAvatar().isEmpty()) {
            authUser.setAvatar(dtoAuthUser.getAvatar());
        }
        if(dtoAuthUser.getPassword()!=null && !dtoAuthUser.getPassword().isEmpty()){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            authUser.setPassword(passwordEncoder.encode(dtoAuthUser.getPassword()));
        }
        if(dtoAuthUser.getPhone()!=null && !dtoAuthUser.getPhone().isEmpty()){
            authUser.setPhone(dtoAuthUser.getPhone());
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

        if(Constants.isEmptyOrNull(dtoAuthUser.getUsername())) {
            return null;
        }
        if(Constants.isEmptyOrNull(dtoAuthUser.getFirstName())) {
            return null;
        }
        if(Constants.isEmptyOrNull(dtoAuthUser.getLastName())){
            return null;
        }
        if(Constants.isEmptyOrNull(dtoAuthUser.getEmail())){
            return null;
        }
        if(Constants.isEmptyOrNull(dtoAuthUser.getPassword())){
            return null;
        }
        if(Constants.isEmptyOrNull(dtoAuthUser.getPhone())) {
            return null;
        }

        //Check email + username unique
        if(getByEmail(dtoAuthUser.getEmail())!=null){
            return null;
        }
        if(getByUsername(dtoAuthUser.getUsername())!=null){
            return null;
        }

        AuthUser authUser = new AuthUser();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        authUser.setPassword(passwordEncoder.encode(dtoAuthUser.getPassword()));
        authUser.setAdmin(false);

        if ((dtoAuthUser.getAvatar() == null)) {
            authUser.setAvatar("");
        } else {
            authUser.setAvatar(dtoAuthUser.getAvatar());
        }

        authUser.setUsername(dtoAuthUser.getUsername());
        authUser.setFirstName(dtoAuthUser.getFirstName());
        authUser.setLastName(dtoAuthUser.getLastName());
        authUser.setEmail(dtoAuthUser.getEmail());
        authUser.setPhone(dtoAuthUser.getPhone());

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
        if(token==null || token.isEmpty() || dtoAuthUser.getPassword()==null || dtoAuthUser.getPassword().isEmpty()){
            return false;
        }

        AuthUser authUser = userRepository.getByResetToken(token).orElse(null);
        if(authUser==null){
            return false;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        authUser.setPassword(passwordEncoder.encode(dtoAuthUser.getPassword()));
        authUser.setResetToken("");

        userRepository.save(authUser);
        return true;

    }
}
