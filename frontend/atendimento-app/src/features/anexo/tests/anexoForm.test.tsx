import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import AnexoForm from "../components/anexoForm";
import React from "react";
import { toast } from "sonner";

jest.mock("react-pdf", () => ({
  Document: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="pdf-doc">{children}</div>
  ),
  Page: () => <div data-testid="pdf-page" />,
}));

jest.mock("../../../utils/renderizarFormatoArquivo", () => ({
  renderizarFormatoArquivo: () => null,
}));

beforeAll(() => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (global as any).URL.createObjectURL = jest.fn(() => "blob:mock");
});

jest.mock("sonner", () => ({ toast: { error: jest.fn() } }));
const mockOnSubmit = jest.fn();

const renderForm = () => render(<AnexoForm onSubmit={mockOnSubmit} />);

const setArquivo = (file: File) => {
  const input = screen.getByLabelText(/inserir arquivo/i, {
    selector: "input",
  }) as HTMLInputElement;
  fireEvent.change(input, { target: { files: [file] } });
};

describe("AnexoForm - cenários negativos", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não envia com título só espaços", async () => {
    renderForm();
    setArquivo(new File(["abc"], "ok.pdf", { type: "application/pdf" }));

    fireEvent.change(screen.getByPlaceholderText(/título do anexo/i), {
      target: { value: "   " },
    });
    fireEvent.change(screen.getByPlaceholderText(/descrição do anexo/i), {
      target: { value: "   " },
    });

    const btn = screen.getByRole("button", { name: /adicionar anexo/i });
    expect(btn).toBeDisabled();
    expect(toast.error).not.toHaveBeenCalled();
    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  test("bloqueia data futura", async () => {
    renderForm();
    setArquivo(new File(["abc"], "ok.pdf", { type: "application/pdf" }));

    fireEvent.change(screen.getByLabelText(/data/i), {
      target: { value: "2999-01-01" },
    });
    fireEvent.change(screen.getByPlaceholderText(/título do anexo/i), {
      target: { value: "Titulo válido" },
    });
    fireEvent.change(screen.getByPlaceholderText(/descrição do anexo/i), {
      target: { value: "Descrição válida" },
    });

    fireEvent.click(screen.getByRole("button", { name: /adicionar anexo/i }));

    await waitFor(() => expect(toast.error).toHaveBeenCalled());
    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  test("bloqueia arquivo mp3", async () => {
    renderForm();

    fireEvent.change(screen.getByLabelText(/data/i), {
      target: { value: "2024-01-01" },
    });
    fireEvent.change(screen.getByPlaceholderText(/título do anexo/i), {
      target: { value: "Titulo válido" },
    });
    fireEvent.change(screen.getByPlaceholderText(/descrição do anexo/i), {
      target: { value: "Descrição válida" },
    });

    setArquivo(new File(["audio"], "musica.mp3", { type: "audio/mpeg" }));

    fireEvent.click(screen.getByRole("button", { name: /adicionar anexo/i }));

    await waitFor(() =>
      expect(toast.error).toHaveBeenCalledWith(
        expect.stringMatching(/pdf|imagem/i),
      ),
    );
    expect(mockOnSubmit).not.toHaveBeenCalled();
  });
});

