package com.eleuterio.abarrotes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertaResponse {
    private String tipo;
    private String titulo;
    private String mensaje;
    private Integer productoId;
}
