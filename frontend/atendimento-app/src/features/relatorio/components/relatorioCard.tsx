import { Trash2, Image as ImageIcon, FileText, Calendar, Paperclip } from "lucide-react";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";
import { formatarData } from "@/utils/formatarData";

interface RelatorioCardProps {
  id: number;
  titulo: string;
  data: string;
  fileName: string;
  imageUrl?: string;
  onView: () => void;
  onDelete: () => void;
}

export default function RelatorioCard({
  titulo,
  data,
  fileName,
  imageUrl,
  onView,
  onDelete,
}: RelatorioCardProps) {
  const extensao = (fileName.split(".").pop() || "").toLowerCase();

  const renderizar = renderizarFormatoArquivo(
    extensao,
    imageUrl || "",
    "thumbnail",
  );

  return (
    <div className="group relative w-full bg-white border border-[#E5E7EB] border-t-[6px] border-t-[#165BAA] rounded-2xl shadow-sm hover:shadow-md p-4 flex flex-col h-[320px] transition-all duration-200">
      <div className="flex justify-between items-start gap-2">
        <div className="flex flex-col gap-0.5 min-w-0">
          <div className="flex items-center gap-1.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
            <FileText size={12} strokeWidth={2.5} />
            <span>Título</span>
            <span className="text-gray-300">•</span>
            <Calendar size={12} strokeWidth={2.5} />
            <span>{formatarData(data)}</span>
          </div>
          <div
            className="text-[16px] font-bold text-[#101828] mt-0.5 leading-tight line-clamp-1"
            title={titulo}
          >
            {titulo}
          </div>
        </div>

        <button
          onClick={(e) => {
            e.stopPropagation();
            onDelete();
          }}
          className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500 p-1.5 rounded-md hover:bg-red-50 transition-all duration-200 cursor-pointer shrink-0"
          title="Excluir relatório"
        >
          <Trash2 size={16} />
        </button>
      </div>

      <div
        onClick={onView}
        className="flex-1 bg-gray-50 rounded-[12px] border border-gray-200 flex items-center justify-center overflow-hidden cursor-pointer relative hover:border-[#165BAA]/40 hover:opacity-95 transition-all my-3"
      >
        {imageUrl ? (
          renderizar
        ) : (
          <ImageIcon className="text-gray-300 w-12 h-12" />
        )}
      </div>

      <div className="flex items-center justify-between mt-auto pt-2 border-t border-gray-100">
        <div className="flex flex-col gap-0.5 min-w-0 w-full">
          <div className="flex items-center gap-1.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
            <Paperclip size={12} strokeWidth={2.5} />
            <span>Arquivo</span>
          </div>
          <p
            className="text-xs font-medium text-[#344054] truncate"
            title={fileName}
          >
            {fileName}
          </p>
        </div>
      </div>
    </div>
  );
}