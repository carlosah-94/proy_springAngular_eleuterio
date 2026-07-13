package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.dto.LoginRequest;
import com.eleuterio.abarrotes.dto.LoginResponse;
import com.eleuterio.abarrotes.service.AuthService;
import com.eleuterio.abarrotes.security.CustomUserDetailsService;
import com.eleuterio.abarrotes.security.JwtAuthFilter;
import com.eleuterio.abarrotes.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void loginDebeRetornarToken() throws Exception {
        when(authService.login(any())).thenReturn(
                LoginResponse.builder()
                        .token("jwt-token")
                        .nombre("Don Eleuterio")
                        .email("eleuterio@abarrotes.com")
                        .rol("ROLE_ADMIN")
                        .build()
        );

        LoginRequest request = new LoginRequest();
        request.setEmail("eleuterio@abarrotes.com");
        request.setPassword("Eleuterio2024!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.nombre").value("Don Eleuterio"));
    }

    @Test
    void logoutDebeResponderOk() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sesión cerrada correctamente"));
    }
}
