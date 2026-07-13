package com.eleuterio.abarrotes.util;

import java.text.Normalizer;

public final class TextUtils {

    private TextUtils() {
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }
}
