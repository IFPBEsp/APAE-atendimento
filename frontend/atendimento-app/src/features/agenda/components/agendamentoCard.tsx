import { Trash2, Check, Lock } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AgendamentoCardProps {
  id: string;
  paciente: string;
  horario: string;
  numeroAtendimento: number;
  status: boolean;
  externo: boolean; 
  onDeleteClick?: () => void;
}

export default function AgendamentoCard({
  id,
  paciente,
  horario,
  numeroAtendimento,
  status,
  externo,
  onDeleteClick,
}: AgendamentoCardProps) {

  return (
    <>
      <div className={`bg-white border ${externo ? 'border-amber-200' : 'border-[#E5E7EB]'} rounded-2xl shadow-sm p-4 flex flex-col justify-between min-h-62.5`}>
        <div className="flex justify-between items-start gap-2">
          <div className="flex flex-col gap-1">
            <div className="text-[17px] font-semibold text-[#344054] leading-tight line-clamp-2">
              {paciente}
            </div>
            {/* Tag visual para o agendamento do Sistema Geral */}
            {externo && (
              <span className="text-[10px] bg-amber-50 text-amber-600 px-2 py-0.5 rounded w-fit border border-amber-100 flex items-center gap-1">
                <Lock size={10} /> Sistema Geral
              </span>
            )}
          </div>

          <div className="flex items-center gap-2">
            {/* Oculta a numeração se for um agendamento externo */}
            {!externo && (
              <span className="text-[11px] font-semibold text-white bg-[#165BAA] w-6 h-6 flex items-center justify-center rounded-full">
                {String(numeroAtendimento).padStart(2, "0")}
              </span>
            )}

            {/* Oculta a lixeira se for um agendamento externo */}
            {!externo && (
              <button
                onClick={onDeleteClick}
                className="text-red-400 hover:text-red-600 cursor-pointer"
                title="Apagar agendamento"
              >
                <Trash2 size={20} />
              </button>
            )}
          </div>
        </div>

        <div className="flex justify-center my-6">
          <div className="bg-[#F8FAFD] w-full min-h-27.5 flex justify-center items-center">
            <span className="text-3xl font-bold text-[#344054]">{horario}</span>
          </div>
        </div>

        <div className="flex justify-center">
          <Button
            className={`h-8 px-3 rounded-full text-xs shadow-sm ${
              status
                ? "bg-green-500 hover:bg-green-600 text-white"
                : "bg-white border border-[#3B82F6] hover:bg-[#F8FAFD] text-[#344054]"
            }`}
          >
            {status ? (
              <>
                <Check size={14} className="mr-1" />
                Concluído
              </>
            ) : (
              <>Não concluído</>
            )}
          </Button>
        </div>
      </div>
    </>
  );
}