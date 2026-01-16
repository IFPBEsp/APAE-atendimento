import { Trash2, Image as ImageIcon } from "lucide-react";
import PdfViewer from "../../../components/pdf/PdfViewner";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";

interface AnexoCardProps {
  id?: number;
  titulo: string;
  data: string;
  fileName: string;
  imageUrl?: string;
  onView: () => void;
  onDelete: () => void;
}

export default function AnexoCard({
  titulo,
  data,
  fileName,
  imageUrl,
  onView,
  onDelete,
}: AnexoCardProps) {
  const renderizar = renderizarFormatoArquivo(
    fileName.split(".").pop() || "",
    imageUrl || ""
  );
  return (
    <div className="w-full bg-white rounded-3xl shadow-md p-4 border border-gray-100 flex flex-col h-[320px] transition-all">
      <div className="flex items-center justify-between mb-3">
        <span className="text-lg font-bold text-[#344054]">
          {titulo} - {data}
        </span>

        <button
          onClick={(e) => {
            e.stopPropagation();
            onDelete();
          }}
          className="text-red-500 hover:bg-gray-100 p-2 rounded-full transition-colors cursor-pointer"
          title="Excluir anexo"
        >
          <Trash2 size={20} />
        </button>
      </div>

      <div
        onClick={onView}
        className="flex-1 bg-gray-50 rounded-xl border border-dashed border-gray-200 flex items-center justify-center overflow-hidden cursor-pointer relative hover:opacity-90 transition-opacity mb-3"
      >
        {imageUrl ? (
          renderizar
        ) : (
          <ImageIcon className="text-gray-300 w-12 h-12" />
        )}
      </div>

      <div className="text-center">
        <p className="text-xs text-[#344054] truncate font-regular">
          {fileName}
        </p>
      </div>
    </div>
  );
}
