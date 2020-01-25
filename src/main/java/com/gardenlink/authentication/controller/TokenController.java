package com.gardenlink.authentication.controller;

import com.gardenlink.authentication.domain.dto.DTOAuthToken;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
public class TokenController {

    final AuthTokenService authTokenService;

    public TokenController(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @PostMapping(value = "/auth/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> login(@NotNull @Validated @RequestBody DTOAuthToken dtoAuthToken){
        Map<String, String> token = authTokenService.doConnect(dtoAuthToken);
        if(token==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/token/introspect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DTOTokenInformation> tokenIntrospect(@NotNull @Validated @RequestBody DTOTokenInformation token){
        DTOTokenInformation dtoTokenInformation = authTokenService.introspect(token.token);
        if(dtoTokenInformation==null || authTokenService.isRepudiated(token.token)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dtoTokenInformation);
    }

    @DeleteMapping("/token/{token}")
    public ResponseEntity<Void> repudiate(@PathVariable("token") String token){
        authTokenService.repudiateToken(token);
        return ResponseEntity.ok().build();
    }
}
