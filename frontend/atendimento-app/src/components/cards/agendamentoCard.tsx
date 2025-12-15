import { CalendarCheck, Trash2, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AgendamentoCardProps {
  paciente: string;
  horario: string;
  numeracao: number;
  concluido?: boolean;
}

export default function AgendamentoCard({
  paciente,
  horario,
  numeracao,
}: AgendamentoCardProps) {
  return (
    <div
      className="
        bg-white border border-[#E5E7EB]
        rounded-2xl shadow-sm
        p-4 flex flex-col justify-between
        min-h-[250px]
      "
    >
      <div className="flex justify-between items-start gap-2">
        <div className="text-[17px] font-semibold text-[#344054] leading-tight line-clamp-2">
          {paciente}
        </div>

        <div className="flex items-center gap-2">
          <span className="text-[11px] font-semibold  text-white bg-[#165BAA] w-6 h-6 flex items-center justify-center rounded-full">
            {String(numeracao).padStart(2, "0")}
          </span>

          <button className="text-red-400 hover:text-red-600 cursor-pointer">
            <Trash2 size={20} />
          </button>
        </div>
      </div>

      <div className="flex justify-center my-6">
        <div className="bg-[#F8FAFD] w-full min-h-[110] flex justify-center items-center">
          <span
            className="text-3xl font-bold text-[#344054]">
            {horario}
          </span>
        </div>
      </div>
      
      <div className="flex justify-center">
          <Button
            className="
              h-8 px-2 rounded-full
              bg-[#165BAA] hover:bg-[#13447D]
              text-xs shadow-sm cursor-pointer
            "
          >
            <Plus size={14} className="mr-1" />
            Criar atendimento
          </Button>
      </div>
    </div>
  );
}
