package br.org.apae.atendimento.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class AuthService {

    public boolean emailExisteNoFirebase(String email) {
        try {
            FirebaseAuth.getInstance().getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            if ("user-not-found".equals(e.getAuthErrorCode().name().toLowerCase())) {
                return false;
            }
            throw new RuntimeException("Erro ao consultar o Firebase: " + e.getMessage());
        }
    }

    public void syncCustomClaims(String firebaseUid, UUID idProfissional) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("idProfissional", idProfissional.toString());

            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Falha ao sincronizar permissões de acesso: " + e.getMessage());
        }
    }
}


