import { Expand, Check } from "lucide-react";
import { useState } from "react";
import { AtendimentoDetailsModal } from "./atendimentoDetailsModal";
import { Atendimento, Relatorio } from "../types";
import { useConcluirAtendimento } from "../hooks/useConcluirAtendimento"; 
import { Button } from "@/components/ui/button";

interface AtendimentoCardProps {
  id: string;
  data: string;
  hora: string;
  numeracao: number;
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
  const { mutate: concluir, isPending } = useConcluirAtendimento(); // Inicializa o hook

  const primeiroRelatorio =
    relatorio && relatorio.length > 0 ? relatorio[0] : null;

  return (
    <>
      <div className="w-full bg-white rounded-3xl shadow-md p-5 border border-gray-100 flex flex-col justify-between h-full">
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
              >
                <Expand size={22} />
              </button>
            </div>
          </div>

          <div className="w-full h-[2px] bg-[#E8EEF7] mb-3"></div>

          <h2 className="text-[15px] font-semibold text-[#344054] mb-1">
            {primeiroRelatorio?.titulo || "Sem título"}
          </h2>
          <p className="text-sm text-[#222222] leading-relaxed mb-4">
            {primeiroRelatorio?.descricao || "Nenhum relatório adicionado."}
          </p>
        </div>

        {/* Botão de concluir no final do Card */}
        <div className="flex justify-center mt-auto pt-2 border-t border-gray-50">
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
        </div>
      </div>

      <AtendimentoDetailsModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        atendimentoId={id}
        data={data}
        hora={hora}
        numeracao={numeracao}
        status={status}
        relatorios={relatorio}
        atendimentos={atendimentos}
      />
    </>
  );
}