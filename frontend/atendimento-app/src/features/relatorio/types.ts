import {PacientePdfDTO, ProfissionalPdfDTO } from "@/api/dadosRelatorioPdf";


export interface Relatorio extends RelatorioBase{
  id: number;
}

export interface RelatorioBase{
  objectName: string;
  presignedUrl: string;
  data: string;               
  nomeArquivo: string;
  titulo: string;
  descricao: string;
}

export type RelatorioResponse = RelatorioBase


export type RelatorioPDFData = {
  paciente: PacientePdfDTO;
  profissional: ProfissionalPdfDTO;
};
