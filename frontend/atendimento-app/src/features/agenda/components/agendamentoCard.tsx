import { Trash2, Check, Lock, User, Pencil, Clock } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AgendamentoCardProps {
  id: string;
  paciente: string;
  horario: string;
  numeroAtendimento: string;
  status: boolean;
  externo: boolean;
  onDeleteClick?: () => void;
  onConcluirClick?: () => void;
  onEditClick?: () => void;
}

export default function AgendamentoCard({
  paciente,
  horario,
  numeroAtendimento,
  status,
  externo,
  onDeleteClick,
  onConcluirClick,
  onEditClick,
}: AgendamentoCardProps) {
  return (
    <div
      className={`group bg-white border ${
        externo ? "border-amber-200" : "border-[#E5E7EB]"
      } border-t-[6px] border-t-[#165BAA] rounded-2xl shadow-sm hover:shadow-md p-4 flex flex-col transition-all duration-300 relative`}
    >
      <div className="flex justify-between items-start">
        <div className="flex flex-col gap-0.5">
          <div className="flex items-center gap-1.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
            <User size={12} strokeWidth={2.5} />
            <span>Paciente</span>
            
            {externo && (
              <span className="normal-case text-[9px] font-semibold bg-amber-50 text-amber-600 px-1.5 py-0.5 rounded border border-amber-100 flex items-center gap-1 ml-1">
                <Lock size={10} /> Sistema Geral
              </span>
            )}
          </div>
          <div className="text-[16px] font-bold text-[#101828] mt-0.5 leading-tight line-clamp-1">
            {paciente}
          </div>
        </div>

        {status && !externo && (
          <button
            onClick={onDeleteClick}
            className="text-gray-300 hover:text-red-500 p-1 rounded-md hover:bg-red-50 transition-colors cursor-pointer"
            title="Apagar agendamento"
          >
            <Trash2 size={16} />
          </button>
        )}
      </div>

      <div className="flex justify-between items-center border border-gray-200 rounded-[12px] px-3 py-2.5 my-3.5 bg-white">
        
        <div className="flex items-center gap-3 pl-1">
          <Clock className="text-[#165BAA]" size={18} />
          <div className="flex flex-col">
            <span className="text-[9px] font-bold text-gray-400 uppercase tracking-wider">
              Horário
            </span>
            <span className="text-[22px] font-extrabold text-[#101828] leading-none mt-0.5">
              {horario}
            </span>
          </div>
        </div>

        {!externo && (
          <div className="flex items-center h-full">
            <div className="w-px h-8 bg-gray-200 mx-4" />
            
            <div className="flex flex-col items-center justify-center pr-2">
              <span className="text-[9px] font-bold text-gray-400 uppercase tracking-wider">
                Ficha
              </span>
              <span className="mt-1 text-[11px] font-bold text-white bg-[#165BAA] w-6 h-6 flex items-center justify-center rounded-full">
                {String(numeroAtendimento ?? "0").padStart(2, "0")}
              </span>
            </div>
          </div>
        )}
      </div>

      <div className="flex items-center justify-between mt-auto">
        
        {status ? (
          <Button
            disabled
            className="text-white w-full h-9 rounded-[10px] text-[13px] font-semibold shadow-none disabled:opacity-100 disabled:bg-[#7fe4a9]"
          >
            <Check size={16} className="mr-2" strokeWidth={3} />
            Atendimento Concluído
          </Button>
        ) : (

          <div className="flex items-center justify-between w-full">
            
            <Button
              onClick={() => {
                if (!externo && !status) {
                  onConcluirClick?.();
                }
              }}
              disabled={externo}
              className="bg-white border border-[#165BAA] text-[#165BAA] hover:bg-[#165BAA] hover:text-white h-9 px-4 rounded-[10px] text-[12px] font-semibold shadow-none transition-colors disabled:opacity-50"
            >
              Concluir Atendimento
            </Button>

            <div className="flex items-center gap-1.5 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              
              <button
                onClick={onEditClick}
                className="w-9 h-9 flex items-center justify-center text-gray-400 border border-transparent bg-white rounded-[10px] hover:border-[#165BAA] hover:text-[#165BAA] hover:bg-blue-50 transition-all cursor-pointer"
                title="Editar agendamento"
              >
                <Pencil size={15} />
              </button>

              {!externo && (
                <button
                  onClick={onDeleteClick}
                  className="w-9 h-9 flex items-center justify-center text-gray-400 border border-transparent bg-white rounded-[10px] hover:bg-[#fa2c37] hover:text-white hover:border-[#fa2c37] transition-all cursor-pointer"
                  title="Apagar agendamento"
                >
                  <Trash2 size={15} />
                </button>
              )}
            </div>
            
          </div>
        )}
      </div>
    </div>
  );
}