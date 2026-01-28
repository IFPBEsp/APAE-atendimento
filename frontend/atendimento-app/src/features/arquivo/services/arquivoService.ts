import { api } from "@/services/axios";
import { AxiosError } from "axios";
import { ArquivoResponse, TipoArquivo } from "../types";

// Adicionamos idProfissional como primeiro parâmetro
export async function getArquivos(
  idProfissional: string,
  idPaciente: string,
  tipoArquivo: TipoArquivo,
): Promise<ArquivoResponse[]> {
  try {
    const { data } = await api.get<ArquivoResponse[]>(
      `/arquivo/${idProfissional}/${idPaciente}/${tipoArquivo}`,
    );
    return data;
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao buscar arquivo",
    );
    throw new Error(mensagem);
  }
}

export async function enviarArquivo(relatorioEnvio: FormData) {
  try {
    const { data } = await api.post(`/arquivo`, relatorioEnvio);
    return data;
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao enviar arquivo",
    );
    throw new Error(mensagem);
  }
}