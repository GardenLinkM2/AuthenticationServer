package com.gardenlink.authentication.repository;


import com.gardenlink.authentication.domain.AuthClient;

import java.util.Optional;

public interface ClientRepository extends SearchablePagingAndSortingRepository<AuthClient, Long> {
    Optional<AuthClient> getById(String id);

    Optional<AuthClient> getByClientId(String id);


}