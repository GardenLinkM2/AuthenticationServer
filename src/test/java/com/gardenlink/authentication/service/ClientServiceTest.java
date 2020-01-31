package com.gardenlink.authentication.service;


import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.dto.DTOAuthClient;
import com.gardenlink.authentication.repository.ClientRepository;
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
public class ClientServiceTest {

    @Mock
    ClientRepository clientRepository;

    @InjectMocks
    ClientService clientService;

    @Test
    public void regenerateSecret(){
        when(clientRepository.getById(any())).thenReturn(Optional.empty());
        assertThat(clientService.regenerateSecret("4")).isEqualTo(null);


        AuthClient authClient = new AuthClient();
        authClient.setClientSecret("a");
        authClient.setClientId("coucou");

        when(clientRepository.save(any())).thenReturn(authClient);

        when(clientRepository.getById(any())).thenReturn(Optional.of(authClient));
        assertThat(clientService.regenerateSecret("4").getClientId()).isEqualTo("coucou");
        assertThat(clientService.regenerateSecret("4").getClientSecret()).isNotEqualTo("a");
    }

    @Test
    public void getClients(){
        List<AuthClient> authClients = new ArrayList<>();

        when(clientRepository.findAll((Pageable) any())).thenReturn(new PageImpl<>(authClients));
        clientService.getClients(PageRequest.of(0,10));
    }

    @Test
    public void getClient(){
        AuthClient authClient = new AuthClient();
        authClient.setClientId("coucou");

        when(clientRepository.getByClientId(any())).thenReturn(Optional.empty());
        assertThat(clientService.getByClientId("4")).isEqualTo(null);

        when(clientRepository.getByClientId(any())).thenReturn(Optional.of(authClient));
        assertThat(clientService.getByClientId("4")).isEqualTo(authClient);
    }

    @Test
    public void delete(){
        clientService.delete("4");
        when(clientRepository.getById(any())).thenReturn(Optional.of(new AuthClient()));
        clientService.delete("4");
    }


    @Test
    public void create(){
        DTOAuthClient dtoAuthClient = new DTOAuthClient();
        assertThat(clientService.create(dtoAuthClient)).isEqualTo(null);
        dtoAuthClient.setClientId("coucou");
        assertThat(clientService.create(dtoAuthClient)).isEqualTo(null);
        dtoAuthClient.setClientName("ah");
        assertThat(clientService.create(dtoAuthClient)).isEqualTo(null);
        dtoAuthClient.setClientBaseURL("ziheu");

        when(clientRepository.getByClientId(any())).thenReturn(Optional.of(new AuthClient()));
        assertThat(clientService.create(dtoAuthClient)).isEqualTo(null);

        when(clientRepository.getByClientId(any())).thenReturn(Optional.empty());

        AuthClient authClient = new AuthClient();

        when(clientRepository.save(any())).thenReturn(authClient);
        assertThat(clientService.create(dtoAuthClient)).isEqualTo(authClient);
    }



}