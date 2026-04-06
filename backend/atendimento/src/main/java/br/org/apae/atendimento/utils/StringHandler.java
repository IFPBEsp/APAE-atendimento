package br.org.apae.atendimento.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class StringHandler {

    // aplica trim.
    public static String normalizar(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("\\s{2,}", " ");
    }

    // Remove tag html
    public static String sanitizar(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    // lowercase
    public static String canonicalizar(String input) {
        if (input == null) return null;
        return input.toLowerCase();
    }
}
