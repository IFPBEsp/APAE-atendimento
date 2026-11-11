import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AtendimentoPage from "../components/atendimentoPage";
import AtendimentoForm from "../components/atendimentoForm";
import { criarAtendimento } from "../services/atendimentoService";
import normalizarAtendimento, {
  carregarAtendimentos,
} from "../utils/normalizarAtendimento";

const mockPush = jest.fn();
const mockBack = jest.fn();
const mockReplace = jest.fn();
let mockSearchParams = new URLSearchParams();

jest.mock("next/navigation", () => ({
  useRouter: () => ({
    back: mockBack,
    push: mockPush,
    replace: mockReplace,
  }),
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
  };
});

jest.mock("../../../components/shared/header", () => ({
  __esModule: true,
  default: () => <div data-testid="header" />,
}));

jest.mock("../utils/normalizarAtendimento", () => {
  const actual = jest.requireActual("../utils/normalizarAtendimento");
  return {
    __esModule: true,
    default: actual.default,
    carregarAtendimentos: jest.fn(),
  };
});

const mockedCarregarAtendimentos =
  carregarAtendimentos as jest.MockedFunction<typeof carregarAtendimentos>;
const mockedCriarAtendimento =
  criarAtendimento as jest.MockedFunction<typeof criarAtendimento>;

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

