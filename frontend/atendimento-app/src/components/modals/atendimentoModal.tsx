import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { X, Trash2 } from "lucide-react";
import { Relatorio } from "@/types/Atendimento";
import { deletarAtendimento } from "@/api/dadosAtendimentos";
import dados from "../../../data/verificacao.json";
import { useParams } from "next/navigation";

interface AtendimentoDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  atendimentoId: string;
  data: string;
  numeracao: number;
  relatorios?: Relatorio[];
  onDeleted: (id: string) => void;
}

export function AtendimentoDetailsModal({
  isOpen,
  onClose,
  atendimentoId,
  data,
  numeracao,
  relatorios,
  onDeleted,
}: AtendimentoDetailsModalProps) {
  const { id: pacienteId } = useParams();

  async function handleDelete() {
    if (!pacienteId) return;

    const confirmacao = confirm(
      "Tem certeza que deseja excluir este atendimento?"
    );

    if (!confirmacao) return;

    try {
      await deletarAtendimento(
        dados.idProfissional,
        String(pacienteId),
        atendimentoId
      );

      onDeleted(atendimentoId);
      onClose();
    } catch (err) {
      console.error(err);
      alert("Erro ao excluir atendimento");
    }
  }

  const listaRelatorios =
    relatorios && relatorios.length > 0
      ? relatorios
      : [{ titulo: "Sem título", descricao: "Sem descrição" }];

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="flex flex-col w-full max-w-[360px] max-h-[569px] sm:max-w-[768px] sm:max-h-[611px] p-0 gap-0 rounded-[30px] overflow-hidden bg-white [&>button]:hidden">
        <div className="flex items-center justify-between px-4 py-3 shrink-0">
          <h2 className="text-xl font-bold text-[#344054]">{data}</h2>

          <div className="flex items-center gap-3">
            <span className="w-8 h-8 rounded-full bg-[#165BAA] text-white text-sm flex items-center justify-center font-bold">
              {String(numeracao).padStart(2, "0")}
            </span>

            <button
              onClick={handleDelete}
              className="text-red-500 hover:cursor-pointer hover:bg-gray-100 p-1 rounded-full transition"
            >
              <Trash2 size={24} />
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
          <DialogTitle className="hidden">
            Detalhes do Atendimento {data}
          </DialogTitle>

          <div className="space-y-3">
            {listaRelatorios.map((relatorios, index) => (
              <div key={index} className="space-y-1">
                <h3 className="text-base font-bold text-[#344054]">
                  {relatorios.titulo}
                </h3>
                <p className="text-sm text-[#555555] leading-relaxed text-justify whitespace-pre-wrap">
                  {relatorios.descricao}
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
