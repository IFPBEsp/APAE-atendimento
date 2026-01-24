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

interface AnexoModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: ReactNode;
}

const nunitoFont = Nunito({ weight: "700" });

export function AnexoModal ({ 
    open, 
    onOpenChange,
    children,
} : AnexoModalProps) {

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent 
            className={`sm:max-w-[632px] rounded-[30px] max-h-[560px] overflow-auto ${nunitoFont.className}`}
            >
                <div className="absolute right-5 top-5 z-10">
                    <DialogClose className="rounded-full p-1 hover:bg-gray-100 transition-colors outline-none cursor-pointer">
                        <X className="h-6 w-6" />
                    </DialogClose>
                </div>

                <DialogHeader className="mt-4">
                  <DialogTitle className=" text-xl text-center text-[#344054]">
                    Adicionar Anexo
                  </DialogTitle>
                </DialogHeader>

                {children}

            </DialogContent>
        </Dialog>
    );
}