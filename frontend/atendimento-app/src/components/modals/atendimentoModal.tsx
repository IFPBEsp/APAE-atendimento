import {
    Dialog,
    DialogContent,
    DialogTitle,
} from "@/components/ui/dialog";
import { Pencil, X } from "lucide-react";

interface Topico {
    titulo: string;
    descricao: string;
}

interface AtendimentoDetailsModalProps {
    isOpen: boolean;
    onClose: () => void;
    data: string;
    numeracao: number;
    topicos?: Topico[];
}

export function AtendimentoDetailsModal({
    isOpen,
    onClose,
    data,
    numeracao,
    topicos,
}: AtendimentoDetailsModalProps) {
    const listaTopicos = topicos && topicos.length > 0 ? topicos : [{ titulo: "Sem título", descricao: "Sem descrição" }];

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="flex flex-col w-full max-w-[360px] max-h-[569px] sm:max-w-[768px] sm:max-h-[611px] p-0 gap-0 rounded-[30px] overflow-hidden bg-white [&>button]:hidden">
                <div className="flex items-center justify-between px-4 py-3 shrink-0">
                    <h2 className="text-xl font-bold text-[#344054]">{data}</h2>

                    <div className="flex items-center gap-3">
                        <span className="w-8 h-8 rounded-full bg-[#165BAA] text-white text-sm flex items-center justify-center font-bold">
                            {String(numeracao).padStart(2, "0")}
                        </span>

                        <button className="text-[#344054] hover:cursor-pointer hover:bg-gray-100 p-1 rounded-full transition">
                            <Pencil size={20} />
                        </button>

                        <button
                            onClick={onClose}
                            className="text-[#344054] hover:cursor-pointer hover:bg-gray-100 p-1 rounded-full transition"
                        >
                            <X size={24} />
                        </button>
                    </div>
                </div>

                <div className="h-[2px] bg-[#E8EEF7] mx-4 shrink-0"></div>

                <div className="overflow-y-auto custom-scrollbar px-4 pt-4">

                    <DialogTitle className="hidden">Detalhes do Atendimento {data}</DialogTitle>

                    <div className="space-y-3">
                        {listaTopicos.map((topico, index) => (
                            <div key={index} className="space-y-1">
                                <h3 className="text-base font-bold text-[#344054]">
                                    {topico.titulo}
                                </h3>
                                <p className="text-sm text-[#555555] leading-relaxed text-justify whitespace-pre-wrap">
                                    {topico.descricao}
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