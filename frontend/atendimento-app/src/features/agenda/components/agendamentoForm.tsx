"use client";

import { useForm } from "react-hook-form";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Nunito } from "next/font/google";
import { Check, Search, Users } from "lucide-react";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { usePacientesDropdown } from "@/features/agenda/hooks/usePacientesDropdown";
import { useProfissionaisDropdown } from "@/features/agenda/hooks/useProfissionaisDropdown";

export type AgendamentoFormData = {
  pacienteId: string;
  pacienteNome?: string;
  data: string;
  horario: string;
};

interface AgendamentoFormProps {
  onSubmit: (data: AgendamentoFormData) => void;
}

const nunito = Nunito({ weight: "700" });

function getTodayLocalDate() {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60000;
  return new Date(now.getTime() - offset).toISOString().split("T")[0];
}

function getNowLocalTime() {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60000;
  return new Date(now.getTime() - offset).toISOString().split("T")[1].slice(0, 5);
}

export default function AgendamentoForm({
  onSubmit,
}: AgendamentoFormProps) {
  const { register, handleSubmit, setValue, watch } =
    useForm<AgendamentoFormData>({
      defaultValues: {
        pacienteId: "",
        pacienteNome: "",
        data: getTodayLocalDate(),
        horario: "",
      },
    });

  const pacienteId = watch("pacienteId");
  const dataSelecionada = watch("data");

  const hoje = getTodayLocalDate();
  const isDataHoje = dataSelecionada === hoje;
  const horarioMinimo = isDataHoje ? getNowLocalTime() : undefined;

  const { data: pacientes = [], isLoading: isLoadingPacientes } = usePacientesDropdown("meus");

  function handleSelectPaciente(value: string) {
    const paciente = pacientes.find((p) => p.id === value);
    if (!paciente) return;

    setValue("pacienteId", paciente.id);
    setValue("pacienteNome", paciente.nome);
  }

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
            min={hoje}
            {...register("data", {
              required: true,
              validate: (value) =>
                value >= hoje || "A data não pode estar no passado",
            })}
            className="rounded-[30px] border-[#3B82F6] focus-visible:ring-0"
          />
        </div>

        <div className="grid gap-2">
          <Label>
            Horário <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="time"
            min={horarioMinimo}
            {...register("horario", {
              required: true,
              validate: (value) =>
                !isDataHoje ||
                value >= horarioMinimo! ||
                "O horário não pode estar no passado",
            })}
            className="rounded-[30px] border-[#3B82F6] focus-visible:ring-0"
          />
        </div>
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