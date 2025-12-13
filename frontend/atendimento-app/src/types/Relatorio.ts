export interface Relatorio extends RelatorioBase{
  id: number;
}

export interface RelatorioBase{
  objectName: string;
  presignedUrl: string;
  data: string;               
  nomeArquivo: string;
  titulo: string;
  descricao: string;
}

export type RelatorioResponse = RelatorioBase


