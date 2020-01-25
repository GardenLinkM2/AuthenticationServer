package com.gardenlink.authentication.controller;


import com.gardenlink.authentication.TestUtilities;
import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.dto.DTOAuthUser;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import com.gardenlink.authentication.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    UserService userService;

    @Mock
    AuthTokenService authTokenService;


    @InjectMocks
    UserController userController;

    @Test
    public void getAllUsers(){
        HttpServletRequest request = TestUtilities.getHttpRequest();

        AuthUser user1 = new AuthUser();
        user1.setPhone("0123456789");

        AuthUser user2 = new AuthUser();
        user2.setResetToken("iuzhde");

        List<AuthUser> authUsers = new ArrayList<>();
        authUsers.add(user1);
        authUsers.add(user2);

        when(authTokenService.introspect(any())).thenReturn(null);
        when(userService.getUsers(any())).thenReturn(new PageImpl<>(authUsers));
        assertThat(userController.getUsers(PageRequest.of(0,10), request).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUser(){
        HttpServletRequest request = TestUtilities.getHttpRequest();

        AuthUser user1 = new AuthUser();
        user1.setPhone("0123456789");

        when(authTokenService.introspect(any())).thenReturn(null);
        when(userService.getById(any())).thenReturn(user1);
        assertThat(userController.getUserInfo("id", request).getStatusCode()).isEqualTo(HttpStatus.OK);

        when(userService.getById(any())).thenReturn(null);
        assertThat(userController.getUserInfo("id", request).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteUser(){
        HttpServletRequest request = TestUtilities.getHttpRequest();

        when(authTokenService.introspect(any())).thenReturn(null);
        assertThat(userController.deleteUser("1", request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        DTOTokenInformation token = new DTOTokenInformation();
        token.isAdmin=false;
        token.uuid="coucou";
        token.emitter="account";
        when(authTokenService.introspect(any())).thenReturn(token);
        assertThat(userController.deleteUser("1", request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        token.uuid="1";
        assertThat(userController.deleteUser("1", request).getStatusCode()).isEqualTo(HttpStatus.OK);

        token.uuid="coucou";
        token.isAdmin=true;
        assertThat(userController.deleteUser("1", request).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateUser(){
        HttpServletRequest request = TestUtilities.getHttpRequest();

        when(authTokenService.introspect(any())).thenReturn(null);
        assertThat(userController.updateUserInfo("1", new DTOAuthUser(), request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        DTOTokenInformation token = new DTOTokenInformation();
        token.isAdmin=false;
        token.uuid="coucou";
        token.emitter="account";
        when(authTokenService.introspect(any())).thenReturn(token);
        assertThat(userController.updateUserInfo("1", new DTOAuthUser(), request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        AuthUser authUser = new AuthUser();
        authUser.setPhone("0011");

        when(userService.updateUser(any(), any())).thenReturn(authUser);
        token.uuid="1";
        assertThat(userController.updateUserInfo("1", new DTOAuthUser(), request).getStatusCode()).isEqualTo(HttpStatus.OK);

        token.uuid="coucou";
        token.isAdmin=true;
        assertThat(userController.updateUserInfo("1", new DTOAuthUser(), request).getStatusCode()).isEqualTo(HttpStatus.OK);

        when(userService.updateUser(any(), any())).thenReturn(null);
        assertThat(userController.updateUserInfo("1", new DTOAuthUser(), request).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createUser(){
        when(userService.create(any())).thenReturn(null);
        assertThat(userController.createUser(new DTOAuthUser(), UriComponentsBuilder.newInstance()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AuthUser authUser = new AuthUser();
        authUser.setId("uaihs");
        when(userService.create(any())).thenReturn(authUser);
        assertThat(userController.createUser(new DTOAuthUser(), UriComponentsBuilder.newInstance()).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void lostPassword(){
        when(userService.sendPasswordResetMail(any())).thenReturn(false);
        assertThat(userController.lostPassword("coucou").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        when(userService.sendPasswordResetMail(any())).thenReturn(true);
        assertThat(userController.lostPassword("coucou").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updatePassword(){
        DTOAuthUser dtoAuthUser = new DTOAuthUser();
        dtoAuthUser.password = "newpassword";

        when(userService.newPassword(any(),any())).thenReturn(false);
        assertThat(userController.newPassword("token", dtoAuthUser).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        when(userService.newPassword(any(),any())).thenReturn(true);
        assertThat(userController.newPassword("token", dtoAuthUser).getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(userController.newPassword(null, dtoAuthUser).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.newPassword("", dtoAuthUser).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        dtoAuthUser.password = "";
        assertThat(userController.newPassword("a", dtoAuthUser).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        dtoAuthUser.password = null;
        assertThat(userController.newPassword("a", dtoAuthUser).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);



    }
}