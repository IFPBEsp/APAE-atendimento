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


