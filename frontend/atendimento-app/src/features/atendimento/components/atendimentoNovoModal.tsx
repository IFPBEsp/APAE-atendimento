import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ReactNode } from "react";
import { Nunito } from "next/font/google";
import { X } from "lucide-react";

interface AtendimentoModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: ReactNode;
  modo?: "create" | "edit";
}

const nunitoFont = Nunito({ weight: "700" });

export function AtendimentoModal({
  open,
  onOpenChange,
  children,
  modo,
}: AtendimentoModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={`sm:max-w-[950px] w-[95vw] rounded-[28px] max-h-[90vh] overflow-y-auto p-8 ${nunitoFont.className}`}
      >
        <div className="relative">
          <DialogClose className="absolute right-0 top-0 text-gray-500 hover:text-black transition-colors outline-none cursor-pointer">
            <X className="h-6 w-6" />
          </DialogClose>

          <DialogHeader className="pt-2 pb-6">
            <DialogTitle className="text-[22px] font-bold text-center text-[#102A43]">
              {modo === "edit"
                ? "Editar atendimento"
                : "Adicionar novo atendimento"}
            </DialogTitle>
          </DialogHeader>

          {children}
        </div>
      </DialogContent>
    </Dialog>
  );
}