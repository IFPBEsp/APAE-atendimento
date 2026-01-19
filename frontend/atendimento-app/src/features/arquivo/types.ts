
import { AnexoResponse } from "../anexo/types";
import { RelatorioResponse } from "../relatorio/types";

export type ArquivoResponse = AnexoResponse | RelatorioResponse;

export enum TipoArquivo {
  anexo = 1,
  relatorio = 2
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
}