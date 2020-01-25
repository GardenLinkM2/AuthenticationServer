package com.gardenlink.authentication.service;

import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.RepudiatedToken;
import com.gardenlink.authentication.domain.dto.DTOAuthToken;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.repository.RepudiatedTokenRepository;
import io.jsonwebtoken.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuthTokenService {

    final UserService userService;
    final ClientService clientService;
    final RepudiatedTokenRepository repudiatedTokenRepository;

    public AuthTokenService(UserService userService, ClientService clientService,RepudiatedTokenRepository repudiatedTokenRepository) {
        this.userService = userService;
        this.clientService = clientService;
        this.repudiatedTokenRepository = repudiatedTokenRepository;
    }

    public Boolean isRepudiated(String token){
        DTOTokenInformation tok = introspect(token);

        if(tok==null){
            return false;
        }

        RepudiatedToken repudiatedToken = repudiatedTokenRepository.getById(tok.tokenId).orElse(null);
        return repudiatedToken != null;

    }

    public void repudiateToken(String token){
        DTOTokenInformation tok = introspect(token);

        if(tok!=null){
            RepudiatedToken repudiatedToken = new RepudiatedToken();
            repudiatedToken.setId(tok.tokenId);
            repudiatedTokenRepository.save(repudiatedToken);
        }

    }

    public Map<String, String> doConnect(DTOAuthToken dtoAuthToken){
        if(dtoAuthToken.clientId==null || dtoAuthToken.clientId.isEmpty()){
            System.out.println("clientId is null");
            return null;
        }
        if(dtoAuthToken.password==null || dtoAuthToken.password.isEmpty()){
            System.out.println("password is null");
            return null;
        }
        if(dtoAuthToken.username==null || dtoAuthToken.username.isEmpty()){
            return null;
        }

        AuthUser authUser = userService.getByUsername(dtoAuthToken.username);
        if(authUser==null){
            return null;
        }

        AuthClient authClient = clientService.getByClientId(dtoAuthToken.clientId);
        if(authClient==null){
            return null;
        }

        AuthClient accountClient = clientService.getByClientId("account");



        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(!passwordEncoder.matches(dtoAuthToken.password, authUser.getPassword())){
            System.out.println("Password does not match");
            return null;
        }


        Map<String, Object> map = new HashMap<>();
        map.put("isAdmin", authUser.getAdmin());
        map.put("uuid", authUser.getId());
        map.put("emitter", authClient.getClientId());

        Map<String, Object> header = new HashMap<>();
        map.put("kid", authClient.getClientId());

        //Could connect

        //Generating user token for required service
        Map<String, String> retmap = new HashMap<>();

        String uuid = UUID.randomUUID().toString();

        String accessToken = Jwts.builder()
                .setClaims(map)
                .setId(uuid)
                .setHeader(header)
                .setHeaderParam("kid", authClient.getClientId())
                .setSubject(authUser.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 2048000000))
                .signWith(SignatureAlgorithm.HS512, authClient.getClientSecret())
                .compact();

        retmap.put("access_token", accessToken);

        //Generating user token for account update
        String userToken = Jwts.builder()
                .setClaims(map)
                .setId(uuid)
                .setHeader(header)
                .setHeaderParam("kid", accountClient.getClientId())
                .setSubject(authUser.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 2048000000))
                .signWith(SignatureAlgorithm.HS512, accountClient.getClientSecret())
                .compact();

        retmap.put("user_token", userToken);

        return retmap;
    }

    public DTOTokenInformation introspect(String token){
        //1. Get client used to sign token
        String[] str = token.split("\\.");
        String emitter = (String)Jwts.parser().parse(str[0]+"."+str[1]+".").getHeader().get("kid");

        AuthClient authClient = clientService.getByClientId(emitter);

        if(authClient==null){
            return null;
        }

        //2 Parse token
        DTOTokenInformation dtoTokenInformation = new DTOTokenInformation();

        Claims claims = Jwts.parser().setSigningKey(authClient.getClientSecret()).parseClaimsJws(token).getBody();

        dtoTokenInformation.emitter = claims.get("emitter", String.class);
        dtoTokenInformation.expirationTime = claims.get("exp", Date.class);
        dtoTokenInformation.isAdmin = claims.get("isAdmin", Boolean.class);
        dtoTokenInformation.tokenId = claims.get("jti", String.class);
        dtoTokenInformation.username = claims.get("sub", String.class);
        dtoTokenInformation.uuid = claims.get("uuid", String.class);
        dtoTokenInformation.token = token;

        return dtoTokenInformation;
    }

}
