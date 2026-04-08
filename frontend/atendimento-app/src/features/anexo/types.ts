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
