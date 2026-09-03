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
  profissionalId: string;
  profissionalNome?: string;
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
  const [origemPacientes, setOrigemPacientes] = useState<"meus" | "todos">("meus");
  const { register, handleSubmit, setValue, watch } =
    useForm<AgendamentoFormData>({
      defaultValues: {
        pacienteId: "",
        pacienteNome: "",
        profissionalId: "",
        profissionalNome: "",
        data: getTodayLocalDate(),
        horario: "",
      },
    });

  const pacienteId = watch("pacienteId");
  const profissionalId = watch("profissionalId");
  const dataSelecionada = watch("data");

  const hoje = getTodayLocalDate();
  const isDataHoje = dataSelecionada === hoje;
  const horarioMinimo = isDataHoje ? getNowLocalTime() : undefined;

  const { data: pacientes = [], isLoading: isLoadingPacientes } = usePacientesDropdown(origemPacientes);
  const { data: profissionais = [], isLoading: isLoadingProfissionais } = useProfissionaisDropdown();

  function handleSelectPaciente(value: string) {
    const paciente = pacientes.find((p) => p.id === value);
    if (!paciente) return;

    setValue("pacienteId", paciente.id);
    setValue("pacienteNome", paciente.nome);
  }

  function trocarOrigemPacientes(origem: "meus" | "todos") {
    setOrigemPacientes(origem);
    setValue("pacienteId", "");
    setValue("pacienteNome", "");
  }

  function handleSelectProfissional(value: string) {
    const profissional = profissionais.find((p) => p.id === value);
    if (!profissional) return;

    setValue("profissionalId", profissional.id);
    setValue("profissionalNome", profissional.nome);
  }

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
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <Label>
            Paciente <span className="text-[#F28C38]">*</span>
          </Label>

          <div className="grid grid-cols-2 gap-1 rounded-full border border-[#D0D5DD] bg-white p-1">
            <Button
              type="button"
              variant={origemPacientes === "meus" ? "default" : "ghost"}
              onClick={() => trocarOrigemPacientes("meus")}
              className={`h-8 rounded-full px-3 text-xs ${
                origemPacientes === "meus"
                  ? "bg-[#165BAA] text-white hover:bg-[#13447D]"
                  : "text-[#344054] hover:bg-[#EDF2FB]"
              }`}
            >
              <Users className="h-3.5 w-3.5" />
              Meus Pacientes
            </Button>

            <Button
              type="button"
              variant={origemPacientes === "todos" ? "default" : "ghost"}
              onClick={() => trocarOrigemPacientes("todos")}
              className={`h-8 rounded-full px-3 text-xs ${
                origemPacientes === "todos"
                  ? "bg-[#165BAA] text-white hover:bg-[#13447D]"
                  : "text-[#344054] hover:bg-[#EDF2FB]"
              }`}
            >
              <Search className="h-3.5 w-3.5" />
              Todos
            </Button>
          </div>
        </div>

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

      <div className="grid gap-2">
        <Label htmlFor="numeracao">Numeração</Label>
        <Input
          id="numeracao"
          value="Gerada automaticamente"
          disabled
          className="w-full rounded-[30px] border border-[#3B82F6] text-center bg-gray-100 text-gray-500 cursor-not-allowed italic"
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