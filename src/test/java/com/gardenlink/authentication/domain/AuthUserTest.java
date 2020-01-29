package com.gardenlink.authentication.domain;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AuthUserTest {

    @Test
    public void testAuthUser(){

        AuthUser authUser = new AuthUser();
        authUser.setId("a");
        authUser.setFirstName("Flo");
        authUser.setLastName("For");
        authUser.setUsername("art");
        authUser.setAdmin(false);
        authUser.setEmail("test");
        authUser.setPassword("coucou");
        authUser.setAvatar("no");
        authUser.setPhone("0000");
        authUser.setResetToken("a");

        assertThat(authUser.getId()).isEqualTo("a");
        assertThat(authUser.getFirstName()).isEqualTo("Flo");
        assertThat(authUser.getLastName()).isEqualTo("For");
        assertThat(authUser.getUsername()).isEqualTo("art");
        assertThat(authUser.getAdmin()).isEqualTo(false);
        assertThat(authUser.getEmail()).isEqualTo("test");
        assertThat(authUser.getPassword()).isEqualTo("coucou");
        assertThat(authUser.getAvatar()).isEqualTo("no");
        assertThat(authUser.getPhone()).isEqualTo("0000");
        assertThat(authUser.getResetToken()).isEqualTo("a");

    }

}
