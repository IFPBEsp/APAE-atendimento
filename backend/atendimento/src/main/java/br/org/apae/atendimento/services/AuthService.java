package br.org.apae.atendimento.services;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    public boolean emailExisteNoFirebase(String email) {
        try {
            FirebaseAuth.getInstance().getUserByEmail(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void syncCustomClaims(String firebaseUid, UUID idProfissional) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("idProfissional", idProfissional.toString());

            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);
        } catch (FirebaseAuthException e) {;
        }
    }
}


