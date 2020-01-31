package com.gardenlink.authentication.service;


import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.dto.DTOAuthUser;
import com.gardenlink.authentication.repository.UserRepository;
import com.gardenlink.authentication.service.mailer.SendLostPasswordMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    SendLostPasswordMail sendLostPasswordMail;

    @InjectMocks
    UserService userService;

    @Test
    public void updateUser(){
         when(userRepository.getById(any())).thenReturn(Optional.empty());
         assertThat(userService.updateUser("4", new DTOAuthUser())).isEqualTo(null);

         AuthUser authUser = new AuthUser();

         when(userRepository.getById(any())).thenReturn(Optional.of(authUser));

         DTOAuthUser dtoAuthUser = new DTOAuthUser();
         dtoAuthUser.setAvatar("aiuhde");
         dtoAuthUser.setPassword("iuhre");
         dtoAuthUser.setPhone("002544");

         userService.updateUser("4", dtoAuthUser);
    }

    @Test
    public void deleteUser(){
        when(userRepository.getById(any())).thenReturn(Optional.of(new AuthUser()));
        userService.delete("4");
    }

    @Test
    public void testUserRetrieve(){
        when(userRepository.getByEmail(any())).thenReturn(Optional.empty());
        assertThat(userService.getByEmail("coucou")).isEqualTo(null);

        AuthUser authUser = new AuthUser();
        when(userRepository.getByEmail(any())).thenReturn(Optional.of(authUser));
        assertThat(userService.getByEmail("coucou")).isEqualTo(authUser);



        when(userRepository.getByUsername(any())).thenReturn(Optional.empty());
        assertThat(userService.getByUsername("coucou")).isEqualTo(null);

        when(userRepository.getByUsername(any())).thenReturn(Optional.of(authUser));
        assertThat(userService.getByUsername("coucou")).isEqualTo(authUser);


        AuthUser authUser1 = new AuthUser();
        List<AuthUser> authUsers = new ArrayList<>();
        authUsers.add(authUser);
        authUsers.add(authUser1);

        when(userRepository.findAll((Pageable) any())).thenReturn(new PageImpl<>(authUsers));
        assertThat(userService.getUsers(PageRequest.of(0,10)).getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testCreate(){
        DTOAuthUser dtoAuthUser = new DTOAuthUser();

        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        dtoAuthUser.setUsername("coucou");
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        dtoAuthUser.setFirstName("flo");
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        dtoAuthUser.setLastName("test");
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        dtoAuthUser.setEmail("mail");
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        dtoAuthUser.setPassword("pass");
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        dtoAuthUser.setPhone("0000");

        when(userRepository.getByEmail(any())).thenReturn(Optional.of(new AuthUser()));
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        when(userRepository.getByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.getByUsername(any())).thenReturn(Optional.of(new AuthUser()));
        assertThat(userService.create(dtoAuthUser)).isEqualTo(null);
        when(userRepository.getByUsername(any())).thenReturn(Optional.empty());


        when(userRepository.save(any())).thenReturn(new AuthUser());
        userService.create(dtoAuthUser);
        dtoAuthUser.setAvatar("afer");
        userService.create(dtoAuthUser);
    }

    @Test
    public void testLostPassword(){
        when(userRepository.getByEmail(any())).thenReturn(Optional.empty());
        assertThat(userService.sendPasswordResetMail("coucou")).isEqualTo(false);

        AuthUser authUser = new AuthUser();
        authUser.setEmail("coucou@local.dev");
        authUser.setResetToken("ijuzhed");

        when(userRepository.getByEmail(any())).thenReturn(Optional.of(authUser));
        when(userRepository.save(any())).thenReturn(authUser);
        doNothing().when(sendLostPasswordMail).prepareAndSend(any());

        assertThat(userService.sendPasswordResetMail("coucou")).isEqualTo(true);

    }

    @Test
    public void testSetPassword(){
        assertThat(userService.newPassword(null, new DTOAuthUser())).isEqualTo(false);
        assertThat(userService.newPassword("ah", new DTOAuthUser())).isEqualTo(false);

        DTOAuthUser dtoAuthUser = new DTOAuthUser();
        dtoAuthUser.setPassword("newpwd");

        when(userRepository.getByResetToken(any())).thenReturn(Optional.empty());
        assertThat(userService.newPassword("ah", dtoAuthUser)).isEqualTo(false);
        when(userRepository.getByResetToken(any())).thenReturn(Optional.of(new AuthUser()));
        assertThat(userService.newPassword("ah", dtoAuthUser)).isEqualTo(true);
    }


    @Test
    public void testPromoteDemote(){

        when(userRepository.getById(any())).thenReturn(Optional.empty());
        userService.promote("id");
        userService.demote("id");

        when(userRepository.save(any())).thenReturn(new AuthUser());
        when(userRepository.getById(any())).thenReturn(Optional.of(new AuthUser()));
        userService.promote("id");
        userService.demote("id");

    }
}