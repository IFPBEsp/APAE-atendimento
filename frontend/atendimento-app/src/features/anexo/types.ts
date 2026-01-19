import { DocumentoFormData, DocumentoFormDataEnvio, TipoArquivo } from "../arquivo/types";

export interface Anexo extends AnexoBase{
  id: number;  
}

export interface AnexoBase {
  objectName: string;
  presignedUrl: string;
  data: string;               
  nomeArquivo: string;
  titulo: string;
  descricao: string;
}
export type AnexoResponse = AnexoBase;


interface AnexoFormProps {
  onSubmit: (data: AnexoEnvioFormData) => void;
}

export type AnexoEnvioFormData = DocumentoFormData & DocumentoFormDataEnvio & {
  tipoArquivo: TipoArquivo.anexo;
}
