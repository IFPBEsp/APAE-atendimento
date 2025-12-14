"use client";

import { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Nunito } from "next/font/google";
import { Check, Search } from "lucide-react";
import { getPacientes } from "@/api/dadosPacientes";
import type { Paciente } from "@/types/Paciente";

export type AgendamentoFormData = {
  pacienteId: string;
  pacienteNome?: string;
  data: string;
  horario: string;
  numeracao: number;
};

interface AgendamentoFormProps {
  onSubmit: (data: AgendamentoFormData) => void;
}

const nunito = Nunito({ weight: "700" });

export default function AgendamentoForm({ onSubmit }: AgendamentoFormProps) {
  const { register, handleSubmit, setValue, watch } =
    useForm<AgendamentoFormData>({
      defaultValues: {
        pacienteId: "",
        pacienteNome: "",
        data: new Date().toISOString().split("T")[0],
        horario: "",
        numeracao: 1,
      },
    });

  const pacienteNome = watch("pacienteNome", "");

  const [pacientes, setPacientes] = useState<Paciente[]>([]);
  const [filtro, setFiltro] = useState("");
  const [mostraSugestoes, setMostraSugestoes] = useState(false);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      try {
        const lista = await getPacientes();
        if (!cancelled) setPacientes(lista);
      } catch (err) {
        console.error("Erro ao buscar pacientes para autocomplete", err);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!pacienteNome) {
      setFiltro("");
      return;
    }

    const t = setTimeout(
      () => setFiltro(pacienteNome.trim().toLowerCase()),
      200
    );

    return () => clearTimeout(t);
  }, [pacienteNome]);

  const sugestoes = useMemo(() => {
    if (!filtro) return [];

    return pacientes
      .filter((p) =>
        (p.nomeCompleto || "").toLowerCase().includes(filtro)
      )
      .slice(0, 8);
  }, [filtro, pacientes]);

  function handleSelectPaciente(p: Paciente) {
    setValue("pacienteId", String(p.id));
    setValue("pacienteNome", p.nomeCompleto ?? "");
    setMostraSugestoes(false);
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className={`grid gap-6 pt-5 text-[#344054] ${nunito.className}`}
    >
      <div className="grid gap-2 relative">
        <Label>
          Paciente <span className="text-[#F28C38]">*</span>
        </Label>

        <div>
          <Input
            placeholder="Digite o nome do paciente"
            {...register("pacienteNome")}
            onFocus={() => setMostraSugestoes(true)}
            className="bg-white border border-[#3B82F6] rounded-full text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]"
          />
          <Search className="absolute right-3 top-10 h-5 w-5 text-gray-400" />
        </div>

        {mostraSugestoes && sugestoes.length > 0 && (
          <ul className="absolute z-20 mt-1 w-full bg-white border rounded-lg shadow-md max-h-44 overflow-auto">
            {sugestoes.map((p) => (
              <li
                key={p.id}
                className="px-3 py-2 hover:bg-gray-100 cursor-pointer text-left"
                onMouseDown={(e) => {
                  e.preventDefault();
                  handleSelectPaciente(p);
                }}
              >
                <div className="text-sm font-medium text-[#344054]">
                  {p.nomeCompleto}
                </div>
                <div className="text-xs text-gray-500">
                  {p.cpf ?? p.contato ?? ""}
                </div>
              </li>
            ))}
          </ul>
        )}
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
          {...register("numeracao")}
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