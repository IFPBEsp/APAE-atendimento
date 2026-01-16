import { api } from "@/services/axios";
import dados from "@/../data/verificacao.json";
import { AxiosError } from "axios";
import { ArquivoResponse, TipoArquivo } from "../types";

export async function getArquivos(
  idPaciente: string,
  tipoArquivo: TipoArquivo
): Promise<ArquivoResponse[]> {
  try {
    const { data } = await api.get<ArquivoResponse[]>(
      `${dados.urlBase}/arquivo/${dados.idProfissional}/${idPaciente}/${tipoArquivo}`
    );
    return data;
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao buscar arquivo"
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
    console.log(
      axiosError.response?.data,
      axiosError.message,
      axiosError.cause
    );
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao enviar arquivo"
    );
    throw new Error(mensagem);
  }
}

export async function handleDownload(objectName: string): Promise<void> {
  if (!objectName) {
    throw new Error("Nome do arquivo não informado");
  }

  try {
    const response = await api.get<string>(`${dados.urlBase}/pressigned/`, {
      params: { objectName },
      responseType: "text",
    });

    const presignedUrl = response.data;

    if (!presignedUrl) {
      throw new Error("URL de download inválida");
    }

    window.location.href = presignedUrl;
  } catch (error) {
    if (error instanceof AxiosError) {
      const mensagem =
        typeof error.response?.data === "string"
          ? error.response.data
          : error.message;

      throw new Error(mensagem || "Erro ao gerar link de download");
    }

    throw error;
  }
}
