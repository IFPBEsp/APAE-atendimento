import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Nunito } from "next/font/google";
import { X } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import AtendimentoForm, {
  AtendimentoFormData,
} from "../forms/atendimentoForm";

const nunitoFont = Nunito({ weight: "700" });

interface AtendimentoModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;

  onSubmit: (data: AtendimentoFormData) => void;

  /** Dados iniciais vindos do agendamento (opcional) */
  initialData?: {
    pacienteNome?: string;
    data?: string;
    numeracao?: number;
  };

  /** Define se campos ficam bloqueados */
  lockFromAgendamento?: boolean;
}

export function AtendimentoModal({
  open,
  onOpenChange,
  onSubmit,
  initialData,
  lockFromAgendamento = false,
}: AtendimentoModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={`sm:max-w-[632px] rounded-[30px] max-h-[560px] p-1 pb-8 ${nunitoFont.className}`}
      >
        <ScrollArea className="max-h-[480px] px-4">
          <div className="relative mb-4">
            <DialogClose className="mt-2 absolute right-0 top-0 rounded-full p-1 hover:bg-gray-100 transition-colors outline-none cursor-pointer">
              <X className="h-6 w-6" />
            </DialogClose>

            <DialogHeader className="pt-6">
              <DialogTitle className="text-xl text-center text-[#344054]">
                Adicionar novo atendimento
              </DialogTitle>
            </DialogHeader>
          </div>

          <AtendimentoForm
            onSubmit={(data) => {
              onSubmit(data);
              onOpenChange(false);
            }}
            initialData={initialData}
            lockedFields={
              lockFromAgendamento
                ? {
                    paciente: true,
                    data: true,
                    numeracao: true,
                  }
                : undefined
            }
          />
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}