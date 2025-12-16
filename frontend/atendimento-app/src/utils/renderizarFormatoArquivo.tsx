import PdfPreview from "@/components/pdf/PdfViewner";
import { JSX } from "react";

type TipoNormalizado = 'pdf' | 'image';

export function renderizarFormatoArquivo(
  tipo: string,
  url: string
): JSX.Element | null {

  let tipoNormalizado: TipoNormalizado | null = null;

  const tiposPdf = ['application/pdf', 'pdf'];

  const tiposImagem = [
    'image/jpeg',
    'image/jpg',
    'image/png',
    'image/heic', 
    'image/heif', 
    'jpeg',
    'jpg',
    'heic',
    'heif'
  ];

  if (tiposPdf.includes(tipo)) {
    tipoNormalizado = 'pdf';
  } else if (tiposImagem.includes(tipo)) {
    tipoNormalizado = 'image';
  }

  if (!tipoNormalizado) return null;

  const opcoesFormatoArquivo: Record<TipoNormalizado, () => JSX.Element> = {
    pdf: () => <PdfPreview pdfUrl={url} />,
    image: () => (
      <img
        src={url}
        alt="Pré-visualização"
        className="w-full h-full object-contain"
      />
    ),
  };

  return opcoesFormatoArquivo[tipoNormalizado]();
}
