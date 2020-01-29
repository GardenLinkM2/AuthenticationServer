package com.gardenlink.authentication.controller;


import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.service.ClientService;
import com.gardenlink.authentication.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SetupControllerTest {
    @Mock
    ClientService clientService;

    @Mock
    UserService userService;

    @InjectMocks
    SetupController setupController;


    @Test
    public void testSetup() {
        ArrayList<AuthUser> users = new ArrayList<>();
        ArrayList<AuthClient> clients = new ArrayList<>();

        AuthUser user = new AuthUser();
        user.setPassword("iue");
        users.add(user);

        when(userService.getUsers(any())).thenReturn(new PageImpl<>(users));
        when(clientService.getClients(any())).thenReturn(new PageImpl<>(clients));

        assertThat(setupController.doSetup().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        users = new ArrayList<>();
        when(userService.getUsers(any())).thenReturn(new PageImpl<>(users));

        doNothing().when(userService).promote(any());
        when(userService.create(any())).thenReturn(new AuthUser());
        when(clientService.create(any())).thenReturn(new AuthClient());

        assertThat(setupController.doSetup().getStatusCode()).isEqualTo(HttpStatus.OK);

        /*
        long users = userService.getUsers(PageRequest.of(0,10)).getTotalElements();
        long clients = clientService.getClients(PageRequest.of(0,10)).getTotalElements();

        if (users!=0 || clients!=0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        HashMap<String, String> setup = new HashMap<>();

        DTOAuthClient dtoAuthClient = new DTOAuthClient();
        dtoAuthClient.clientBaseURL = "https://authm2.artheriom.fr/";
        dtoAuthClient.clientId = "account";
        dtoAuthClient.clientName = "Client account";
        AuthClient authClient = clientService.create(dtoAuthClient);

        DTOAuthUser dtoAuthUser =  new DTOAuthUser();
        dtoAuthUser.password = RandomString.make(24);
        dtoAuthUser.username = "administrator";
        dtoAuthUser.phone = "+33610101010";
        dtoAuthUser.firstName = "Administrator";
        dtoAuthUser.lastName = "Account";
        dtoAuthUser.email = "administrator@localhost.com";
        AuthUser authUser = userService.create(dtoAuthUser);
        userService.promote(authUser.getId());

        HashMap<String, String> ret = new HashMap<>();
        ret.put("clientID", authClient.getClientId());
        ret.put("clientSecret", authClient.getClientSecret());
        ret.put("username", authUser.getUsername());
        ret.put("password", dtoAuthUser.password);

        return ResponseEntity.ok(ret);
    }
         */
    }
}