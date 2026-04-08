package br.org.apae.atendimento.utils;

import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class StringSanitizer {

    private StringSanitizer() {} 

    public static String normalizeSpaces(String s) {
        return s == null ? null : s.trim().replaceAll("\\s+", " ");
    }

    public static String stripHtml(String s) {
        return s == null ? null : Jsoup.clean(s, Safelist.none());
    }

    public static String canonical(String s) {
        return s == null ? null : stripHtml(normalizeSpaces(s)).toLowerCase(Locale.ROOT);
    }

    public static String sanitizeFilename(String name) {
        return name == null ? null : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
