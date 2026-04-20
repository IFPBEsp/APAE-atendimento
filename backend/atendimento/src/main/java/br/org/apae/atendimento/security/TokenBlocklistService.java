package br.org.apae.atendimento.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlocklistService {

    private final Map<String, Instant> revogados = new ConcurrentHashMap<>();

    public void revogar(String jti, Date expiracaoToken) {
        if (jti == null || jti.isBlank() || expiracaoToken == null) return;
        revogados.put(jti, expiracaoToken.toInstant());
    }

    public boolean estaRevogado(String jti) {
        if (jti == null || jti.isBlank()) return false;

        Instant exp = revogados.get(jti);
        if (exp == null) return false;

        if (exp.isBefore(Instant.now())) {
            revogados.remove(jti);
            return false;
        }
        return true;
    }
}
