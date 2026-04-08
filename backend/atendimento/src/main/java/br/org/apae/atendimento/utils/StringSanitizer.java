package br.org.apae.atendimento.utils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public final class StringSanitizer {

    private StringSanitizer() {} 

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
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}