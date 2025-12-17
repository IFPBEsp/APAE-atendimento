import { Trash2, Check } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AgendamentoCardProps {
  paciente: string;
  horario: string;
  numeracao: number;
  data: string;
  status: boolean;
  onDeleteClick?: () => void;
}

export default function AgendamentoCard({
  paciente,
  horario,
  numeracao,
  status,
  onDeleteClick,
}: AgendamentoCardProps) {
  return (
    <>
      <div className="bg-white border border-[#E5E7EB] rounded-2xl shadow-sm p-4 flex flex-col justify-between min-h-[250px]">
        <div className="flex justify-between items-start gap-2">
          <div className="text-[17px] font-semibold text-[#344054] leading-tight line-clamp-2">
            {paciente}
          </div>

          <div className="flex items-center gap-2">
            <span className="text-[11px] font-semibold text-white bg-[#165BAA] w-6 h-6 flex items-center justify-center rounded-full">
              {String(numeracao).padStart(2, "0")}
            </span>

            <button
              onClick={onDeleteClick}
              className="text-red-400 hover:text-red-600 cursor-pointer"
              title="Apagar agendamento"
            >
              <Trash2 size={20} />
            </button>
          </div>
        </div>

        <div className="flex justify-center my-6">
          <div className="bg-[#F8FAFD] w-full min-h-[110px] flex justify-center items-center">
            <span className="text-3xl font-bold text-[#344054]">{horario}</span>
          </div>
        </div>

        <div className="flex justify-center">
          <Button
            className={`h-8 px-3 rounded-full text-xs shadow-sm ${
              status
                ? "bg-green-500 hover:bg-green-600"
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
