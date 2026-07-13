package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.util.TextUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextUtilsTest {

    @Test
    void normalizarDebeIgnorarTildesYMayusculas() {
        assertEquals("backus", TextUtils.normalizar("BACkús"));
        assertEquals("aceite vegetal", TextUtils.normalizar("  Aceite Vegetal  "));
    }
}
