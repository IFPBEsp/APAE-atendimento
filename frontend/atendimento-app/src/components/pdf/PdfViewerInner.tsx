'use client';

import { Document, Page, pdfjs } from 'react-pdf';



pdfjs.GlobalWorkerOptions.workerSrc = 
  `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.min.js`;

interface PDFThumbnailProps {
  pdfUrl: string;
  width?: number;
}

export default function PDFThumbnailSimple({ 
  pdfUrl, 
  width = 300 
}: PDFThumbnailProps) {
  return (
    <div className="w-full h-full flex items-center justify-center overflow-hidden">
      <Document
        file={pdfUrl}
        loading={<div className="text-xs text-gray-400">Gerando thumbnail...</div>}
        error={<div className="text-xs text-red-400">Sem preview</div>}
        className="flex items-center justify-center"
      >                                                             
        <Page 
          pageNumber={1} 
          width={width}                                                             
          renderTextLayer={false}
          renderAnnotationLayer={false}
          className="pointer-events-none shadow-sm" 
        />
      </Document>
    </div>
  );
}