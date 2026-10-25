// 🔹 TIPOS BASE DO FORM
export type DocumentoFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList; // ✅ OPCIONAL (IMPORTANTE)
  descricao: string;
};

export type DocumentoFormDataEnvio = {
  pacienteId?: string;
};

// 🔹 ENUM (UM SÓ, CENTRALIZADO)
export enum TipoArquivo {
  anexo = 1,
  relatorio = 2,
}

// 🔹 TIPOS DE ENVIO
export type AnexoEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.anexo;
  };

export type RelatorioEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.relatorio;
  };

// 🔹 RESPONSE DA API
export interface AnexoBase {
  objectName: string;
  presignedUrl: string;
  data: string;
  nomeArquivo: string;
  titulo: string;
  descricao: string;
}

// 🔹 CASO QUEIRA ESTENDER NO FUTURO
export interface Anexo extends AnexoBase {
  id?: string;
}

export type AnexoResponse = AnexoBase;

// 🔹 PAGINAÇÃO (formato devolvido por GET /arquivo/{pacienteId}/{tipoId})
export interface AnexoPaginationMeta {
  page: number; // começa em 0
  limit: number;
  totalItems: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPreviousPage: boolean;
}

export interface AnexoPageResponse {
  data: Anexo[];
  paginationMetaDTO: AnexoPaginationMeta;
}

export interface AnexosPaginados {
  anexos: Anexo[];
  pagination: AnexoPaginationMeta | null;
}