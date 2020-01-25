package com.gardenlink.authentication.repository;


import com.gardenlink.authentication.domain.RepudiatedToken;

import java.util.Optional;

public interface RepudiatedTokenRepository extends SearchablePagingAndSortingRepository<RepudiatedToken, Long> {
    Optional<RepudiatedToken> getById(String id);


}