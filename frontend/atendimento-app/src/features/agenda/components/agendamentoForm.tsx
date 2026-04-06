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

import { Agendamento } from "../types";
import { usePacientesDropdown } from "@/features/agenda/hooks/usePacientesDropdown";
import { useProfissionaisDropdown } from "@/features/agenda/hooks/useProfissionaisDropdown";

export type AgendamentoFormData = {
  pacienteId: string;
  pacienteNome?: string;
  profissionalId: string;
  profissionalNome?: string;
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
        profissionalId: "",
        profissionalNome: "",
        data: getTodayLocalDate(),
        horario: "",
        numeroAtendimento: 1,
      },
    });

  const pacienteId = watch("pacienteId");
  const profissionalId = watch("profissionalId")
  const dataSelecionada = watch("data");

  const { data: pacientes = [], isLoading: isLoadingPacientes } = usePacientesDropdown();
  const { data: profissionais = [], isLoading: isLoadingProfissionais } = useProfissionaisDropdown();

  function handleSelectPaciente(value: string) {
    const paciente = pacientes.find((p) => p.id === value);
    if (!paciente) return;

    setValue("pacienteId", paciente.id);
    setValue("pacienteNome", paciente.nome);
  }

  function handleSelectProfissional(value: string) {
    const profissional = profissionais.find((p) => p.id === value);
    if (!profissional) return;

    setValue("profissionalId", profissional.id);
    setValue("profissionalNome", profissional.nome);
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
          Profissional <span className="text-[#F28C38]">*</span>
        </Label>

        <Select
            required
            value={profissionalId}
            onValueChange={handleSelectProfissional}
            disabled={isLoadingProfissionais || profissionais.length === 0}
        >
          <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full text-sm focus:ring-0 w-full disabled:opacity-50 disabled:cursor-not-allowed">
            <SelectValue
                placeholder={
                  isLoadingProfissionais ? "Carregando..." :
                      profissionais.length === 0 ? "Nenhum profissional encontrado" :
                          "Selecione o profissional"
                }
            />
          </SelectTrigger>

          <SelectContent>
            {profissionais.map((p) => (
                <SelectItem key={p.id} value={p.id} className="cursor-pointer">
                  <span className="text-sm font-medium">{p.nome}</span>
                </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="grid gap-2">
        <Label>
          Paciente <span className="text-[#F28C38]">*</span>
        </Label>

        <Select
            required
            value={pacienteId}
            onValueChange={handleSelectPaciente}
            disabled={isLoadingPacientes || pacientes.length === 0}
        >
          <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full text-sm focus:ring-0 w-full disabled:opacity-50 disabled:cursor-not-allowed">
            <SelectValue
                placeholder={
                  isLoadingPacientes ? "Carregando..." :
                      pacientes.length === 0 ? "Nenhum paciente encontrado" :
                          "Selecione o paciente"
                }
            />
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
