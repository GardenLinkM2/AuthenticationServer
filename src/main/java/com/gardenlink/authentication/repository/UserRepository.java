package com.gardenlink.authentication.repository;

import com.gardenlink.authentication.domain.AuthUser;

import java.util.Optional;

public interface UserRepository extends SearchablePagingAndSortingRepository<AuthUser, Long> {
    Optional<AuthUser> getById(String id);
    Optional<AuthUser> getByEmail(String email);
    Optional<AuthUser> getByResetToken(String token);
}