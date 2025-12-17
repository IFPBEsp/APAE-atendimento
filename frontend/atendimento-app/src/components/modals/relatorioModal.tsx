"use client";

import { X, Trash2, Download, Image as ImageIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Relatorio } from "@/types/Relatorio";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";

interface DeleteModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

interface ViewModalProps {
  isOpen: boolean;
  onClose: () => void;
  data: Relatorio | null;
}

export function RelatorioDeleteModal({
  isOpen,
  onClose,
  onConfirm,
}: DeleteModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-white rounded-3xl p-8 w-[360px] md:w-[650px] flex flex-col items-center shadow-xl zoom-in-95">
        <h2 className="text-xl font-bold text-[#344054] mb-2 text-center">
          Tem certeza que deseja excluir?
        </h2>
        <p className="text-[#344054] text-sm mb-8 text-center">
          Não é possível desfazer essa ação.
        </p>

        <div className="flex flex-col md:flex-row gap-3 w-full">
          <Button
            variant="outline"
            onClick={onClose}
            className="w-full md:flex-1 rounded-full border-[#165BAA] text-[#165BAA] hover:bg-blue-50 h-11 cursor-pointer"
          >
            <X size={18} className="mr-2" />
            Cancelar
          </Button>
          <Button
            onClick={onConfirm}
            className="w-full md:flex-1 rounded-full bg-[#FF5C5C] hover:bg-[#ff4040] text-white h-11 cursor-pointer"
          >
            <Trash2 size={18} className="mr-2" />
            Apagar
          </Button>
        </div>
      </div>
    </div>
  );
}

export function RelatorioViewModal({ isOpen, onClose, data }: ViewModalProps) {
  if (!isOpen || !data) return null;
  const renderizar = renderizarFormatoArquivo(
    data.nomeArquivo.split(".").pop() || "",
    data.presignedUrl || ""
  );
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm px-4 py-6 animate-in fade-in duration-200">
      <div className="bg-white rounded-[24px] w-full max-w-[632px] max-h-[90vh] flex flex-col shadow-2xl overflow-hidden slide-in-from-bottom-10 overflow-y-auto">
        <div className="flex items-center justify-between p-4 border-b border-gray-100">
          <h3 className="text-xl font-bold text-[#344054]">
            {data.titulo} - {data.data}
          </h3>
          <button
            onClick={onClose}
            className="text-gray-500 hover:bg-gray-100 rounded-full transition-colors cursor-pointer"
          >
            <X size={24} />
          </button>
        </div>

        <p className="text-[10px] md:text-[12px] font-regular text-[#344054] px-5 py-3">
          {data.descricao}
        </p>

        <div className=" bg-white rounded-[24px] w-full max-w-[632px] max-h-[90vh] flex flex-col shadow-2xl slide-in-from-bottom-10 overflow-y-auto">
          {data.presignedUrl ? (
            renderizar
          ) : (
            <div className="flex flex-col items-center text-gray-400">
              <ImageIcon size={64} />
              <p className="mt-2 text-sm">Visualização não disponível</p>
            </div>
          )}
        </div>

        <div className="p-4 border-t border-gray-100 flex flex-col items-center gap-4">
          <span className="text-sm text-[#344054] underline decoration-1">
            {data.nomeArquivo}
          </span>
          <a
            href={data.presignedUrl}
            download={data.nomeArquivo}
            target="_blank"
            rel="noopener noreferrer"
            className="w-full"
          >
            <Button className="w-full bg-[#165BAA] hover:bg-[#13447D] text-white h-12 rounded-full text-base font-semibold shadow-lg">
              <Download size={20} className="mr-2" />
              Salvar anexo
            </Button>
          </a>
        </div>
      </div>
    </div>
  );
}
