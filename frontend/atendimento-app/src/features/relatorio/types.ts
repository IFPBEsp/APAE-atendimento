import { PacientePdfDTO, ProfissionalPdfDTO } from "@/api/dadosRelatorioPdf";
import {
  DocumentoFormData,
  DocumentoFormDataEnvio,
  TipoArquivo,
} from "../arquivo/types";

export interface Relatorio extends RelatorioBase {
  id: number;
}

export interface RelatorioBase {
  objectName: string;
  presignedUrl: string;
  data: string;
  nomeArquivo: string;
  titulo: string;
  descricao: string;
}

export type RelatorioPDFData = {
  paciente: PacientePdfDTO;
  profissional: ProfissionalPdfDTO;
};

export type RelatorioEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.relatorio;
  };
