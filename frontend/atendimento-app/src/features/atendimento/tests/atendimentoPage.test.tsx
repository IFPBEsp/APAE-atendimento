import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AtendimentoPage from "../components/atendimentoPage";
import normalizarAtendimento, {
  carregarAtendimentos,
} from "../utils/normalizarAtendimento";

jest.mock("next/navigation", () => ({
  useRouter: () => ({
    back: jest.fn(),
    push: jest.fn(),
  }),
  useParams: () => ({
    id: "a4000000-0000-4000-8000-000000000001",
  }),
}));

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
});
