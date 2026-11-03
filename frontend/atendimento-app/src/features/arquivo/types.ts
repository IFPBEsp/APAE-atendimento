export type DocumentoFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList; 
  descricao: string;
};

export type DocumentoFormDataEnvio = {
  pacienteId?: string;
};

export enum TipoArquivo {
  anexo = 1,
  relatorio = 2,
}

export type AnexoEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.anexo;
  };

export type RelatorioEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.relatorio;
  };

export interface AnexoBase {
  objectName: string;
  presignedUrl: string;
  data: string;
  nomeArquivo: string;
  titulo: string;
  descricao: string;
}

export interface Anexo extends AnexoBase {
  id?: string;
}

export type AnexoResponse = AnexoBase;

export type ArquivoPagination = {
  page: number;
  limit: number;
  totalItems: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type ArquivosPaginados<T> = {
  data: T[];
  pagination: ArquivoPagination;
};