package br.org.apae.atendimento.security;

import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ProfissionalSaudeRepository profissionalSaudeRepository;
    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
            String firebaseUid = decoded.getUid();
            UUID id;

            String claimId = (String) decoded.getClaims().get("idProfissional");

            if (claimId != null){
                id = UUID.fromString(claimId);
            }
            else {
                id = profissionalSaudeRepository.findIdByFirebaseUID(decoded.getUid());
                if (id != null) {
                    authService.syncCustomClaims(firebaseUid, id);
                } else {
                    throw new Exception("Usuário não encontrado no banco de dados local.");
                }
            }

            UsuarioAutenticado usuarioAtenticado = new UsuarioAutenticado(id);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    usuarioAtenticado,
                    null,
                    List.of()
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }
}