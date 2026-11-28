import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AtendimentoForm from "../components/atendimentoForm";
import { criarAtendimento, editarAtendimento } from "../services/atendimentoService";
import { Atendimento } from "../types";

const mockSearchParams = new URLSearchParams();

jest.mock("next/navigation", () => ({
  useParams: () => ({
    id: "a4000000-0000-4000-8000-000000000001",
  }),
  useSearchParams: () => mockSearchParams,
}));

jest.mock("../services/atendimentoService", () => {
  const actual = jest.requireActual("../services/atendimentoService");
  return {
    ...actual,
    criarAtendimento: jest.fn(),
    editarAtendimento: jest.fn(),
  };
});

const mockedCriarAtendimento =
  criarAtendimento as jest.MockedFunction<typeof criarAtendimento>;
const mockedEditarAtendimento =
  editarAtendimento as jest.MockedFunction<typeof editarAtendimento>;

const PACIENTE_ID = "a4000000-0000-4000-8000-000000000001";

const atendimentoExistente: Atendimento = {
  id: "a9000000-0000-4000-8000-000000000001",
  data: "10/05/2026",
  hora: "09:00",
  numeracao: "1",
  status: false,
  relatorio: [{ titulo: "Condicao antiga", descricao: "Condicao antiga" }],
};

function renderWithQueryClient(ui: React.ReactElement) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>,
  );
}

function renderizarCriacao() {
  return renderWithQueryClient(
    <AtendimentoForm
      pacienteId={PACIENTE_ID}
      pacienteNome="Paciente Ficticio Um"
      onClose={jest.fn()}
    />,
  );
}

describe("AtendimentoForm - criação e edição", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("na criação, deve exibir os botões Adicionar atendimento e Salvar atendimento", () => {
    renderizarCriacao();

    expect(
      screen.getByRole("button", { name: /Adicionar atendimento/i }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /Salvar atendimento/i }),
    ).toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: /Salvar alterações/i }),
    ).not.toBeInTheDocument();
  });

  it("na edição, deve esconder Adicionar atendimento e exibir Salvar alterações", () => {
    renderWithQueryClient(
      <AtendimentoForm
        pacienteId={PACIENTE_ID}
        pacienteNome="Paciente Ficticio Um"
        atendimentoEditavel={atendimentoExistente}
        onClose={jest.fn()}
      />,
    );

    expect(
      screen.queryByRole("button", { name: /Adicionar atendimento/i }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: /Salvar atendimento/i }),
    ).not.toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /Salvar alterações/i }),
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText("campo vazio")).toHaveValue(
      "Condicao antiga",
    );
  });

  it("na edição, deve salvar chamando editarAtendimento com os dados do atendimento", async () => {
    mockedEditarAtendimento.mockResolvedValueOnce({} as never);
    const onClose = jest.fn();

    renderWithQueryClient(
      <AtendimentoForm
        pacienteId={PACIENTE_ID}
        pacienteNome="Paciente Ficticio Um"
        atendimentoEditavel={atendimentoExistente}
        onClose={onClose}
      />,
    );

    fireEvent.click(screen.getByRole("button", { name: /Salvar alterações/i }));

    await waitFor(() =>
      expect(mockedEditarAtendimento).toHaveBeenCalledWith(
        atendimentoExistente.id,
        {
          pacienteId: PACIENTE_ID,
          data: "10-05-2026",
          hora: "09:00",
          numeracao: "1",
          relatorio: [
            { titulo: "Condicao antiga", descricao: "Condicao antiga" },
          ],
        },
      ),
    );
    await waitFor(() => expect(onClose).toHaveBeenCalled());
    expect(mockedCriarAtendimento).not.toHaveBeenCalled();
  });

  it("deve limitar a condição a 100 caracteres e mostrar o contador", () => {
    renderizarCriacao();

    const condicao = screen.getByPlaceholderText("campo vazio") as HTMLInputElement;
    expect(condicao).toHaveAttribute("maxlength", "100");
    expect(screen.getByText("0/100")).toBeInTheDocument();

    fireEvent.change(condicao, { target: { value: "a".repeat(105) } });

    expect(condicao.value).toHaveLength(100);
    expect(screen.getByText("100/100")).toBeInTheDocument();
  });

  it("deve exibir o botão Remover apenas na criação com mais de um atendimento", () => {
    renderizarCriacao();

    expect(
      screen.queryByRole("button", { name: /Remover/i }),
    ).not.toBeInTheDocument();

    fireEvent.click(
      screen.getByRole("button", { name: /Adicionar atendimento/i }),
    );

    expect(screen.getAllByRole("button", { name: /Remover/i })).toHaveLength(2);
  });

  it("deve exibir o nome do paciente uma única vez, mesmo com vários atendimentos", () => {
    renderizarCriacao();

    fireEvent.click(
      screen.getByRole("button", { name: /Adicionar atendimento/i }),
    );

    expect(screen.getAllByTitle("Nome do paciente")).toHaveLength(1);
    expect(screen.getByText("Atendimento #1")).toBeInTheDocument();
    expect(screen.getByText("Atendimento #2")).toBeInTheDocument();
  });
});