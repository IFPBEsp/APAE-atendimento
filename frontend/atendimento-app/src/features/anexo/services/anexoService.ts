import { api } from "@/services/axios";
import { AxiosError } from "axios";
import type { Anexo } from "../types";
import {
  getArquivos,
  getArquivosPorData,
} from "@/features/arquivo/services/arquivoService";
import { TipoArquivo } from "@/features/arquivo/types";
import type { ArquivosPaginados } from "@/features/arquivo/types";

export async function enviarAnexo(formData: FormData) {
  try {
    await api.post("/arquivo", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  } catch (error) {
    const err = error as AxiosError<{ message?: string; error?: string }>;

    const message =
      err.response?.data?.message ||
      err.response?.data?.error ||
      JSON.stringify(err.response?.data) ||
      "Erro ao enviar anexo";

    throw new Error(message);
  }
}

export async function getAnexos(
  pacienteId: string,
  page = 1,
  limit = 10,
): Promise<ArquivosPaginados<Anexo>> {
  return getArquivos<Anexo>(pacienteId, TipoArquivo.anexo, page, limit);
}

export async function getAnexosPorData(
  pacienteId: string,
  data: string,
): Promise<Anexo[]> {
  return getArquivosPorData<Anexo>(pacienteId, TipoArquivo.anexo, data);
}

export async function apagarAnexo(objectName: string): Promise<void> {
  await api.delete(`/arquivo/delete`, {
    params: { objectName },
  });
}