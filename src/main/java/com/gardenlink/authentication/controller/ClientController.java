package com.gardenlink.authentication.controller;

import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import com.gardenlink.authentication.service.ClientService;
import com.gardenlink.authentication.domain.dto.DTOAuthClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static com.gardenlink.authentication.Constants.ACCOUNT_CLIENT_NAME;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/clients")
public class ClientController {

    final ClientService clientService;
    final AuthTokenService authTokenService;

    public ClientController(ClientService clientService, AuthTokenService authTokenService) {
        this.clientService = clientService;
        this.authTokenService = authTokenService;
    }

    @GetMapping("")
    public ResponseEntity<Page<AuthClient>> getClients(Pageable page){
        Page<AuthClient> result = clientService.getClients(page);
        return(result.getTotalElements()==0)?ResponseEntity.status(HttpStatus.NO_CONTENT).build():ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthClient> getClientInfo(@PathVariable("id") String id){
        AuthClient authClient = clientService.getById(id);
        return (authClient==null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(authClient);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createClient(@NotNull @Validated @RequestBody DTOAuthClient dtoAuthClient, UriComponentsBuilder ucb, HttpServletRequest request){
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));

        if(token==null || !token.getEmitter().equals(ACCOUNT_CLIENT_NAME) || Boolean.FALSE.equals(token.getAdmin())){
            return ResponseEntity.status(403).build();
        }

        AuthClient authClient = clientService.create(dtoAuthClient);
        return (authClient==null) ? ResponseEntity.badRequest().build() : ResponseEntity.created(ucb.path("/clients/"+authClient.getId()).buildAndExpand().toUri()).body("{\"client_secret\":\""+authClient.getClientSecret()+"\"}");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable("id") String id, HttpServletRequest request){
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));
        if(token==null || !token.getEmitter().equals(ACCOUNT_CLIENT_NAME) || Boolean.FALSE.equals(token.getAdmin())){
            return ResponseEntity.status(403).build();
        }

        clientService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/regenerateSecret", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> regenerateSecret(@PathVariable("id") String id, HttpServletRequest request){
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));
        if(token==null || !token.getEmitter().equals(ACCOUNT_CLIENT_NAME) || Boolean.FALSE.equals(token.getAdmin())){
            return ResponseEntity.status(403).build();
        }
        AuthClient authClient = clientService.regenerateSecret(id);
        return (authClient==null) ? ResponseEntity.badRequest().build() : ResponseEntity.ok().body("{\"client_secret\":\""+authClient.getClientSecret()+"\"}");
    }
}
