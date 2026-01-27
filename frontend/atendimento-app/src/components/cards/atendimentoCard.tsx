import { Expand } from "lucide-react";
import { useState } from "react";
import { AtendimentoDetailsModal } from "../modals/atendimentoModal";
import { Atendimento, Relatorio } from "@/types/Atendimento";

interface AtendimentoCardProps {
  id: string;
  data: string;
  hora: string;
  numeracao: number;
  relatorio?: Relatorio[];
  atendimentos: Atendimento[];
  onUpdated: (a: Atendimento) => void;
}
export default function AtendimentoCard({
  id,
  data,
  hora,
  numeracao,
  relatorio,
  atendimentos,
  onUpdated,
}: AtendimentoCardProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const primeiroRelatorio =
    relatorio && relatorio.length > 0 ? relatorio[0] : null;

  return (
    <>
      <div className="w-full bg-white rounded-3xl shadow-md p-5 border border-gray-100">
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
          {primeiroRelatorio?.titulo}
        </h2>
        <p className="text-sm text-[#222222] leading-relaxed">
          {primeiroRelatorio?.descricao}
        </p>
      </div>

      <AtendimentoDetailsModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        atendimentoId={id}
        data={data}
        hora={hora}
        numeracao={numeracao}
        relatorios={relatorio}
        atendimentos={atendimentos}
        onUpdated={onUpdated!}
      />
    </>
  );
}
