export interface Anexo extends AnexoBase{
  id: number;  
  mimetype: string
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


