# Documentação do Sistema de Atendimento da APAE

Uma aplicação web e móvel responsiva que permite aos profissionais de saúde da APAE gerenciarem os prontuários dos pacientes, incluindo consultas, relatórios e anexos.

---

## Pacientes

O especialista visualiza **a lista de pacientes pelos quais é responsável**, contendo:

- Nome do paciente
- Condição (intelectual e múltipla)

### Funcionalidades

- **Consultar prontuário** (`Ver detalhes`):
    
    Permite acessar informações detalhadas do paciente.
    
- **Adicionar consultas**:
    - Registrar atendimento do paciente
    - Informar **data da consulta**
    - Adicionar **descrição completa do atendimento**
- **Adicionar anexos**:
    - Incluir documentos importantes, como **receitas, laudos e outros documentos surgidos durante as consultas**
- **Produzir relatórios**:
    - Devem conter:
        - **Tipo**: classificação do relatório (ex.: relatório anual, relatório por demanda)
        - **Descrição**: informações sobre a evolução do paciente 
    - Devem permitir:
        - Acompanhamento: permite acompanhar o histórico de atendimentos
        - Encaminhamento: botão para finalizar e encaminhar à diretoria
- **Pesquisar pacientes**: localizar pelo **nome**
- **Navegar entre páginas** da lista de pacientes

### Observações

- Cada especialista **só vê pacientes pelos quais é responsável**, garantindo **privacidade e segurança**

---

## Meus Dados

Permite ao profissional visualizar seus **dados pessoais cadastrados no sistema**

---

> Informações detalhadas, consultar a documentação [historia dos usuarios](https://github.com/IFPBEsp/APAE-atendimento/blob/main/docs/historia.md)

---

## Stack Tecnológico

### Frontend

**🧩 Tecnologias**

[Tecnologias-Frontend](https://github.com/IFPBEsp/APAE-atendimento/tree/dev/frontend#-tecnologias)

### Backend

**🧩 Tecnologias**

[Tecnologias-Backend](https://github.com/IFPBEsp/APAE-atendimento/tree/dev/backend#-pré-requisitos)

## 
