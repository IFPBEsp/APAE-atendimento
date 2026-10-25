import { PacientePdfDTO, ProfissionalPdfDTO, RelatorioBase } from "@/features/relatorio/types";
import {
  enviarArquivo,
  getArquivos,
  getArquivosPorData,
} from "@/features/arquivo/services/arquivoService";
import { AxiosError } from "axios";
import { TipoArquivo } from "@/features/arquivo/types";
import type { ArquivosPaginados } from "@/features/arquivo/types";
import { api } from "@/services/axios";

import { formatarDataBr } from "@/features/relatorio/utils/formatarData";

export async function getRelatorios(
  pacienteId: string,
  page = 1,
  limit = 10,
): Promise<ArquivosPaginados<RelatorioBase>> {
  try {
    if (!pacienteId) {
      return {
        data: [],
        pagination: {
          page: 1,
          limit,
          totalItems: 0,
          totalPages: 0,
          first: true,
          last: true,
        },
      };
    }
    return await getArquivos<RelatorioBase>(
      pacienteId,
      TipoArquivo.relatorio,
      page,
      limit,
    );
  } catch (error) {
    const axiosError = error as AxiosError;
    const mensagem = String(
      axiosError.response?.data ||
        axiosError.message ||
        "Erro ao buscar relatórios",
    );
    throw new Error(mensagem);
  }
}

export async function getRelatoriosPorData(
  pacienteId: string,
  data: string,
): Promise<RelatorioBase[]> {
  return getArquivosPorData<RelatorioBase>(
    pacienteId,
    TipoArquivo.relatorio,
    data,
  );
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
        "Erro ao enviar relatório",
    );

    throw new Error(mensagem);
  }
}

export async function buscarPacienteParaPdf(
  pacienteId: string
): Promise<PacientePdfDTO> {

  const {data: {
    nomeCompleto,
    dataDeNascimento,
    endereco,
    responsaveis
  }} = await api.get(`/pacientes/${pacienteId}`);

  return {
    nome: nomeCompleto,
    dataNascimento: formatarDataBr(dataDeNascimento),
    endereco,
    responsaveis
  };

}

export async function buscarProfissionalParaPdf(): Promise<ProfissionalPdfDTO> {

  const {data: {
    nomeCompleto
  }} = await api.get(`/profissionais`);

  return {
    nome: nomeCompleto
  };
}