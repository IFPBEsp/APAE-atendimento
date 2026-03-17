package br.org.apae.atendimento.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste-toast")
public class TesteErroController {

    @GetMapping("/erro-500")
    public void forcarErro500() {
        // Exatamente o que a issue pede para testar!
        throw new RuntimeException("Erro 500 forçado para teste do Toast da Issue!");
    }
}