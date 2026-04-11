package br.org.apae.atendimento.security;

import br.org.apae.atendimento.entities.ProfissionalSaude;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.minutes:30}")
    private Long expirationMinutes;

    @PostConstruct
    public void validarChave() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret deve ter no mínimo 256 bits (32 bytes)");
        }
    }

    public String gerarToken(ProfissionalSaude usuario) {
        long tempoExpiracaoMillis = expirationMinutes * 60 * 1000;
        Date agora = new Date();
        Date validade = new Date(agora.getTime() + tempoExpiracaoMillis);

        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("roles", List.of(usuario.getPerfil() != null ? usuario.getPerfil() : "ROLE_PROFISSIONAL"))
                .setIssuedAt(agora)
                .setExpiration(validade)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean tokenValido(String token) {
        try {
            Claims claims = extrairClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extrairSubject(String token) {
        return extrairClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extrairRoles(String token) {
        return (List<String>) extrairClaims(token).get("roles");
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}