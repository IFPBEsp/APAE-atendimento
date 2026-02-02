package br.org.apae.atendimento.utils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacienteImagemUtil {
   private static final Map<UUID, String> pacientesImagens = new HashMap<>();

   static {
        pacientesImagens.put(
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            "https://picsum.photos/id/237/300/300" 
        );
    }

     public static String obterImagem(UUID pacienteId) {
        return pacientesImagens.get(pacienteId);
    }

    public static boolean possuiImagem(UUID pacienteId) {
        return pacientesImagens.containsKey(pacienteId);
    }
    
}
