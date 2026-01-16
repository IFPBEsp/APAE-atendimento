import { TipoArquivo } from "@/features/anexo/components/anexoForm";
import {
  enviarArquivo,
  getArquivos,
} from "@/features/arquivo/services/arquivoService";
import { AxiosError } from "axios";

import { api } from "@/services/axios";
import data from "@/../data/verificacao.json";
import { Anexo, AnexoResponse } from "../types";

export async function getAnexos(pacienteId: string): Promise<Anexo[]> {
  try {
    if (!pacienteId) return [];
    const resposta = (await getArquivos(
      pacienteId,
      TipoArquivo.anexo
    )) as AnexoResponse[];
    return resposta.map((e: AnexoResponse, i) => ({
      id: ++i,
      ...e,
    }));
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data || axiosError.message || "Erro ao buscar anexos"
    );
    throw new Error(mensagem);
  }
}

export async function enviarAnexo(formData: FormData) {
  try {
    await enviarArquivo(formData);
    return "Anexo enviado com sucesso!";
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data || axiosError.message || "Erro ao enviar anexo"
    );
    throw new Error(mensagem);
  }
}

export async function apagarAnexo(objectName?: string): Promise<void> {
  try {
    if (!objectName) {
      throw new Error("Nome do arquivo não informado");
    }

    await api.delete(`${data.urlBase}/arquivo/delete`, {
      params: { objectName },
    });
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem =
      String(axiosError.response?.data) ||
      axiosError.message ||
      "Erro ao apagar anexo";

    throw new Error(mensagem);
  }
}
