package com.funnyproject.todolistuserapi.user;

import com.funnyproject.todolistuserapi.AppConfig;
import com.funnyproject.todolistuserapi.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RetrieveUserTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RetrieveUser retrieveUser;

    @Test
    public void requestWithValidBearerToken() throws Exception {
        when(retrieveUser.retrieveUser(Mockito.anyString(), Mockito.any(HttpServletRequest.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));  // Adjust the response as needed

        mvc.perform(MockMvcRequestBuilders.get("/users/me")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk());
    }

}
