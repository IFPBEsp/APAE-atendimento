package br.org.apae.atendimento.security;

import java.util.UUID;

public class UsuarioAutenticado {
    private final UUID id;

    public UsuarioAutenticado(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
