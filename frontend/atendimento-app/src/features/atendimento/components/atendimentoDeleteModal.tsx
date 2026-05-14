"use client";

import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { X, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AtendimentoDeleteModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    disabled: boolean;
}

export function AtendimentoDeleteModal({
                                           isOpen,
                                           onClose,
                                           onConfirm,
                                           disabled,
                                       }: AtendimentoDeleteModalProps) {
    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="flex flex-col w-full max-w-[360px] md:max-w-[650px] p-0 gap-0 rounded-[30px] overflow-hidden bg-white [&>button]:hidden">
                <div className="p-8 flex flex-col items-center">
                    <DialogTitle className="text-xl font-bold text-[#344054] mb-2 text-center">
                        Tem certeza que deseja excluir?
                    </DialogTitle>
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
                            onClick={() => {
                                onConfirm();
                                onClose();
                            }}
                            disabled={disabled}
                            className="w-full md:flex-1 rounded-full bg-[#FF5C5C] hover:bg-[#ff4040] text-white h-11 cursor-pointer"
                        >
                            <Trash2 size={18} className="mr-2" />
                            Apagar
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}