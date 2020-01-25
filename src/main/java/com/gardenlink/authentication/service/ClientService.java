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
        if(dtoAuthClient.clientId==null || dtoAuthClient.clientId.isEmpty()) {
            return null;
        }
        if(dtoAuthClient.clientName==null || dtoAuthClient.clientName.isEmpty()) {
            return null;
        }
        if(dtoAuthClient.clientBaseURL==null || dtoAuthClient.clientBaseURL.isEmpty()) {
            return null;
        }

        if(getByClientId(dtoAuthClient.clientId) != null){
            return null;
        }

        //Everything is ok, generating.
        AuthClient authClient = new AuthClient();
        authClient.setClientSecret(RandomString.make(24));
        authClient.setClientBaseURL(dtoAuthClient.clientBaseURL);
        authClient.setClientName(dtoAuthClient.clientName);
        authClient.setClientId(dtoAuthClient.clientId);

        return clientRepository.save(authClient);
    }
}
