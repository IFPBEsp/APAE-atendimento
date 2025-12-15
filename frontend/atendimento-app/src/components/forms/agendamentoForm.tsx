import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Nunito } from "next/font/google";
import { Check, Search } from "lucide-react";

export type AgendamentoFormData = {
  paciente: string;
  data: string;
  horario: string;
  numeracao: number;
};

interface AgendamentoFormProps {
  onSubmit: (data: AgendamentoFormData) => void;
}

const nunito = Nunito({ weight: "700" });

export default function AgendamentoForm({ onSubmit }: AgendamentoFormProps) {

    const { register, handleSubmit } = useForm<AgendamentoFormData>({
      defaultValues: {
        paciente: "",
        data: "",
        horario: "",
        numeracao: 1,
      },
    });

  return  (
    <form onSubmit={handleSubmit(onSubmit)}
      className={`grid gap-6 pt-5 text-[#344054] ${nunito.className}`}>
      
      <div className="grid gap-2">
        <Label>
          Paciente <span className="text-[#F28C38]">*</span>
        </Label>
        <div className="relative flex-1">
          <Input
            placeholder="Nome do paciente"
            className="bg-white border border-[#3B82F6] rounded-full text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]"
            {...register("paciente", { required: true })}
          />
          <Search className="absolute right-3 top-2 h-5 w-5 text-gray-500" />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
  
        <div className="grid gap-2">
          <Label>
            Data <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="date"
            className="rounded-[30px] border-[#3B82F6] focus-visible:ring-0 focus-visible:border-[#3B82F6]"
            {...register("data", { required: true })}
          />
        </div>
        
        <div className="grid gap-2">
          <Label>
            Horário <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="time"
            className="rounded-[30px] border-[#3B82F6] focus-visible:ring-0 focus-visible:border-[#3B82F6]"
            {...register("horario", { required: true })}
          />
        </div>
        
      </div>

      <div className="grid gap-2">
        <Label>Numeração</Label>
        <input
          type="number"
          min={1}
          className="w-full rounded-[30px] border border-[#3B82F6] 
            focus-visible:ring-0 focus-visible:border-[#3B82F6] py-2 text-sm text-center"
          {...register("numeracao")}
        />
      </div>

      <DialogFooter>
        <Button
          type="submit"
          className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
        >
          <Check className="mr-1" />
          Criar Agendamento
        </Button>
      </DialogFooter>
    </form>
  )
}