describe("AtendimentoPage - Estados de erro, vazio e nova tentativa", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockSearchParams = new URLSearchParams();
  });

  it("deve exibir estado de erro explícito quando a consulta falhar e permitir tentar novamente", async () => {
    mockedCarregarAtendimentos
      .mockRejectedValueOnce(new Error("HTTP 500 Internal Server Error"))
      .mockResolvedValueOnce({
        paciente: {
          id: "a4000000-0000-4000-8000-000000000001",
          nomeCompleto: "Paciente Ficticio Um",
        } as never,
        atendimentos: [
          {
            id: "a9000000-0000-4000-8000-000000000001",
            data: "10/05/2026",
            hora: "09:00",
            numeracao: "AT-LOCAL-001",
            status: true,
            relatorio: [
              {
                id: "aa000000-0000-4000-8000-000000000001",
                titulo: "Evolucao ficticia",
                descricao:
                  "Registro criado exclusivamente para desenvolvimento local.",
              },
            ],
          },
        ],
        totalPages: 1,
        totalElements: 1,
        currentPage: 0,
        pageSize: 10,
        first: true,
        last: true,
      });

    renderWithQueryClient(<AtendimentoPage />);

    expect(
      await screen.findByText(/Erro ao carregar os atendimentos deste paciente/i),
    ).toBeInTheDocument();

    expect(
      screen.queryByText(/Não existem atendimentos para este paciente/i),
    ).not.toBeInTheDocument();

    const botaoTentarNovamente = screen.getByRole("button", {
      name: /Tentar novamente/i,
    });
    expect(botaoTentarNovamente).toBeInTheDocument();

    fireEvent.click(botaoTentarNovamente);

    await waitFor(() => {
      expect(mockedCarregarAtendimentos).toHaveBeenCalledTimes(2);
    });

    expect(await screen.findByText("Evolucao ficticia")).toBeInTheDocument();
    expect(
      screen.queryByText(/Erro ao carregar os atendimentos deste paciente/i),
    ).not.toBeInTheDocument();
  });

  it("deve exibir mensagem de ausência de atendimentos quando a consulta retornar lista vazia sem erro", async () => {
    mockedCarregarAtendimentos.mockResolvedValueOnce({
      paciente: {
        id: "a4000000-0000-4000-8000-000000000001",
        nomeCompleto: "Paciente Ficticio Um",
      } as never,
      atendimentos: [],
      totalPages: 0,
      totalElements: 0,
      currentPage: 0,
      pageSize: 10,
      first: true,
      last: true,
    });

    renderWithQueryClient(<AtendimentoPage />);

    expect(
      await screen.findByText(/Não existem atendimentos para este paciente/i),
    ).toBeInTheDocument();
    expect(
      screen.queryByText(/Erro ao carregar os atendimentos deste paciente/i),
    ).not.toBeInTheDocument();
  });

  it("normalizarAtendimento deve filtrar elementos nulos em relatorio defensivamente", () => {
    const resultado = normalizarAtendimento({
      id: "a9000000-0000-4000-8000-000000000001",
      data: "10-05-2026",
      hora: "09:00:00",
      numeracao: "1",
      status: true,
      relatorio: [
        null as never,
        {
          id: "aa000000-0000-4000-8000-000000000001",
          titulo: "Evolucao ficticia",
          descricao: "Descricao valida",
        },
      ],
    });

    expect(resultado.relatorio).toHaveLength(1);
    expect(resultado.relatorio[0].titulo).toBe("Evolucao ficticia");
  });

  it("deve vincular o agendamentoId apenas ao primeiro atendimento quando varias linhas forem salvas", async () => {
    mockSearchParams = new URLSearchParams("agendamentoId=agendamento-123");
    mockedCriarAtendimento.mockResolvedValue({} as never);

    const { container } = renderWithQueryClient(
      <AtendimentoForm
        pacienteId="a4000000-0000-4000-8000-000000000001"
        pacienteNome="Paciente Ficticio Um"
        onClose={jest.fn()}
      />,
    );

    fireEvent.click(screen.getByRole("button", { name: /Adicionar atendimento/i }));

    const condicoes = screen.getAllByPlaceholderText("campo vazio");
    const datas = container.querySelectorAll('input[type="date"]');
    const horas = container.querySelectorAll('input[type="time"]');
    expect(condicoes).toHaveLength(2);

    ["Primeira", "Segunda"].forEach((texto, i) => {
      fireEvent.change(condicoes[i], { target: { value: texto } });
      fireEvent.change(datas[i], { target: { value: "2026-05-10" } });
      fireEvent.change(horas[i], { target: { value: i === 0 ? "09:00" : "10:00" } });
    });

    fireEvent.click(screen.getByRole("button", { name: /Salvar atendimento/i }));

    await waitFor(() => expect(mockedCriarAtendimento).toHaveBeenCalledTimes(2));

    const [primeiro] = mockedCriarAtendimento.mock.calls[0];
    const [segundo] = mockedCriarAtendimento.mock.calls[1];
    expect(primeiro.agendamentoId).toBe("agendamento-123");
    expect(segundo).not.toHaveProperty("agendamentoId");
  });

  it("no fluxo de conclusao, fechar o modal sem salvar retorna para a agenda sem nenhuma requisicao", async () => {
    mockSearchParams = new URLSearchParams("agendamentoId=agendamento-123");
    mockedCarregarAtendimentos.mockResolvedValueOnce({
      paciente: {
        id: "a4000000-0000-4000-8000-000000000001",
        nomeCompleto: "Paciente Ficticio Um",
      } as never,
      atendimentos: [],
      totalPages: 0,
      totalElements: 0,
      currentPage: 0,
      pageSize: 10,
      first: true,
      last: true,
    });

    renderWithQueryClient(<AtendimentoPage />);

    const botaoFechar = await screen.findByRole("button", { name: /Fechar/i });
    fireEvent.click(botaoFechar);

    await waitFor(() => expect(mockReplace).toHaveBeenCalledWith("/agenda"));
    expect(mockedCriarAtendimento).not.toHaveBeenCalled();
  });

  it("deve preencher paciente, data e hora do agendamento no AtendimentoForm e manter o agendamentoId mesmo ao ajustar data ou hora", async () => {
    mockSearchParams = new URLSearchParams(
      "agendamentoId=agendamento-123&data=10-05-2026&hora=09:00&paciente=Paciente+Ficticio+Um",
    );
    mockedCriarAtendimento.mockResolvedValueOnce({
      id: "atendimento-1",
      data: "12/05/2026",
      hora: "14:30",
      numeracao: "1",
      status: true,
      relatorio: [{ titulo: "Paciente estavel", descricao: "Paciente estavel" }],
    });

    const onClose = jest.fn();

    const { container } = renderWithQueryClient(
      <AtendimentoForm
        pacienteId="a4000000-0000-4000-8000-000000000001"
        onClose={onClose}
      />,
    );

    const pacienteInput = screen.getByTitle(
      "Nome do paciente",
    ) as HTMLInputElement;
    const dateInput = container.querySelector(
      'input[type="date"]',
    ) as HTMLInputElement;
    const timeInput = container.querySelector(
      'input[type="time"]',
    ) as HTMLInputElement;

    expect(pacienteInput.value).toBe("Paciente Ficticio Um");
    expect(dateInput.value).toBe("2026-05-10");
    expect(timeInput.value).toBe("09:00");

    const condicaoInput = screen.getByPlaceholderText("campo vazio");
    fireEvent.change(condicaoInput, {
      target: { value: "Paciente estavel" },
    });

    fireEvent.change(dateInput, { target: { value: "2026-05-12" } });
    fireEvent.change(timeInput, { target: { value: "14:30" } });

    const botaoSalvar = screen.getByRole("button", {
      name: /Salvar atendimento/i,
    });
    fireEvent.click(botaoSalvar);

    await waitFor(() => {
      expect(mockedCriarAtendimento).toHaveBeenCalledWith({
        pacienteId: "a4000000-0000-4000-8000-000000000001",
        agendamentoId: "agendamento-123",
        data: "12-05-2026",
        hora: "14:30",
        numeracao: "1",
        relatorio: [
          {
            titulo: "Paciente estavel",
            descricao: "Paciente estavel",
          },
        ],
      });
    });

    expect(onClose).toHaveBeenCalled();
  });

  it("deve voltar para a listagem ao clicar em Voltar sem disparar requisicao de criacao de atendimento", async () => {
    mockedCarregarAtendimentos.mockResolvedValueOnce({
      paciente: {
        id: "a4000000-0000-4000-8000-000000000001",
        nomeCompleto: "Paciente Ficticio Um",
      } as never,
      atendimentos: [],
      totalPages: 0,
      totalElements: 0,
      currentPage: 0,
      pageSize: 10,
      first: true,
      last: true,
    });

    renderWithQueryClient(<AtendimentoPage />);

    const botaoVoltar = screen.getByRole("button", { name: /Voltar/i });
    fireEvent.click(botaoVoltar);

    expect(mockBack).toHaveBeenCalledTimes(1);
    expect(mockedCriarAtendimento).not.toHaveBeenCalled();
  });
});
