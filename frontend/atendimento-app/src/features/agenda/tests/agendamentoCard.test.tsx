import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import AgendamentoCard from "../components/agendamentoCard";
import { api } from "../../../services/axios";

const mockPush = jest.fn();

jest.mock("next/navigation", () => ({
  useRouter: () => ({ push: mockPush, back: jest.fn(), replace: jest.fn() }),
}));

jest.mock("../../../services/axios", () => ({
  api: { get: jest.fn(), post: jest.fn(), put: jest.fn(), patch: jest.fn(), delete: jest.fn() },
}));

const PACIENTE_ID = "a4000000-0000-4000-8000-000000000001";
const AGENDAMENTO_ID = "a8000000-0000-4000-8000-000000000001";

function renderCard(props: Partial<React.ComponentProps<typeof AgendamentoCard>> = {}) {
  return render(
    <AgendamentoCard
      id={AGENDAMENTO_ID}
      pacienteId={PACIENTE_ID}
      paciente="Paciente Ficticio Um"
      horario="09:00"
      numeroAtendimento="1"
      status={false}
      externo={false}
      {...props}
    />,
  );
}

describe("AgendamentoCard - botão Concluir Atendimento", () => {
  beforeEach(() => jest.clearAllMocks());

  it("redireciona para o prontuário com agendamentoId na query, sem chamar a API", () => {
    renderCard();

    fireEvent.click(screen.getByRole("button", { name: /Concluir Atendimento/i }));

    expect(mockPush).toHaveBeenCalledWith(
      `/atendimento/${PACIENTE_ID}?agendamentoId=${AGENDAMENTO_ID}`,
    );
    expect(api.patch).not.toHaveBeenCalled();
    expect(api.post).not.toHaveBeenCalled();
  });

  it("não exibe o botão de concluir quando o agendamento já está concluído", () => {
    renderCard({ status: true });

    expect(screen.queryByRole("button", { name: /Concluir Atendimento/i })).not.toBeInTheDocument();
    expect(screen.getByText(/Atendimento Concluído/i)).toBeInTheDocument();
  });

  it("oculta o botão de concluir para agendamentos externos (Sistema Geral)", () => {
    renderCard({ externo: true });

    expect(
      screen.queryByRole("button", { name: /Concluir Atendimento/i }),
    ).not.toBeInTheDocument();
    expect(mockPush).not.toHaveBeenCalled();
  });
});
