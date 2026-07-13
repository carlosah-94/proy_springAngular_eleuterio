package com.eleuterio.abarrotes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String nombre;
    private String email;
    private String rol;
}
