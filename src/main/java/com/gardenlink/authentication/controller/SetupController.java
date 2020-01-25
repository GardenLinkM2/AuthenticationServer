package com.gardenlink.authentication.controller;

import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.dto.DTOAuthClient;
import com.gardenlink.authentication.domain.dto.DTOAuthUser;
import com.gardenlink.authentication.service.ClientService;
import com.gardenlink.authentication.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class SetupController {

    final UserService userService;

    final ClientService clientService;

    public SetupController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @GetMapping("/setup")
    public ResponseEntity<HashMap<String, String>> doSetup(){
        long users = userService.getUsers(PageRequest.of(0,10)).getTotalElements();
        long clients = clientService.getClients(PageRequest.of(0,10)).getTotalElements();

        if (users!=0 || clients!=0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

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
}
