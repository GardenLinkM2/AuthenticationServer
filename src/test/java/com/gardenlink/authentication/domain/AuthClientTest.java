package com.gardenlink.authentication.domain;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AuthClientTest {

    @Test
    public void testAuthClient(){
        AuthClient authClient = new AuthClient();
        authClient.setId("A");
        authClient.setClientSecret("secret");
        authClient.setClientBaseURL("http://nowhere");
        authClient.setClientName("Test");
        authClient.setClientId("id");

        assertThat(authClient.getClientSecret()).isEqualTo("secret");
        assertThat(authClient.getId()).isEqualTo("A");
        assertThat(authClient.getClientBaseURL()).isEqualTo("http://nowhere");
        assertThat(authClient.getClientName()).isEqualTo("Test");
        assertThat(authClient.getClientId()).isEqualTo("id");

    }

}
