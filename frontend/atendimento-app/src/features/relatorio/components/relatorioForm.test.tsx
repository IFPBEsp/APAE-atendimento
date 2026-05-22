import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import "@testing-library/jest-dom";
import RelatorioForm from "@/features/relatorio/components/relatorioForm";
import { PacientePdfDTO, ProfissionalPdfDTO } from "@/features/relatorio/types";
import { toast } from "sonner";

jest.mock("@react-pdf/renderer", () => ({
  pdf: () => ({
    toBlob: () =>
        Promise.resolve(new Blob(["mock pdf"], { type: "application/pdf" })),
  }),
  Document: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  Page: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  Text: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  View: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  Image: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  StyleSheet: { create: (styles: object) => styles },
  Font: { register: () => {} },
}));

jest.mock("sonner", () => ({
  toast: {
    error: jest.fn(),
    success: jest.fn(),
  },
}));

if (typeof window.URL.createObjectURL === "undefined") {
  window.URL.createObjectURL = jest.fn(() => "mocked-url");
}

if (typeof window.URL.revokeObjectURL === "undefined") {
  window.URL.revokeObjectURL = jest.fn();
}

describe("RelatorioForm - Validações e Cénarios Negativos", () => {
  const mockOnSubmit = jest.fn();
  const mockDadosPdf = {
    paciente: {
      nome: "Paciente Teste",
      dataNascimento: "01/01/2000",
      endereco: "Rua Teste",
      responsaveis: ["Responsavel 1"],
    } as PacientePdfDTO,
    profissional: {
      nome: "Profissional Teste",
    } as ProfissionalPdfDTO,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("deve bloquear a submissão com data futura ou fora do limite institucional (30 anos)", async () => {
    render(
        <RelatorioForm
            onSubmit={mockOnSubmit}
            dadosPdf={mockDadosPdf}
            carregandoPdf={false}
        />,
    );
    const user = userEvent.setup();

    const inputData = screen.getByLabelText(/Data/i);
    const btnSubmit = screen.getByRole("button", {
      name: /Adicionar Relatório/i,
    });

    const dataFutura = new Date();
    dataFutura.setFullYear(dataFutura.getFullYear() + 1);

    fireEvent.change(inputData, {
      target: { value: dataFutura.toISOString().split("T")[0] },
    });

    await user.type(
        screen.getByPlaceholderText(/Insira o título/i),
        "Relatório Válido",
    );
    await user.type(
        screen.getByPlaceholderText(/Insira a descrição/i),
        "Descrição Válida",
    );

    const inputFile = screen.getByTestId("arquivo-input");
    const file = new File(["dummy"], "teste.pdf", { type: "application/pdf" });
    fireEvent.change(inputFile, { target: { files: [file] } });

    fireEvent.submit(btnSubmit.closest("form")!);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalled();
      expect(mockOnSubmit).not.toHaveBeenCalled();
    });
  });

  it("deve rejeitar submissão com título contendo espaços ou caracteres inválidos", async () => {
    render(
        <RelatorioForm
            onSubmit={mockOnSubmit}
            dadosPdf={mockDadosPdf}
            carregandoPdf={false}
        />,
    );
    const user = userEvent.setup();

    const inputTitulo = screen.getByPlaceholderText(/Insira o título/i);
    const form = screen
        .getByRole("button", { name: /Adicionar Relatório/i })
        .closest("form")!;

    await user.type(inputTitulo, "     ");
    fireEvent.submit(form);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalled();
    });

    await user.clear(inputTitulo);
    await user.type(inputTitulo, "Relatório 👽 @@@");
    fireEvent.submit(form);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalled();
      expect(mockOnSubmit).not.toHaveBeenCalled();
    });
  });

  it("deve bloquear the upload de arquivos com extensões não autorizadas (.exe, .zip)", async () => {
    render(
        <RelatorioForm
            onSubmit={mockOnSubmit}
            dadosPdf={mockDadosPdf}
            carregandoPdf={false}
        />,
    );

    const inputFile = screen.getByTestId("arquivo-input");
    const arquivoInvalido = new File(["virus"], "malicioso.exe", {
      type: "application/x-msdownload",
    });

    fireEvent.change(inputFile, { target: { files: [arquivoInvalido] } });

    await waitFor(async () => {
      expect(
          await screen.findByText(/Formato não suportado/i),
      ).toBeInTheDocument();
    });
  });
});