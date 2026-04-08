package br.org.apae.atendimento.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StringSanitizerTest {

    @Test
    void sanitizeRemoveHtml() {
        assertThat(StringSanitizer.sanitize("<b>Oi</b><script>x</script>"))
                .isEqualTo("Oi");
    }

    @Test
    void sanitizeFilenameSubstituiCaracteres() {
        assertThat(StringSanitizer.sanitizeFilename("relatório #1?.pdf"))
                .isEqualTo("relatório__1_.pdf".replace("ã","ã"));
    }

    @Test
    void normalizeECanonicalize() {
        String s = "  Olá   Mundo ";
        String normalized = StringSanitizer.normalize(s);      // "Olá Mundo"
        String canonical = StringSanitizer.canonicalize(normalized); // "olá mundo"
        assertThat(normalized).isEqualTo("Olá Mundo");
        assertThat(canonical).isEqualTo("olá mundo");
    }
}
