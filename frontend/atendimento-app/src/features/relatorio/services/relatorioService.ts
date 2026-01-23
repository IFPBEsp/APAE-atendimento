import { RelatorioBase } from "@/features/relatorio/types";
import {
  enviarArquivo,
  getArquivos,
} from "@/features/arquivo/services/arquivoService";
import { AxiosError } from "axios";
import { TipoArquivo } from "@/features/arquivo/types";

export async function getRelatorios(
  pacienteId: string,
): Promise<RelatorioBase[]> {
  try {
    if (!pacienteId) return [];
    const resposta = (await getArquivos(
      pacienteId,
      TipoArquivo.relatorio,
    )) as RelatorioBase[];
    return resposta;
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao buscar relatórios",
    );
    throw new Error(mensagem);
  }
}

export async function enviarRelatorio(formData: FormData) {
  try {
    await enviarArquivo(formData);
    return "Relatório enviado com sucesso!";
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao enviar relatório",
    );

    throw new Error(mensagem);
  }
}
