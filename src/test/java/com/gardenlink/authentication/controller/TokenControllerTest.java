package com.gardenlink.authentication.controller;


import com.gardenlink.authentication.domain.dto.DTOAuthToken;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenControllerTest {
    @Mock
    AuthTokenService authTokenService;

    @InjectMocks
    TokenController tokenController;

    @Test
    public void TestCreateLogin(){
        Map<String, String> token = new HashMap<>();
        token.put("access_token", "a)");

        when(authTokenService.doConnect(any())).thenReturn(null);
        assertThat(tokenController.login(new DTOAuthToken()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        when(authTokenService.doConnect(any())).thenReturn(token);
        assertThat(tokenController.login(new DTOAuthToken()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void TestDeleteToken(){
        doNothing().when(authTokenService).repudiateToken(any());
        assertThat(tokenController.repudiate("a").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void TestIntrospectToken(){

        DTOTokenInformation dtoTokenInformation = new DTOTokenInformation();
        dtoTokenInformation.token = "ahiduz";

        when(authTokenService.introspect(any())).thenReturn(dtoTokenInformation);
        when(authTokenService.isRepudiated(any())).thenReturn(false);
        assertThat(tokenController.tokenIntrospect(new DTOTokenInformation()).getStatusCode()).isEqualTo(HttpStatus.OK);

        when(authTokenService.isRepudiated(any())).thenReturn(true);
        assertThat(tokenController.tokenIntrospect(new DTOTokenInformation()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        when(authTokenService.introspect(any())).thenReturn(null);
        assertThat(tokenController.tokenIntrospect(new DTOTokenInformation()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);



    }

}