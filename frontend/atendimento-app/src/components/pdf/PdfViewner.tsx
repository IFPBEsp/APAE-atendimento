"use client";

import dynamic from "next/dynamic";

const PdfPreview = dynamic(() => import("./PdfViewerInner"), { ssr: false });

export default PdfPreview;
