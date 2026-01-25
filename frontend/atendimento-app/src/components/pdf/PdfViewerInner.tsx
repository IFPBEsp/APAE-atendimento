"use client";

import { useState, useEffect } from "react";
import dynamic from "next/dynamic";

const Document = dynamic(
  () => import("react-pdf").then((mod) => mod.Document),
  { ssr: false }
);

const Page = dynamic(
  () => import("react-pdf").then((mod) => mod.Page),
  { ssr: false }
);

interface PDFThumbnailProps {
  pdfUrl: string;
  width?: number;
}

export default function PDFThumbnailSimple({
  pdfUrl,
  width = 300,
}: PDFThumbnailProps) {
  const [numPages, setNumPages] = useState<number | null>(null);
  const [pageNumber] = useState<number>(1);

  useEffect(() => {
    import("react-pdf").then(({ pdfjs }) => {
      pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.min.js`;
    });
  }, []);

  function onDocumentLoadSuccess({ numPages }: { numPages: number }) {
    setNumPages(numPages);
  }

  return (
    <div style={{ width: `${width}px` }}>
      <Document
        file={pdfUrl}
        onLoadSuccess={onDocumentLoadSuccess}
        loading={<div>Carregando PDF...</div>}
        error={<div>Erro ao carregar PDF</div>}
      >
        <Page
          pageNumber={pageNumber}
          width={width}
          renderTextLayer={false}
          renderAnnotationLayer={false}
        />
      </Document>

      {numPages && (
        <p style={{ fontSize: "0.875rem", color: "#666", marginTop: "8px" }}>
          Página {pageNumber} de {numPages}
        </p>
      )}
    </div>
  );
}
