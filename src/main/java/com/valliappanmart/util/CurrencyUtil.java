package com.valliappanmart.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    private static final Locale INDIA_LOCALE = new Locale("en", "IN");

    public static String formatINR(BigDecimal amount) {
        if (amount == null) {
            return "₹0";
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(INDIA_LOCALE);
        return formatter.format(amount).replace("INR", "₹").trim();
    }

    public static String formatINR(double amount) {
        return formatINR(BigDecimal.valueOf(amount));
    }
}
