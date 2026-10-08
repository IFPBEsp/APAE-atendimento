import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { X } from "lucide-react";
import { Relatorio } from "../types";

interface AtendimentoDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  data: string;
  relatorios?: Relatorio[];
}

export function AtendimentoDetailsModal({
  isOpen,
  onClose,
  data,
  relatorios,
}: AtendimentoDetailsModalProps) {
  const listaRelatorios =
    relatorios && relatorios.length > 0
      ? relatorios
      : [{ titulo: "Sem titulo", descricao: "Sem descricao" }];

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="flex flex-col w-full max-w-[360px] max-h-[569px] sm:max-w-[768px] sm:max-h-[611px] p-0 gap-0 rounded-[30px] overflow-hidden bg-white [&>button]:hidden">
        <div className="flex items-center justify-between px-4 py-3 shrink-0">
          <h2 className="text-xl font-bold text-[#344054]">{data}</h2>

          <button
            onClick={onClose}
            className="text-[#344054] hover:cursor-pointer hover:bg-gray-100 p-1 rounded-full transition"
          >
            <X size={24} />
          </button>
        </div>

        <div className="h-[2px] bg-[#E8EEF7] mx-4 shrink-0"></div>

        <div className="overflow-y-auto custom-scrollbar px-4 pt-4">
          <DialogTitle className="hidden">
            Detalhes do Atendimento {data}
          </DialogTitle>

          <div className="space-y-3">
            {listaRelatorios.map((relatorio, index) => (
              <div key={index} className="space-y-1">
                <h3 className="text-base font-bold text-[#344054]">
                  {relatorio.titulo}
                </h3>
                <p className="text-sm text-[#555555] leading-relaxed text-justify whitespace-pre-wrap">
                  {relatorio.descricao}
                </p>
              </div>
            ))}

            <div className="h-2 w-full shrink-0" aria-hidden="true" />
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}