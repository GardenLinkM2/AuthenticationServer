package com.gardenlink.authentication.domain;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RepudiatedTokenTest {

    @Test
    public void testRepudiatedToken(){
        RepudiatedToken repudiatedToken = new RepudiatedToken();
        repudiatedToken.setId("a");
        assertThat(repudiatedToken.getId()).isEqualTo("a");


    }

}
