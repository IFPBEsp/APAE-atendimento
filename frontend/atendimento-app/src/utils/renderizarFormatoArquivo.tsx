import Image from "next/image";
import { JSX } from "react";
import { FileText } from "lucide-react";

type TipoNormalizado = 'pdf' | 'image';

const TIPOS_PDF = ['application/pdf', 'pdf'];
const TIPOS_IMAGEM = ['image/jpeg', 'image/jpg', 'image/png', 'image/heic', 'image/heif', 'jpeg', 'jpg', 'png', 'heic', 'heif'];

function tratarUrl(url: string): string {
  return url.replace("://minio:9000", "://localhost:9000");
}

function normalizarTipo(tipo: string): TipoNormalizado | null {
  const tipoTratado = tipo.toLowerCase().trim();
  if (TIPOS_PDF.includes(tipoTratado)) return 'pdf';
  if (TIPOS_IMAGEM.includes(tipoTratado)) return 'image';
  return null;
}

export function renderizarFormatoArquivo(
  tipo: string,
  url: string,
  modo: 'thumbnail' | 'full' | 'form' = 'full'
): JSX.Element | null {
  const tipoNormalizado = normalizarTipo(tipo);
  if (!tipoNormalizado) return null;

  const urlTratada = tratarUrl(url);

  if (tipoNormalizado === 'pdf') {
    return renderizarPdf(urlTratada, modo);
  }

  return renderizarImagem(urlTratada, modo);
}

function renderizarPdf(url: string, modo: 'thumbnail' | 'full' | 'form'): JSX.Element {
  if (modo === 'thumbnail') {
    return (
      <div className="flex flex-col items-center justify-center gap-2 w-full h-full text-[#0D4F97]">
        <FileText size={48} strokeWidth={1.5} />
        <span className="text-xs font-medium text-gray-500">Documento PDF</span>
      </div>
    );
  }

  const alturaClasse = modo === 'form' ? 'h-full min-h-[200px]' : 'min-h-[500px]';

  return (
    <iframe
      src={`${url}#toolbar=1&navpanes=0`}
      title="Pré-visualização PDF"
      className={`w-full ${alturaClasse} border-0`}
      style={{ backgroundColor: '#f5f5f5' }}
    />
  );
}

function renderizarImagem(url: string, modo: 'thumbnail' | 'full' | 'form'): JSX.Element {
  const tamanho = modo === 'thumbnail' ? 200 : 500;

  return (
    <Image
      src={url}
      alt="Pré-visualização"
      width={tamanho}
      height={tamanho}
      className="w-full h-full object-contain"
      unoptimized
    />
  );
}