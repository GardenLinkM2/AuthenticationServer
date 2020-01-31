package com.gardenlink.authentication.service;


import com.gardenlink.authentication.TestUtilities;
import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.RepudiatedToken;
import com.gardenlink.authentication.domain.dto.DTOAuthToken;
import com.gardenlink.authentication.repository.RepudiatedTokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestAuthTokenService {

    @Mock
    ClientService clientService;

    @Mock
    UserService userService;

    @Mock
    RepudiatedTokenRepository repudiatedTokenRepository;

    @InjectMocks
    AuthTokenService authTokenService;

    @Test
    public void checkRepudiated(){

        //generating token
        String token = TestUtilities.generateToken();
        when(clientService.getByClientId(any())).thenReturn(null);
        assertThat(authTokenService.isRepudiated(token)).isEqualTo(false);

        AuthClient authClient = new AuthClient();
        authClient.setClientName("account");
        authClient.setClientSecret("secret");

        when(clientService.getByClientId(any())).thenReturn(authClient);

        when(repudiatedTokenRepository.getById(any())).thenReturn(Optional.empty());
        assertThat(authTokenService.isRepudiated(token)).isEqualTo(false);

        RepudiatedToken repudiatedToken = new RepudiatedToken();
        repudiatedToken.setId("a");

        when(repudiatedTokenRepository.getById(any())).thenReturn(Optional.of(repudiatedToken));
        assertThat(authTokenService.isRepudiated(token)).isEqualTo(true);

    }

    @Test
    public void repudiate(){
        AuthClient authClient = new AuthClient();
        authClient.setClientName("account");
        authClient.setClientSecret("secret");

        String token = TestUtilities.generateToken();
        when(clientService.getByClientId(any())).thenReturn(authClient);

        authTokenService.repudiateToken(token);
    }

    @Test
    public void doConnect(){
        DTOAuthToken dtoAuthToken = new DTOAuthToken();

        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);
        dtoAuthToken.setClientId("out");
        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);
        dtoAuthToken.setPassword("MTXtxJAB9LevJ5VCtORwJgbU");
        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);
        dtoAuthToken.setUsername ("username");
        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);
        when(userService.getByUsername(any())).thenReturn(null);
        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);

        AuthUser authUser = new AuthUser();
        authUser.setAdmin(false);
        authUser.setId("id");
        authUser.setPassword("$2a$10$NaFTXfoR8FS6whikMG/Vs.gtdWn2FT6qm2nAsVxSl..WHXJaL3BvW");
        authUser.setUsername("username");

        when(userService.getByUsername(any())).thenReturn(authUser);
        when(clientService.getByClientId(any())).thenReturn(null);
        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);

        AuthClient authClient = new AuthClient();
        authClient.setClientSecret("secret");
        authClient.setClientName("account");
        authClient.setClientId("id");

        when(clientService.getByClientId(any())).thenReturn(authClient);

        authTokenService.doConnect(dtoAuthToken);

        authUser.setPassword("uhsia");
        assertThat(authTokenService.doConnect(dtoAuthToken)).isEqualTo(null);
    }

}