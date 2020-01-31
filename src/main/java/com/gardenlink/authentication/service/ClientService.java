package com.gardenlink.authentication.service;

import com.gardenlink.authentication.domain.AuthClient;
import com.gardenlink.authentication.domain.dto.DTOAuthClient;
import com.gardenlink.authentication.repository.ClientRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public AuthClient regenerateSecret(String id){
        AuthClient authClient = getById(id);
        if(authClient==null) {
            return null;
        }
        authClient.setClientSecret(RandomString.make(24));
        return clientRepository.save(authClient);

    }

    public Page<AuthClient> getClients(Pageable page){
        return clientRepository.findAll(page);
    }

    public AuthClient getById(String id){
        return clientRepository.getById(id).orElse(null);
    }

    public AuthClient getByClientId(String clientId){
        return clientRepository.getByClientId(clientId).orElse(null);
    }

    public void delete(String id){
        AuthClient authClient = getById(id);

        if(authClient!=null){
            clientRepository.delete(authClient);
        }
    }

    public AuthClient create(DTOAuthClient dtoAuthClient){
        if(dtoAuthClient.getClientId()==null || dtoAuthClient.getClientId().isEmpty()) {
            return null;
        }
        if(dtoAuthClient.getClientName()==null || dtoAuthClient.getClientName().isEmpty()) {
            return null;
        }
        if(dtoAuthClient.getClientBaseURL()==null || dtoAuthClient.getClientBaseURL().isEmpty()) {
            return null;
        }

        if(getByClientId(dtoAuthClient.getClientId()) != null){
            return null;
        }

        //Everything is ok, generating.
        AuthClient authClient = new AuthClient();
        authClient.setClientSecret(RandomString.make(24));
        authClient.setClientBaseURL(dtoAuthClient.getClientBaseURL());
        authClient.setClientName(dtoAuthClient.getClientName());
        authClient.setClientId(dtoAuthClient.getClientId());

        return clientRepository.save(authClient);
    }
}
