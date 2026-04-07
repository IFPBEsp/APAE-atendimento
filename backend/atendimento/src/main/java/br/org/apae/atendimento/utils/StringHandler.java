package br.org.apae.atendimento.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

public class StringHandler {

    // aplica trim.
    public static String normalizar(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("\\s{2,}", " ");
    }

    // Remove tags html mantendo caracteres Unicode intactos
    public static String sanitizar(String input) {
        if (input == null) return null;
        Document.OutputSettings settings = new Document.OutputSettings()
                .charset(StandardCharsets.UTF_8)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        return Jsoup.clean(input, "", Safelist.none(), settings);
    }

    // lowercase preservando acentos
    public static String canonicalizar(String input) {
        if (input == null) return null;
        return input.toLowerCase();
    }
}
