import { api } from "@/services/axios";

import { AxiosError } from "axios";
import { TipoArquivo } from "../types";
import { AnexoResponse } from "@/features/anexo/types";
import { RelatorioBase } from "@/features/relatorio/types";

type ArquivoResponse = AnexoResponse | RelatorioBase;

export async function getArquivos(
  idPaciente: string,
  tipoArquivo: TipoArquivo,
): Promise<ArquivoResponse[]> {
  try {
    const { data } = await api.get<any>(
      `/arquivo/${idPaciente}/${tipoArquivo}?limit=100`,
    );
    if (Array.isArray(data)) {
      return data;
    }
    if (data && Array.isArray(data.data)) {
      return data.data;
    }
    return [];
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
