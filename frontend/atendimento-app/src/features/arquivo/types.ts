export enum TipoArquivo {
  anexo = 1,
  relatorio = 2,
}

export type DocumentoFormData = {
  data: string;
  titulo: string;
  arquivo: FileList;
  descricao: string;
};

export type DocumentoFormDataEnvio = {
  pacienteId?: string;
};

export type AnexoEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.anexo;
  };

export type RelatorioEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.relatorio;
  };