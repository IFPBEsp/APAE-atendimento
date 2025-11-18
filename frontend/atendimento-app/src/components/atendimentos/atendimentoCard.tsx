import { Expand } from "lucide-react";

interface AtendimentoCardProps {
  id: string;
  data: string;
  numeracao: number;
  titulo: string;
  descricao: string;
}

export default function AtendimentoCard({
  id,
  data,
  numeracao,
  titulo,
  descricao,
}: AtendimentoCardProps) {

  return (
    <div className="w-full bg-white rounded-3xl shadow-md p-5 border border-gray-100">
      <div className="flex items-center justify-between mb-3">

        <span className="text-lg font-bold text-[#344054]">{data}</span>

        <div className="flex items-center gap-3">

          <span className="w-6 h-6 rounded-full bg-[#165BAA] text-white text-[11px] flex items-center justify-center font-semibold">
            {String(numeracao).padStart(2, "0")}
          </span>

          <button
            onClick={() => console.log("a implemetar")}
            className="text-[#344054]"
          >
            <Expand size={22}/>
          </button>
        </div>
      </div>


      <div className="w-full h-[2px] bg-[#E8EEF7] mb-3"></div>

      <h2 className="text-[15px] font-semibold text-[#344054] mb-1">{titulo}</h2>
      <p className="text-sm text-[#222222] leading-relaxed">{descricao}</p>
    </div>
  );
}