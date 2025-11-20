import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ReactNode } from "react";
import { Nunito } from "next/font/google";

interface AtendimentoModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: ReactNode;
}

const nunitoFont = Nunito({ weight: "700" });

export function AtendimentoModal({
  open,
  onOpenChange,
  children,
}: AtendimentoModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={`sm:max-w-[632px] rounded-[30px] max-h-[560px] overflow-scroll ${nunitoFont.className}`}
      >
        <DialogHeader>
          <DialogTitle className="text-center text-[#344054]">
            Adicionar novo atendimento
          </DialogTitle>
        </DialogHeader>

        {children}
      </DialogContent>
    </Dialog>
  );
}
