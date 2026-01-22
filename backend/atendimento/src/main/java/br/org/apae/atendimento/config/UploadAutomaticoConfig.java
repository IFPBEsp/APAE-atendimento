package br.org.apae.atendimento.config;
import java.util.List;
import java.util.Random;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Configuration
public class UploadAutomaticoConfig {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String PACIENTE_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    private static final List<String> IMAGENS = List.of(
        "https://picsum.photos/300",
        "https://picsum.photos/400"
    );

    @Bean
    public ApplicationRunner uploadAutomaticoRunner() {
        return args -> {
            try {
                
                String imageUrl = IMAGENS.get(new Random().nextInt(IMAGENS.size()));

               
                byte[] imagem = restTemplate.getForObject(imageUrl, byte[].class);
                if (imagem == null) return;

                
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

                body.add("foto", new ByteArrayResource(imagem) {
                    @Override
                    public String getFilename() {
                        return "foto.jpg";
                    }
                });

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                HttpEntity<MultiValueMap<String, Object>> request =
                        new HttpEntity<>(body, headers);

                
                restTemplate.postForEntity(
                    "http://localhost:8080/pacientes/" + PACIENTE_ID,
                    request,
                    Void.class
                );

            } catch (Exception e) {
                System.out.println("Erro no upload automático, seguindo fluxo...");
            }
        };
    }
}
