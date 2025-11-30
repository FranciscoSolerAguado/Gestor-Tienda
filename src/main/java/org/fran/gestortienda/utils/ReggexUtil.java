package org.fran.gestortienda.utils;

import java.util.regex.Pattern;

public class ReggexUtil {
    // VÁLIDO: usuario@gmail.com
    public static final Pattern GMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");

    // VÁLIDO: 9 dígitos que empiecen por 6, 7, 8 o 9
    public static final Pattern TELEFONO_REGEX = Pattern.compile("^[6789]\\d{8}$");

    //VÁLIDO: "Juan Pérez", "Coca-Cola 500ml" (no vacío, permite letras, números, espacios, guiones)
    public static final Pattern NOMBRE_REGEX = Pattern.compile("^[\\p{L}0-9\\s'-]+$");

    // VÁLIDO: "12.99", "15", "0.50"
    public static final Pattern DECIMAL_REGEX = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
}
