import { Expand, Check, Pencil, Trash2 } from "lucide-react";
import { useState } from "react";
import { AtendimentoDetailsModal } from "./atendimentoDetailsModal";
import { AtendimentoModal } from "./atendimentoNovoModal";
import { AtendimentoDeleteModal } from "./atendimentoDeleteModal";
import AtendimentoForm from "./atendimentoForm";
import { Atendimento, Relatorio } from "../types";
import { useConcluirAtendimento } from "../hooks/useConcluirAtendimento";
import { Button } from "@/components/ui/button";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deletarAtendimento } from "../services/atendimentoService";
import { useParams } from "next/navigation";
import { toast } from "sonner";

interface AtendimentoCardProps {
  id: string;
  data: string;
  hora: string;
  numeracao: string;
  status: boolean;
  relatorio?: Relatorio[];
  atendimentos: Atendimento[];
}

export default function AtendimentoCard({
  id,
  data,
  hora,
  numeracao,
  status,
  relatorio,
  atendimentos,
}: AtendimentoCardProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const { mutate: concluir, isPending } = useConcluirAtendimento();

  const { id: pacienteIdParam } = useParams();
  const pacienteId = typeof pacienteIdParam === "string" ? pacienteIdParam : "";
  const queryClient = useQueryClient();

  const deleteMutation = useMutation({
    mutationFn: () => deletarAtendimento(pacienteId, id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["atendimentos", pacienteId] });
      toast.success("Atendimento apagado com sucesso.");
    },
    onError: () => {
      toast.error("Erro ao apagar atendimento.");
    },
  });

  const primeiroRelatorio =
    relatorio && relatorio.length > 0 ? relatorio[0] : null;

  return (
    <>
      <div className="group w-full bg-white rounded-2xl shadow-sm hover:shadow-md border border-[#E5E7EB] border-t-[6px] border-t-[#165BAA] p-5 flex flex-col justify-between h-full transition-all duration-300">
        <div>
          <div className="flex items-center justify-between mb-3">
            <span className="text-lg font-bold text-[#344054]">{data}</span>

            <div className="flex items-center gap-3">
              <span className="w-6 h-6 rounded-full bg-[#165BAA] text-white text-[11px] flex items-center justify-center font-semibold">
                {String(numeracao).padStart(2, "0")}
              </span>

              <button
                onClick={() => setIsModalOpen(true)}
                className="text-[#344054] hover:cursor-pointer hover:bg-gray-100 p-1 rounded-full transition-colors"
                title="Ver detalhes"
              >
                <Expand size={22} />
              </button>
            </div>
          </div>

          <div className="w-full h-[2px] bg-[#E8EEF7] mb-3"></div>

          <h2 className="text-[15px] font-semibold text-[#344054] mb-1 break-words">
            {primeiroRelatorio?.titulo || "Sem título"}
          </h2>
          <p className="text-sm text-[#222222] leading-relaxed mb-4 break-words">
            {primeiroRelatorio?.descricao || "Nenhum relatório adicionado."}
          </p>
        </div>

        {/* Rodapé do card: concluir + editar/excluir */}
        <div className="flex items-center justify-between mt-auto pt-2 border-t border-gray-50">
          <Button
            onClick={() => !status && concluir(id)}
            disabled={status || isPending}
            className={`h-8 px-3 rounded-full text-xs shadow-sm ${
              status
                ? "bg-green-500 hover:bg-green-600 disabled:opacity-100 text-white"
                : "bg-white border border-[#3B82F6] hover:bg-[#F8FAFD] text-[#344054]"
            }`}
          >
            {status ? (
              <>
                <Check size={14} className="mr-1" />
                Concluído
              </>
            ) : isPending ? (
              <>Concluindo...</>
            ) : (
              <>Marcar como Concluído</>
            )}
          </Button>

          <div className="flex items-center gap-1.5 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
            <button
              onClick={() => setEditOpen(true)}
              className="w-8 h-8 flex items-center justify-center text-gray-400 border border-transparent bg-white rounded-[10px] hover:border-[#165BAA] hover:text-[#165BAA] hover:bg-blue-50 transition-all cursor-pointer"
              title="Editar atendimento"
            >
              <Pencil size={15} />
            </button>

            <button
              onClick={() => setDeleteOpen(true)}
              className="w-8 h-8 flex items-center justify-center text-gray-400 border border-transparent bg-white rounded-[10px] hover:bg-[#fa2c37] hover:text-white hover:border-[#fa2c37] transition-all cursor-pointer"
              title="Apagar atendimento"
            >
              <Trash2 size={15} />
            </button>
          </div>
        </div>
      </div>

      <AtendimentoDetailsModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        data={data}
        relatorios={relatorio}
      />

      <AtendimentoDeleteModal
        isOpen={deleteOpen}
        onClose={() => setDeleteOpen(false)}
        onConfirm={() => {
          if (!pacienteId) {
            toast.error("Paciente inválido.");
            return;
          }
          deleteMutation.mutate();
        }}
        disabled={deleteMutation.isPending}
      />

      <AtendimentoModal open={editOpen} onOpenChange={setEditOpen} modo="edit">
        <AtendimentoForm
          atendimentos={atendimentos}
          atendimentoEditavel={{
            id,
            data,
            hora,
            numeracao,
            status,
            relatorio: relatorio ?? [],
          }}
          onClose={() => setEditOpen(false)}
        />
      </AtendimentoModal>
    </>
  );
}