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

        RepudiatedToken repudiatedToken = repudiatedTokenRepository.getById(tok.getTokenId()).orElse(null);
        return repudiatedToken != null;

    }

    public void repudiateToken(String token){
        DTOTokenInformation tok = introspect(token);

        if(tok!=null){
            RepudiatedToken repudiatedToken = new RepudiatedToken();
            repudiatedToken.setId(tok.getTokenId());
            repudiatedTokenRepository.save(repudiatedToken);
        }

    }

    public Map<String, String> doConnect(DTOAuthToken dtoAuthToken){
        if(dtoAuthToken.getClientId()==null || dtoAuthToken.getClientId().isEmpty()){
            return null;
        }
        if(dtoAuthToken.getPassword()==null || dtoAuthToken.getPassword().isEmpty()){
            return null;
        }
        if(dtoAuthToken.getUsername()==null || dtoAuthToken.getUsername().isEmpty()){
            return null;
        }

        AuthUser authUser = userService.getByUsername(dtoAuthToken.getUsername());
        if(authUser==null){
            return null;
        }

        AuthClient authClient = clientService.getByClientId(dtoAuthToken.getClientId());
        if(authClient==null){
            return null;
        }

        AuthClient accountClient = clientService.getByClientId("account");



        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(!passwordEncoder.matches(dtoAuthToken.getPassword(), authUser.getPassword())){
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

    public DTOTokenInformation introspect(String token) {
        try {
            //1. Get client used to sign token
            String[] str = token.split("\\.");
            String emitter = (String) Jwts.parser().parse(str[0] + "." + str[1] + ".").getHeader().get("kid");

            AuthClient authClient = clientService.getByClientId(emitter);

            if (authClient == null) {
                return null;
            }

            //2 Parse token
            DTOTokenInformation dtoTokenInformation = new DTOTokenInformation();

            Claims claims = Jwts.parser().setSigningKey(authClient.getClientSecret()).parseClaimsJws(token).getBody();

            dtoTokenInformation.setEmitter(claims.get("emitter", String.class));
            dtoTokenInformation.setExpirationTime(claims.get("exp", Date.class));
            dtoTokenInformation.setAdmin(claims.get("isAdmin", Boolean.class));
            dtoTokenInformation.setTokenId(claims.get("jti", String.class));
            dtoTokenInformation.setUsername(claims.get("sub", String.class));
            dtoTokenInformation.setUuid(claims.get("uuid", String.class));
            dtoTokenInformation.setToken(token);

            return dtoTokenInformation;

        } catch (Exception e) {
            return null;
        }
    }

}
