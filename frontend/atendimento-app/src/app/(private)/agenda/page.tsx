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

import {
  listarAgendamentos,
  criarAgendamento,
  deletarAgendamento,
} from "@/api/agendamento";

import dados from "../../../../data/verificacao.json";

interface Agendamento {
  id: string;
  pacienteId?: string;
  paciente: string;
  horario: string;
  data: string;
  numeracao: number;
}

const nunitoFont = Nunito({ weight: "700" });

export default function AgendaPage() {
  const router = useRouter();

  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);

  const profissionalId = dados.idProfissional;

  const [openDelete, setOpenDelete] = useState(false);
  const [agendamentoSelecionado, setAgendamentoSelecionado] =
    useState<Agendamento | null>(null);

  async function carregarAgendamentos() {
    try {
      const dto = await listarAgendamentos(profissionalId);

      const flatten: Agendamento[] = [];

      (dto || []).forEach((diaObj: any) => {
        const dia = diaObj.dia;

        (diaObj.agendamentos || []).forEach((a: any) => {
          flatten.push({
            id: String(a.atendimentoId),
            pacienteId: String(a.pacienteId),
            paciente: a.nomePaciente,
            horario: a.time,
            data: dia,
            numeracao: a.numeroAtendimento ?? 0,
          });
        });
      });

      setAgendamentos(flatten);
    } catch (err) {
      console.error("Erro ao carregar agendamentos", err);
      alert("Erro ao carregar agendamentos.");
    }
  }

  useEffect(() => {
    carregarAgendamentos();
  }, []);

  const agendamentosFiltrados = dataSelecionada
    ? agendamentos.filter((a) => a.data === dataSelecionada)
    : agendamentos;

  const gruposParaRenderizar = dataSelecionada
    ? { [dataSelecionada]: agendamentosFiltrados }
    : agendamentos.reduce<Record<string, Agendamento[]>>((acc, cur) => {
        acc[cur.data] = acc[cur.data] || [];
        acc[cur.data].push(cur);
        return acc;
      }, {});

  async function handleCreateAgendamento(data: AgendamentoFormData) {
    try {
      if (!data.pacienteId) {
        alert("Selecione um paciente válido.");
        return;
      }

      const horaCorrigida =
        data.horario.length === 5
          ? `${data.horario}:00`
          : data.horario;

      await criarAgendamento({
        profissionalId,
        pacienteId: data.pacienteId,
        data: data.data,
        hora: horaCorrigida,
        numeroAtendimento: Number(data.numeracao),
      });

      await carregarAgendamentos();
      setOpen(false);
    } catch (error) {
      console.error("Erro ao criar agendamento", error);
    }

    const payload = {
      profissionalId,
      pacienteId: data.pacienteId,
      data: data.data,
      hora:
        data.horario.length === 5
          ? `${data.horario}:00`
          : data.horario,
      numeroAtendimento: Number(data.numeracao),
    };

    console.log("Payload enviado:", payload);
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
    } catch (err) {
      console.error("Erro ao deletar agendamento", err);
      alert("Erro ao cancelar agendamento.");
    }
  }

  async function handleDeletarAgendamento(ag: Agendamento) {
    try {
      if (!ag.pacienteId) {
        alert("Agendamento sem pacienteId — não foi possível deletar.");
        return;
      }

      await deletarAgendamento(profissionalId, ag.pacienteId, ag.id);
      await carregarAgendamentos();
    } catch (err) {
      console.error("Erro ao deletar agendamento", err);
      alert("Erro ao deletar agendamento.");
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
              className="hidden md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-[38px] rounded-full text-sm"
            >
              <CalendarPlus size={18} />
              Novo agendamento
            </Button>

            <Input
              type="date"
              value={dataSelecionada}
              onChange={(e) => setDataSelecionada(e.target.value)}
              className="bg-white border border-[#3B82F6] rounded-full w-[150px] text-gray-600 text-sm"
            />
          </div>
        </div>
      </section>

      <section className="bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">
        <h1
          className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}
        >
          Agendamentos
        </h1>

        {Object.entries(gruposParaRenderizar).map(([data, itens]) => (
          <div key={data} className="flex flex-col gap-4">
            <h2 className="text-sm font-semibold text-[#344054]">
              {new Date(data.split('-').reverse().join('-')).toLocaleDateString("pt-BR")}
            </h2>

            <hr className="border-[#E5E7EB]" />

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {itens.map((item) => (
                <AgendamentoCard
                  key={item.id}
                  paciente={item.paciente}
                  horario={item.horario}
                  numeracao={item.numeracao}
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
          <div className="text-center mt-20">
            <p className="text-[#344054] text-[15px] font-medium">
              Nenhum agendamento encontrado.
            </p>

            {dataSelecionada && (
              <Button
                variant="link"
                onClick={() => setDataSelecionada("")}
                className="text-[#165BAA] underline text-sm"
              >
                Limpar filtro
              </Button>
            )}
          </div>
        )}

        <AgendamentoModal open={open} onOpenChange={setOpen}>
          <AgendamentoForm onSubmit={handleCreateAgendamento} />
        </AgendamentoModal>
      </section>

      <button
        onClick={() => setOpen(true)}
        className="fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA] flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)] md:hidden"
      >
        <CalendarPlus size={28} className="text-white" />
      </button>

      <DeletarAgendamentoModal
        isOpen={openDelete}
        onClose={() => {
          setOpenDelete(false);
          setAgendamentoSelecionado(null);
        }}
        onConfirm={confirmarDeleteAgendamento}
      />
    </div>
  );
}