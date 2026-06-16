package br.org.apae.atendimento.services.integration;

import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.services.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AgendamentoExternoClient {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoExternoClient.class);

    private final RestTemplate restTemplate;
    private final PacienteService pacienteService;

    @Value("${api.geral.url:http://localhost:8090/apae-geral/api}")
    private String apiGeralUrl;

    @Value("${api.geral.username:admin@teste.com}")
    private String apiGeralUsername;

    @Value("${api.geral.password:senha123}")
    private String apiGeralPassword;

    public AgendamentoExternoClient(PacienteService pacienteService) {
        this.restTemplate = new RestTemplate();
        this.pacienteService = pacienteService;
    }

    private String obterTokenSistemaGeral() {
        try {
            String loginUrl = apiGeralUrl + "/auth/signin";
            Map<String, String> credenciais = new HashMap<>();
            credenciais.put("username", apiGeralUsername);
            credenciais.put("password", apiGeralPassword);

            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, credenciais, Map.class);
            if (response.getBody() == null) {
                log.warn("Login no sistema geral retornou corpo vazio (url={})", loginUrl);
                return null;
            }
            return (String) response.getBody().get("token");
        } catch (Exception e) {
            log.warn("Erro ao obter token do sistema geral: {}", e.getMessage());
            return null;
        }
    }

    public List<AgendamentoResponseDTO> buscarAgendamentos(UUID profissionalId) {
        String token = obterTokenSistemaGeral();
        if (token == null) return new ArrayList<>();

        try {
            String url = apiGeralUrl + "/appointments/professional/" + profissionalId + "/generated";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() == null) return new ArrayList<>();

            return response.getBody().stream()
                    .filter(map -> !Boolean.TRUE.equals(map.get("cancelled")))
                    .map(this::mapearParaLocalDTO)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Erro ao buscar agendamentos externos para profissional {}: {}", profissionalId, e.getMessage());
            return new ArrayList<>();
        }
    }

    private AgendamentoResponseDTO mapearParaLocalDTO(Map<String, Object> externalData) {
        try {
            String effectiveDateTime = (String) externalData.get("effectiveDateTime");
            if (effectiveDateTime == null || !effectiveDateTime.contains("T")) {
                log.warn("Agendamento externo {} ignorado: effectiveDateTime ausente ou inválido",
                        externalData.get("id"));
                return null;
            }
            String[] partes = effectiveDateTime.split("T");
            LocalDate data = LocalDate.parse(partes[0]);
            LocalTime hora = LocalTime.parse(partes[1]);

            Object patientIdRaw = externalData.get("patientId");
            if (patientIdRaw == null) {
                log.warn("Agendamento externo {} ignorado: patientId ausente", externalData.get("id"));
                return null;
            }
            UUID patientId = UUID.fromString((String) patientIdRaw);

            String nomePaciente = "Paciente (Sistema Geral)";
            try {
                nomePaciente = pacienteService.getNomeCompletoPacienteById(patientId);
            } catch (Exception ignored) {
                // paciente pode não existir localmente; mantém o rótulo padrão
            }

            return new AgendamentoResponseDTO(
                    UUID.fromString((String) externalData.get("id")),
                    patientId,
                    nomePaciente,
                    data,
                    hora,
                    0L,
                    Boolean.TRUE.equals(externalData.get("performed")),
                    true
            );
        } catch (Exception e) {
            log.warn("Erro ao mapear agendamento externo {}: {}", externalData.get("id"), e.getMessage());
            return null;
        }
    }
}
