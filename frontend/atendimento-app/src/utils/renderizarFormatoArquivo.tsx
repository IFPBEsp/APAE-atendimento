import PdfPreview from "@/components/pdf/PdfViewner";
import Image from "next/image";
import { JSX } from "react";

type TipoNormalizado = 'pdf' | 'image';

const TIPOS_PDF = ['application/pdf', 'pdf'];
const TIPOS_IMAGEM = ['image/jpeg', 'image/jpg', 'image/png', 'image/heic', 'image/heif', 'jpeg', 'jpg', 'heic', 'heif'];

export function renderizarFormatoArquivo(
  tipo: string,
  url: string
): JSX.Element | null {

  let tipoNormalizado: TipoNormalizado | null = null;

  if (TIPOS_PDF.includes(tipo)) {
    tipoNormalizado = 'pdf';
  } else if (TIPOS_IMAGEM.includes(tipo)) {
    tipoNormalizado = 'image';
  }

  if (!tipoNormalizado) return null;

  const opcoesFormatoArquivo: Record<TipoNormalizado, () => JSX.Element> = {
    pdf: () => <PdfPreview pdfUrl={url} />,
    image: () => (
      <Image
        src={url}
        alt="Pré-visualização"
        width={400}
        height={400}
        className="w-full h-full object-contain"
        unoptimized
      />
    ),
  };

  return opcoesFormatoArquivo[tipoNormalizado]();
}
