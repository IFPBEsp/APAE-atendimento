"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Nunito } from "next/font/google";
import { Check } from "lucide-react";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { getPacientesPorProfissional } from "../services/agendaService";
import { PacienteOption, Agendamento } from "../types";

export type AgendamentoFormData = {
  pacienteId: string;
  pacienteNome?: string;
  data: string;
  horario: string;
  numeroAtendimento: number;
};

interface AgendamentoFormProps {
  agendamentos: Agendamento[];
  onSubmit: (data: AgendamentoFormData) => void;
}

const nunito = Nunito({ weight: "700" });

function getTodayLocalDate() {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60000;
  return new Date(now.getTime() - offset).toISOString().split("T")[0];
}

function extrairMesAno(data: string) {
  if (!data || data.length < 7) {
    return { mes: "", ano: "" };
  }

  if (data.includes("/")) {
    const [, mes, ano] = data.split("/");
    return { mes, ano };
  }

  const parts = data.split("-");

  if (parts[0].length === 4) {
    const [ano, mes] = parts;
    return { mes, ano };
  }

  const [, mes, ano] = parts;
  return { mes, ano };
}

export default function AgendamentoForm({
  agendamentos,
  onSubmit,
}: AgendamentoFormProps) {
  const { register, handleSubmit, setValue, watch } =
    useForm<AgendamentoFormData>({
      defaultValues: {
        pacienteId: "",
        pacienteNome: "",
        data: getTodayLocalDate(),
        horario: "",
        numeroAtendimento: 1,
      },
    });

  const pacienteId = watch("pacienteId");
  const dataSelecionada = watch("data");

  const [pacientes, setPacientes] = useState<PacienteOption[]>([]);

  useEffect(() => {
    async function carregarPacientes() {
      try {
        const data = await getPacientesPorProfissional();
        setPacientes(data);
      } catch (error) {
        console.error("Erro ao carregar pacientes", error);
      }
    }

    carregarPacientes();
  }, []);

  function handleSelectPaciente(value: string) {
    const paciente = pacientes.find((p) => p.id === value);
    if (!paciente) return;

    setValue("pacienteId", paciente.id);
    setValue("pacienteNome", paciente.nome);
  }

  useEffect(() => {
    if (!dataSelecionada) return;

    const { mes, ano } = extrairMesAno(dataSelecionada);

    const totalNoMes = agendamentos.filter((a) => {
      const dataAgendamento = extrairMesAno(a.data);
      return dataAgendamento.mes === mes && dataAgendamento.ano === ano;
    }).length;

    setValue("numeroAtendimento", totalNoMes + 1);
  }, [dataSelecionada, agendamentos, setValue]);

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className={`grid gap-6 pt-5 text-[#344054] ${nunito.className}`}
    >
      <div className="grid gap-2">
        <Label>
          Paciente <span className="text-[#F28C38]">*</span>
        </Label>

        <Select
          required
          value={pacienteId}
          onValueChange={handleSelectPaciente}
        >
          <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full text-sm focus:ring-0 w-full">
            <SelectValue placeholder="Selecione o paciente" />
          </SelectTrigger>

          <SelectContent>
            {pacientes.map((p) => (
              <SelectItem key={p.id} value={p.id} className="cursor-pointer">
                <span className="text-sm font-medium">{p.nome}</span>
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div className="grid gap-2">
          <Label>
            Data <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="date"
            {...register("data", { required: true })}
            className="rounded-[30px] border-[#3B82F6] focus-visible:ring-0"
          />
        </div>

        <div className="grid gap-2">
          <Label>
            Horário <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="time"
            {...register("horario", { required: true })}
            className="rounded-[30px] border-[#3B82F6] focus-visible:ring-0"
          />
        </div>
      </div>

      <div className="grid gap-2">
        <Label>Numeração</Label>
        <Input
          type="number"
          disabled
          {...register("numeroAtendimento", { valueAsNumber: true })}
          min={1}
          className="w-full rounded-[30px] border border-[#3B82F6] text-center"
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
  );
}
