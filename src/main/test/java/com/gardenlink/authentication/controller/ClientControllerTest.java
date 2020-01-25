package com.gardenlink.authentication.controller;


import com.gardenlink.authentication.TestUtilities;
import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.dto.DTOAuthClient;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import com.gardenlink.authentication.service.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.http.*;
import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {
    @Mock
    ClientService clientService;

    @Mock
    AuthTokenService authTokenService;

    @InjectMocks
    ClientController clientController;


    @Test
    public void testGetAllClients() {
        AuthClient clientA = new AuthClient();
        clientA.setClientId("clientA");
        clientA.setClientSecret("secretA");
        clientA.setClientName("nameA");
        clientA.setClientBaseURL("http://a/");

        AuthClient clientB = new AuthClient();
        clientB.setClientId("clientB");
        clientB.setClientSecret("secretB");
        clientB.setClientName("nameB");
        clientB.setClientBaseURL("http://b/");

        ArrayList<AuthClient> clients = new ArrayList<>();
        clients.add(clientA);
        clients.add(clientB);

        when(clientService.getClients(any())).thenReturn(new PageImpl<>(clients));
        assertThat(clientController.getClients(PageRequest.of(0,10)).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(clientController.getClients(PageRequest.of(0, 10)).getBody()).getTotalElements()).isEqualTo(2);

        clients = new ArrayList<>();
        when(clientService.getClients(any())).thenReturn(new PageImpl<>(clients));
        assertThat(clientController.getClients(PageRequest.of(0,10)).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void testGetClient(){
        AuthClient clientA = new AuthClient();
        clientA.setClientId("clientA");
        clientA.setClientSecret("secretA");
        clientA.setClientName("nameA");
        clientA.setClientBaseURL("http://a/");

        when(clientService.getById(any())).thenReturn(clientA);
        assertThat(clientController.getClientInfo("id").getStatusCode()).isEqualTo(HttpStatus.OK);

        when(clientService.getById(any())).thenReturn(null);
        assertThat(clientController.getClientInfo("id").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createClient(){
        HttpServletRequest request = TestUtilities.getHttpRequest();

        when(authTokenService.introspect(any())).thenReturn(null);
        assertThat(clientController.createClient(new DTOAuthClient(), UriComponentsBuilder.newInstance(), request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        DTOTokenInformation token = new DTOTokenInformation();
        token.isAdmin=false;
        token.uuid="coucou";
        token.emitter="account";
        when(authTokenService.introspect(any())).thenReturn(token);
        assertThat(clientController.createClient(new DTOAuthClient(), UriComponentsBuilder.newInstance(), request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        AuthClient authClient = new AuthClient();
        authClient.setId(UUID.randomUUID().toString());
        authClient.setClientSecret("a");

        token.isAdmin=true;
        when(clientService.create(any())).thenReturn(authClient);
        assertThat(clientController.createClient(new DTOAuthClient(), UriComponentsBuilder.newInstance(), request).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        when(clientService.create(any())).thenReturn(null);
        assertThat(clientController.createClient(new DTOAuthClient(), UriComponentsBuilder.newInstance(), request).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteClient(){

        HttpServletRequest request = TestUtilities.getHttpRequest();

        when(authTokenService.introspect(any())).thenReturn(null);
        assertThat(clientController.deleteClient("a", request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        DTOTokenInformation token = new DTOTokenInformation();
        token.isAdmin=false;
        token.emitter="account";
        token.uuid="coucou";
        when(authTokenService.introspect(any())).thenReturn(token);
        assertThat(clientController.deleteClient("a", request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        token.isAdmin=true;

        doNothing().when(clientService).delete(any());
        assertThat(clientController.deleteClient("a", request).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void regenerateToken(){
        HttpServletRequest request = TestUtilities.getHttpRequest();

        when(authTokenService.introspect(any())).thenReturn(null);
        assertThat(clientController.regenerateSecret("a", request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        DTOTokenInformation token = new DTOTokenInformation();
        token.isAdmin=false;
        token.uuid="coucou";
        token.emitter="account";
        when(authTokenService.introspect(any())).thenReturn(token);
        assertThat(clientController.regenerateSecret("a", request).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        token.isAdmin=true;
        when(clientService.regenerateSecret(any())).thenReturn(null);
        assertThat(clientController.regenerateSecret("a", request).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AuthClient authClient = new AuthClient();
        authClient.setClientSecret("b");

        when(clientService.regenerateSecret(any())).thenReturn(authClient);
        assertThat(clientController.regenerateSecret("a", request).getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}