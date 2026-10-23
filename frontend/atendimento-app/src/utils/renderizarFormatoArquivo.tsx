import Image from "next/image";
import { JSX } from "react";
import PdfPreview from "@/components/pdf/PdfViewner";

type TipoNormalizado = 'pdf' | 'image';

const TIPOS_PDF = ['application/pdf', 'pdf'];
const TIPOS_IMAGEM = ['image/jpeg', 'image/jpg', 'image/png', 'image/heic', 'image/heif', 'jpeg', 'jpg', 'png', 'heic', 'heif'];

function normalizarTipo(tipo: string | undefined | null): TipoNormalizado | null {
  if (!tipo || typeof tipo !== 'string') return null;
  const tipoTratado = tipo.toLowerCase().trim();
  if (TIPOS_PDF.includes(tipoTratado)) return 'pdf';
  if (TIPOS_IMAGEM.includes(tipoTratado)) return 'image';
  return null;
}

export function renderizarFormatoArquivo(
  tipo: string | undefined | null,
  url: string | undefined | null,
  modo: 'thumbnail' | 'full' | 'form' = 'full'
): JSX.Element | null {
  if (!url) return null;
  const tipoNormalizado = normalizarTipo(tipo);
  if (!tipoNormalizado) return null;

  if (tipoNormalizado === 'pdf') {
    return renderizarPdf(url, modo);
  }

  return renderizarImagem(url, modo);
}

function renderizarPdf(url: string, modo: 'thumbnail' | 'full' | 'form'): JSX.Element {
  if (modo === 'thumbnail') {
    return (
      <div className="flex flex-col items-center justify-center w-full h-full pointer-events-none overflow-hidden bg-[#f8fafd]">
        <PdfPreview pdfUrl={url} width={300} />
      </div>
    );
  }

  const limitesAltura = modo === 'form' ? 'max-h-[300px]' : 'max-h-[60vh]';

  return (
    <div className={`flex flex-col items-center justify-center w-full h-full ${limitesAltura} overflow-hidden p-4`}>
      <div className="w-full h-full flex items-center justify-center [&_canvas]:max-w-full [&_canvas]:max-h-full [&_canvas]:!w-auto [&_canvas]:!h-auto [&_canvas]:object-contain [&_canvas]:shadow-md [&_canvas]:rounded-lg">
        <PdfPreview pdfUrl={url} width={800} />
      </div>
    </div>
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
