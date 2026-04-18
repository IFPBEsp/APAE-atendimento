package br.org.apae.atendimento.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Profile("test")
@ConditionalOnProperty(name = "auth.mock.enabled", havingValue = "true")
public class MockAuthenticationFilter extends OncePerRequestFilter {

    @Value("${auth.mock.user.id:44444444-4444-4444-4444-444444444444}")
    private String mockUserId;

    @PostConstruct
    public void init() {
        UUID.fromString(mockUserId);
        log.warn("AVISO: Autenticacao mock habilitada no perfil de teste com ID: {}", mockUserId);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsuarioAutenticado mockUser = new UsuarioAutenticado(UUID.fromString(mockUserId));

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            mockUser,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_PROFISSIONAL"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
