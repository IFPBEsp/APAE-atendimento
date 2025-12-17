"use client";

import { X, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";

interface DeletarAgendamentoModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

export function DeletarAgendamentoModal({
  isOpen,
  onClose,
  onConfirm,
}: DeletarAgendamentoModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-white rounded-3xl p-8 w-[360px] md:w-[520px] flex flex-col items-center shadow-xl zoom-in-95">
        <h2 className="text-xl font-bold text-[#344054] mb-2 text-center">
          Cancelar agendamento?
        </h2>

        <p className="text-[#344054] text-sm mb-8 text-center">
          Esse agendamento será removido permanentemente.
        </p>

        <div className="flex flex-col md:flex-row gap-3 w-full">
          <Button
            variant="outline"
            onClick={onClose}
            className="w-full md:flex-1 rounded-full border-[#165BAA] text-[#165BAA] hover:bg-blue-50 h-11 cursor-pointer"
          >
            <X size={18} className="mr-2" />
            Voltar
          </Button>

          <Button
            onClick={onConfirm}
            className="w-full md:flex-1 rounded-full bg-[#FF5C5C] hover:bg-[#ff4040] text-white h-11 cursor-pointer"
          >
            <Trash2 size={18} className="mr-2" />
            Cancelar agendamento
          </Button>
        </div>
      </div>
    </div>
  );
}