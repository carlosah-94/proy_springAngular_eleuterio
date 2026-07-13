package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.dto.InventarioStatsResponse;
import com.eleuterio.abarrotes.dto.PageResponse;
import com.eleuterio.abarrotes.dto.ProductoResponse;
import com.eleuterio.abarrotes.service.ProductoService;
import com.eleuterio.abarrotes.security.CustomUserDetailsService;
import com.eleuterio.abarrotes.security.JwtAuthFilter;
import com.eleuterio.abarrotes.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductoControllerTest {

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Test
    void statsDebeRetornarResumen() throws Exception {
        when(productoService.stats()).thenReturn(
                InventarioStatsResponse.builder()
                        .totalProductos(10)
                        .valorInventario(new BigDecimal("1500.00"))
                        .stockCritico(2)
                        .totalCategorias(5)
                        .build()
        );

        mockMvc.perform(get("/api/productos/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").value(10))
                .andExpect(jsonPath("$.stockCritico").value(2));
    }

    @Test
    void listarDebeRetornarPagina() throws Exception {
        when(productoService.listar(isNull(), isNull(), eq(1), eq(5))).thenReturn(
                PageResponse.<ProductoResponse>builder()
                        .data(List.of(ProductoResponse.builder().id(1).nombre("Aceite").precio(new BigDecimal("11.60")).build()))
                        .total(1)
                        .build()
        );

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Aceite"));
    }
}
