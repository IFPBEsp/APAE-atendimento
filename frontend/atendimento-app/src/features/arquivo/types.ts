import { AnexoResponse } from "../anexo/types";
import { RelatorioBase } from "../relatorio/types";

export type ArquivoResponse = AnexoResponse | RelatorioBase;

export enum TipoArquivo {
  anexo = 1,
  relatorio = 2,
}

export type DocumentoFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList;
  descricao: string;
};

export type DocumentoFormDataEnvio = {
  pacienteId?: string;
  profissionalId?: string;
};
