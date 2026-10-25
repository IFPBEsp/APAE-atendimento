import { api } from "@/services/axios";
import { AxiosError } from "axios";
import { TipoArquivo } from "../types";
import type { Anexo, AnexoPageResponse, AnexosPaginados } from "../types";

export const ANEXOS_POR_PAGINA = 10;

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
  limit = ANEXOS_POR_PAGINA,
): Promise<AnexosPaginados> {
  const { data } = await api.get<AnexoPageResponse>(
    `/arquivo/${pacienteId}/${TipoArquivo.anexo}`,
    { params: { page: Math.max(0, page - 1), limit } },
  );

  return {
    anexos: data?.data ?? [],
    pagination: data?.paginationMetaDTO ?? null,
  };
}

// dataISO no formato yyyy-MM-dd (valor do input type="date")
export async function getAnexosPorData(
  pacienteId: string,
  dataISO: string,
): Promise<Anexo[]> {
  const [ano, mes, dia] = dataISO.split("-");
  const { data } = await api.get<Anexo[]>(
    `/arquivo/date/${pacienteId}/${TipoArquivo.anexo}/${dia}-${mes}-${ano}`,
  );
  return Array.isArray(data) ? data : [];
}

export async function apagarAnexo(objectName: string): Promise<void> {
  await api.delete(`/arquivo/delete`, {
    params: { objectName },
  });
}