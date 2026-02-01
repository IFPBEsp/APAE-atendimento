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

interface AgendamentoModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: ReactNode;
}

const nunitoFont = Nunito({ weight: "700" });

export function AgendamentoModal({
  open,
  onOpenChange,
  children,
}: AgendamentoModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={`sm:max-w-158 rounded-[30px] max-h-140 overflow-auto ${nunitoFont.className}`}
      >
        <div className="absolute right-5 top-5 z-10">
          <DialogClose className="rounded-full p-1 hover:bg-gray-100 transition-colors outline-none">
            <X className="h-6 w-6 text-gray-600" />
          </DialogClose>
        </div>

        <DialogHeader className="mt-4">
          <DialogTitle className="text-xl text-center text-[#344054]">
            Novo Agendamento
          </DialogTitle>
        </DialogHeader>

        {children}
      </DialogContent>
    </Dialog>
  );
}
