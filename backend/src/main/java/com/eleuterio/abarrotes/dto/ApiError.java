package com.eleuterio.abarrotes.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ApiError {
    private String error;
    private String message;
}
