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
        <div className="absolute right-5 top-5 z-10">
          <DialogClose className="rounded-full p-1 hover:bg-gray-100 transition-colors outline-none">
            <X className="h-6 w-6 text[]" />
          </DialogClose>
        </div>

        <DialogHeader className="mt-4">
          <DialogTitle className=" text-xl text-center text-[#344054] ">
            Adicionar novo atendimento
          </DialogTitle>
        </DialogHeader>

        {children}
      </DialogContent>
    </Dialog>
  );
}
