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
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";

interface RelatorioModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: ReactNode;
}

const nunitoFont = Nunito({ weight: "700" });

export function RelatorioModal({
  open,
  onOpenChange,
  children,
}: RelatorioModalProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={`sm:max-w-[632px] rounded-[30px] max-h-[560px] p-1 pb-8 ${nunitoFont.className}`}
      >
        <ScrollArea className="max-h-[480px] px-4 ">
          <div className="relative mb-4">
            <DialogClose className="mt-2 absolute right-0 top-0 rounded-full p-1 hover:bg-gray-100 transition-colors outline-none">
              <X className="h-6 w-6" />
            </DialogClose>

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent 
            className={`sm:max-w-[632px] rounded-[30px] max-h-[560px] overflow-auto ${nunitoFont.className}`}
            >
                <div className="absolute right-5 top-5 z-10">
                    <DialogClose className="rounded-full p-1 hover:bg-gray-100 transition-colors outline-none">
                        <X className="h-6 w-6 text[]" />
                    </DialogClose>
                </div>

          {children}
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
