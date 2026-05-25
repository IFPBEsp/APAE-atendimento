import { api } from "@/services/axios";
import { AxiosError } from "axios";
import type { Anexo } from "../types";

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

export async function getAnexos(pacienteId: string): Promise<Anexo[]> {
  const res = await api.get(`/arquivo/${pacienteId}/1`);
  return res.data;
}

export async function apagarAnexo(objectName: string): Promise<void> {
  await api.delete(`/arquivo/delete`, {
    params: { objectName },
  });
}