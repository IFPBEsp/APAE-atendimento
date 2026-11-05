import { api } from "@/services/axios";

import { AxiosError, isAxiosError } from "axios";
import { ArquivosPaginados, TipoArquivo } from "../types";
import { AnexoResponse } from "@/features/anexo/types";
import { RelatorioBase } from "@/features/relatorio/types";

type ArquivoResponse = AnexoResponse | RelatorioBase;

type ArquivoPageApiResponse<T> = {
  data: T[];
  paginationMetaDTO: {
    page: number;
    limit: number;
    totalItems: number;
    totalPages: number;
    hasNextPage: boolean;
    hasPreviousPage: boolean;
  };
};

export async function getArquivos<T extends ArquivoResponse = ArquivoResponse>(
  idPaciente: string,
  tipoArquivo: TipoArquivo,
  page = 1,
  limit = 10,
): Promise<ArquivosPaginados<T>> {
  try {
    const { data } = await api.get<ArquivoPageApiResponse<T>>(
      `/arquivo/${idPaciente}/${tipoArquivo}`,
      { params: { page: Math.max(0, page - 1), limit } },
    );
    const meta = data.paginationMetaDTO;

    return {
      data: data.data ?? [],
      pagination: {
        page: meta.page + 1,
        limit: meta.limit,
        totalItems: meta.totalItems,
        totalPages: meta.totalPages,
        first: !meta.hasPreviousPage,
        last: !meta.hasNextPage,
      },
    };
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

export async function getArquivosPorData<
  T extends ArquivoResponse = ArquivoResponse,
>(idPaciente: string, tipoArquivo: TipoArquivo, dataIso: string): Promise<T[]> {
  const dataBr = dataIso.split("-").reverse().join("-"); 
  try {
    const { data } = await api.get<T[]>(
      `/arquivo/date/${idPaciente}/${tipoArquivo}/${dataBr}`,
    );
    return Array.isArray(data) ? data : [];
  } catch (error) {
    if (isAxiosError(error) && error.response?.status === 404) return [];
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