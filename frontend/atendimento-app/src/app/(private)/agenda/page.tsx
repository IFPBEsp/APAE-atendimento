"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { ArrowLeft, CalendarPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { AgendamentoModal } from "@/components/modals/agendamentoModal";
import { DeletarAgendamentoModal } from "@/components/modals/deletarAgendamentoModal";
import AgendamentoForm, {
  AgendamentoFormData,
} from "@/components/forms/agendamentoForm";
import AgendamentoCard from "@/components/cards/agendamentoCard";
import { toast } from "sonner";

import {
  listarAgendamentos,
  criarAgendamento,
  deletarAgendamento,
} from "@/api/agendamento";

import dados from "../../../../data/verificacao.json";
import { AgendamentoResponse, DiaAgendamento } from "@/types/Agendamento";

interface Agendamento {
  id: string;
  pacienteId?: string;
  paciente: string;
  horario: string;
  data: string;
  numeracao: number;
  status: boolean;
}

const nunitoFont = Nunito({ weight: "700" });

export default function AgendaPage() {
  const router = useRouter();

  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);
  const [agendamentoSelecionado, setAgendamentoSelecionado] =
    useState<Agendamento | null>(null);

  const profissionalId = dados.idProfissional;

  function isoToDDMMYYYY(date: string) {
    const [year, month, day] = date.split("-");
    return `${day}-${month}-${year}`;
  }

  async function carregarAgendamentos() {
    try {
      const dto = await listarAgendamentos(profissionalId);

      const flatten: Agendamento[] = [];

      (dto || []).forEach((diaObj: DiaAgendamento) => {
        (diaObj.agendamentos || []).forEach(
          (a: AgendamentoResponse) => {
            flatten.push({
              id: a.atendimentoId,
              pacienteId: a.pacienteId,
              paciente: a.nomePaciente,
              horario: a.time,
              data: isoToDDMMYYYY(a.data),
              numeracao: a.numeroAtendimento ?? 0,
              status: a.status,
            });
          }
        );
      });

      setAgendamentos(flatten);
    } catch {
      toast.error("Erro ao carregar agendamentos.");
    }
  }

  /** 🔥 AQUI ESTÁ A CORREÇÃO */
  useEffect(() => {
    carregarAgendamentos();
  }, []);

  const agendamentosFiltrados = dataSelecionada
    ? agendamentos.filter((a) => a.data === dataSelecionada)
    : agendamentos;

  function agruparPorData(lista: Agendamento[]) {
    const grupos: Record<string, Agendamento[]> = {};

    lista.forEach((item) => {
      const [day, month, year] = item.data.split("-");
      const label = `${day}/${month}/${year}`;

      if (!grupos[label]) {
        grupos[label] = [];
      }

      grupos[label].push(item);
    });

    return grupos;
  }

  const gruposParaRenderizar = agruparPorData(agendamentosFiltrados);

  async function handleCreateAgendamento(data: AgendamentoFormData) {
    try {
      if (!data.pacienteId) {
        toast.error("Selecione um paciente válido.");
        return;
      }

      const dataFormatada = isoToDDMMYYYY(data.data);

      await criarAgendamento({
        profissionalId,
        pacienteId: data.pacienteId,
        data: dataFormatada,
        hora: data.horario,
        numeroAtendimento: Number(data.numeracao),
      });

      toast.success("Agendamento criado com sucesso!");
      await carregarAgendamentos();
      setOpen(false);
    } catch (error) {
      if (
        error instanceof Error &&
        error.message.includes("ja possui um agendamento")
      ) {
        toast.warning("Já existe um agendamento para essa data e horário.");
        return;
      }

      toast.error("Erro ao criar agendamento.");
    }
  }

  async function confirmarDeleteAgendamento() {
    if (!agendamentoSelecionado || !agendamentoSelecionado.pacienteId) return;

    try {
      await deletarAgendamento(
        profissionalId,
        agendamentoSelecionado.pacienteId,
        agendamentoSelecionado.id
      );

      setOpenDelete(false);
      setAgendamentoSelecionado(null);
      await carregarAgendamentos();
    } catch {
      toast.error("Erro ao cancelar agendamento.");
    }
  }

  return (
    <div className="min-h-screen w-full bg-[#F8FAFD]">
      <Header />

      <section className="px-5 pt-4 mx-auto w-full">
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => router.back()}
            className="h-[38px] px-4 rounded-full flex items-center gap-2 bg-[#EDF2FB] text-sm text-gray-700"
          >
            <ArrowLeft size={18} />
            Voltar
          </button>

          <div className="flex items-center gap-3">
            <Button
              onClick={() => setOpen(true)}
              className="hidden md:flex items-center bg-[#165BAA] text-white gap-2 px-4 h-[38px] rounded-full text-sm"
            >
              <CalendarPlus size={18} />
              Novo agendamento
            </Button>

            <Input
              type="date"
              value={dataSelecionada}
              onChange={(e) => setDataSelecionada(e.target.value)}
              className="bg-white border border-[#3B82F6] rounded-full w-[150px]"
            />
          </div>
        </div>
      </section>

      <section className="bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">
        <h1 className={`text-xl font-bold ${nunitoFont.className}`}>
          Agendamentos
        </h1>

        {Object.entries(gruposParaRenderizar).map(([data, itens]) => (
          <div key={data} className="flex flex-col gap-4">
            <h2 className="text-sm font-semibold">{data}</h2>
            <hr />
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {itens.map((item) => (
                <AgendamentoCard
                  key={item.id}
                  paciente={item.paciente}
                  horario={item.horario}
                  numeracao={item.numeracao}
                  status={item.status}
                  onDeleteClick={() => {
                    setAgendamentoSelecionado(item);
                    setOpenDelete(true);
                  }}
                />
              ))}
            </div>
          </div>
        ))}

        {agendamentosFiltrados.length === 0 && (
          <p className="text-center mt-20">
            Nenhum agendamento encontrado.
          </p>
        )}
      </section>

      <DeletarAgendamentoModal
        isOpen={openDelete}
        onClose={() => setOpenDelete(false)}
        onConfirm={confirmarDeleteAgendamento}
      />

      <AgendamentoModal open={open} onOpenChange={setOpen}>
        <AgendamentoForm onSubmit={handleCreateAgendamento} />
      </AgendamentoModal>
    </div>
  );
}
