package br.org.apae.atendimento.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "firebase.enable", havingValue = "false", matchIfMissing = true)
public class MockAuthenticationFilter extends OncePerRequestFilter {

    @Value("${mock.user.id:44444444-4444-4444-4444-444444444444}")
    private String mockUserId;

    @PostConstruct
    public void init() {
        log.warn("AVISO: Firebase desabilitado. Autenticação Mockada em uso com ID: {}", mockUserId);
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsuarioAutenticado mockUser = new UsuarioAutenticado(
                    UUID.fromString(mockUserId)
            );

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
