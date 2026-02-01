import { api } from "@/services/axios";
import dados from "@/../data/verificacao.json";
import { AxiosError } from "axios";
import { ArquivoResponse, TipoArquivo } from "../types";

export async function getArquivos(
  idPaciente: string,
  tipoArquivo: TipoArquivo,
): Promise<ArquivoResponse[]> {
  try {
    const { data } = await api.get<ArquivoResponse[]>(
      `${dados.urlBase}/arquivo/${idPaciente}/${tipoArquivo}`,
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
    const { data } = await api.post(`${dados.urlBase}/arquivo`, relatorioEnvio);
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
