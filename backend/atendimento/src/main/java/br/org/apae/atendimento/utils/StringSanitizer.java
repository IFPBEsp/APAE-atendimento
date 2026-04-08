package br.org.apae.atendimento.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class StringSanitizer {

    private StringSanitizer() {}

    public static String normalizeSpaces(String s) {
        return normalize(s);
    }

    public static String stripHtml(String s) {
        return sanitize(s);
    }

    public static String canonical(String s) {
        return canonicalize(normalizeSpaces(stripHtml(s)));
    }

    public static String normalize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("\\s{2,}", " ");
    }

    public static String sanitize(String s) {
        if (s == null) return null;
        Document.OutputSettings settings = new Document.OutputSettings()
                .charset(StandardCharsets.UTF_8)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        return Jsoup.clean(s, "", Safelist.none(), settings);
    }

    public static String canonicalize(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.ROOT);
    }

    public static String sanitizeFilename(String name) {
        if (name == null) return null;
        return name.replaceAll("[^\\p{L}0-9._-]", "_");
    }
}
