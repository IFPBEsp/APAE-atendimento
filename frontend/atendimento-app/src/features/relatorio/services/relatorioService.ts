import { Relatorio, RelatorioResponse } from "@/features/relatorio/types";
import { TipoArquivo } from "@/features/anexo/components/anexoForm";
import {
  enviarArquivo,
  getArquivos,
} from "@/features/arquivo/services/arquivoService";
import { AxiosError } from "axios";

export async function getRelatorios(
  pacienteId: string
): Promise<RelatorioResponse[]> {
  try {
    if (!pacienteId) return [];
    const resposta = (await getArquivos(
      pacienteId,
      TipoArquivo.relatorio
    )) as RelatorioResponse[];
    return resposta;
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao buscar relatórios"
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
        "Erro ao enviar relatório"
    );

    throw new Error(mensagem);
  }
}
