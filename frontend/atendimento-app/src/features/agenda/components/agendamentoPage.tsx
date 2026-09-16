"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import {
  ArrowLeft,
  CalendarPlus,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import { Nunito } from "next/font/google";
import { toast } from "sonner";
import { isAxiosError } from "axios";

import Header from "@/components/shared/header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

import { AgendamentoModal } from "../components/agendamentoModal";
import { AgendamentoModalDeletar } from "../components/agendamentoModalDeletar";
import { AgendamentoModalConcluir } from "../components/agendamentoModalConcluir";
import AgendamentoForm, {
  AgendamentoFormData,
} from "../components/agendamentoForm";
import AgendamentoCard from "../components/agendamentoCard";

import { useAgendamentos } from "../hooks/useAgendamentos";
import { useCriarAgendamento } from "../hooks/useCriarAgendamento";
import { useDeletarAgendamento } from "../hooks/useDeletarAgendamento";
import { useConcluirAgendamento } from "../hooks/useConcluirAgendamento";
import { useEditarAgendamento } from "../hooks/useEditarAgendamento";

import { agruparPorData } from "../utils/agruparPorData";

import { Agendamento } from "../types";

import { isoParaBR } from "@/utils/formatarData";

const nunitoFont = Nunito({ weight: "700" });

function getTodayLocalDate() {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60000;
  return new Date(now.getTime() - offset).toISOString().split("T")[0];
}

export default function AgendamentoPage() {
  const router = useRouter();

  const [dataSelecionada, setDataSelecionada] = useState(getTodayLocalDate());
  const [page, setPage] = useState(1);
const size = 10;
  const [openCreate, setOpenCreate] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);
  const [openConcluir, setOpenConcluir] = useState(false);
  const [agendamentoSelecionado, setAgendamentoSelecionado] =
    useState<Agendamento | null>(null);

  const {
  data: agendamentosData,
  isLoading,
  isFetching,
} = useAgendamentos({
  data: dataSelecionada || undefined,
  page,
  size,
});

const agendamentos = agendamentosData?.agendamentos ?? [];
const pagination = agendamentosData?.pagination;

  const criarAgendamentoMutation = useCriarAgendamento();
  const editarAgendamentoMutation = useEditarAgendamento();
  const deletarAgendamentoMutation = useDeletarAgendamento();
  const concluirAgendamentoMutation = useConcluirAgendamento();


  const gruposParaRenderizar = agruparPorData(agendamentos);

  async function handleCreateAgendamento(data: AgendamentoFormData) {
    if (!data.pacienteId) {
      toast.error("Selecione um paciente válido.");
      return;
    }

    try {
      await criarAgendamentoMutation.mutateAsync({
        pacienteId: data.pacienteId,
        profissionalId: data.profissionalId,
        data: isoParaBR(data.data),
        hora: data.horario,
      });

      toast.success("Agendamento criado com sucesso!");
      setOpenCreate(false);
    } catch (error) {
      if (isAxiosError(error) && error.response) {
        const mensagemBackend = error.response.data.message || error.response.data.error || "Erro de validação";

        if (mensagemBackend.includes("ja possui um agendamento")) {
          toast.warning("Já existe um agendamento para essa data e horário.");
          return;
        }
        toast.error(`Falha: ${mensagemBackend}`);
        return;
      }
      toast.error("Erro ao criar agendamento.");
    }
  }

  async function handleEditAgendamento(data: AgendamentoFormData) {
    if (!agendamentoSelecionado) return;

    if (!data.pacienteId) {
      toast.error("Selecione um paciente válido.");
      return;
    }

    try {
      await editarAgendamentoMutation.mutateAsync({
        agendamentoId: agendamentoSelecionado.id,
        payload: {
          pacienteId: data.pacienteId,
          profissionalId: data.profissionalId,
          data: isoParaBR(data.data),
          hora: data.horario,
        },
      });

      toast.success("Agendamento atualizado com sucesso!");
      setOpenEdit(false);
      setAgendamentoSelecionado(null);
    } catch (error) {
      if (isAxiosError(error) && error.response) {
        const mensagemBackend = error.response.data.message || error.response.data.error || "Erro de validação";

        if (mensagemBackend.includes("ja possui um agendamento")) {
          toast.warning("Já existe um agendamento para essa data e horário.");
          return;
        }
        toast.error(`Falha: ${mensagemBackend}`);
        return;
      }
      toast.error("Erro ao atualizar agendamento.");
    }
  }

  async function confirmarDeleteAgendamento() {
    if (!agendamentoSelecionado) return;

    try {
      await deletarAgendamentoMutation.mutateAsync({
        pacienteId: agendamentoSelecionado.pacienteId,
        agendamentoId: agendamentoSelecionado.id,
      });

      toast.success("Agendamento cancelado.");
      setOpenDelete(false);
      setAgendamentoSelecionado(null);
    } catch {
      toast.error("Erro ao cancelar agendamento.");
    }
  }

  async function confirmarConcluirAgendamento() {
    if (!agendamentoSelecionado) return;

    try {
      await concluirAgendamentoMutation.mutateAsync({
        pacienteId: agendamentoSelecionado.pacienteId,
        agendamentoId: agendamentoSelecionado.id,
      });

      toast.success("Agendamento concluído com sucesso!");
      setOpenConcluir(false);
      setAgendamentoSelecionado(null);
    } catch {
      toast.error("Erro ao concluir agendamento.");
    }
  }

  return (
    <div className="min-h-screen w-full bg-[#F8FAFD]">
      <Header />

      <section className="px-5 pt-4 mx-auto w-full">
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => router.back()}
            className="h-9.5 px-4 rounded-full flex items-center gap-2 bg-[#EDF2FB] text-sm text-gray-700 cursor-pointer"
          >
            <ArrowLeft size={18} />
            Voltar
          </button>

          <div className="flex items-center gap-3">
            <Button
              onClick={() => setOpenCreate(true)}
              className="hidden md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-9.5 rounded-full text-sm cursor-pointer"
            >
              <CalendarPlus size={18} />
              Novo agendamento
            </Button>

            <Input
              type="date"
              value={dataSelecionada}
              onChange={(e) => {
                setDataSelecionada(e.target.value);
                setPage(1);
}}
              className="bg-white border border-[#3B82F6] rounded-full w-37.5 text-gray-600 text-sm"
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

        {isLoading && (
          <p className="text-sm text-gray-500">Carregando agendamentos...</p>
        )}

        {Object.entries(gruposParaRenderizar).map(([dataCompleta, itens]) => (
          <div key={dataCompleta} className="flex flex-col gap-4">
            <h2 className="text-sm font-semibold text-[#344054]">
              {dataCompleta}
            </h2>

            <hr className="border-[#E5E7EB]" />

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {itens.map((item) => (
                <AgendamentoCard
                  id={item.id}
                  key={item.id}
                  paciente={item.paciente}
                  horario={item.horario}
                  numeroAtendimento={item.numeracao}
                  status={item.status}
                  externo={item.externo}
                  onEditClick={() => {
                    setAgendamentoSelecionado(item);
                    setOpenEdit(true);
                  }}
                  onConcluirClick={() => {
                    setAgendamentoSelecionado(item);
                    setOpenConcluir(true);
                  }}
                  onDeleteClick={() => {
                    setAgendamentoSelecionado(item);
                    setOpenDelete(true);
                  }}
                />
              ))}
            </div>
          </div>
        ))}

        {agendamentos.length === 0 && !isLoading && (
          <div className="text-center mt-20">
            <p className="text-[#344054] text-[15px] font-medium">
              Nenhum agendamento encontrado.
            </p>

            {dataSelecionada && (
              <Button
                variant="link"
                onClick={() => {
  setDataSelecionada("");
  setPage(1);
}}
                className="text-[#165BAA] underline text-sm"
              >
                Limpar filtro
              </Button>
            )}
          </div>
        )}
{!isLoading && pagination && pagination.totalElements > 0 && (
  <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-6 border-t border-gray-100 pt-6 px-2 w-full">
    <span className="text-sm text-gray-500 font-medium">
      Página {pagination.page} de {pagination.totalPages} (
      {pagination.totalElements} agendamentos)
    </span>

    <div className="flex gap-3">
      <Button
        variant="outline"
        onClick={() => setPage((p) => Math.max(1, p - 1))}
        disabled={pagination.first || isFetching}
        className="rounded-full shadow-sm hover:bg-gray-50 flex items-center"
      >
        <ChevronLeft className="h-4 w-4 mr-1" />
        Anterior
      </Button>

      <Button
        variant="outline"
        onClick={() => setPage((p) => p + 1)}
        disabled={pagination.last || isFetching}
        className="rounded-full shadow-sm hover:bg-gray-50 flex items-center"
      >
        Próxima
        <ChevronRight className="h-4 w-4 ml-1" />
      </Button>
    </div>
  </div>
)}
        <AgendamentoModal open={openCreate} onOpenChange={setOpenCreate}>
          <AgendamentoForm
            onSubmit={handleCreateAgendamento}
          />
        </AgendamentoModal>

        <AgendamentoModal open={openEdit} onOpenChange={(open) => {
          setOpenEdit(open);
          if (!open) setAgendamentoSelecionado(null);
        }}>
          {agendamentoSelecionado && (
            <AgendamentoForm
              isEditing
              initialData={{
                pacienteId: agendamentoSelecionado.pacienteId,
                pacienteNome: agendamentoSelecionado.paciente,
                profissionalId: agendamentoSelecionado.profissionalId,
                profissionalNome: agendamentoSelecionado.nomeProfissional,
                data: agendamentoSelecionado.data.split('-').reverse().join('-'),
                horario: agendamentoSelecionado.horario,
              }}
              onSubmit={handleEditAgendamento}
            />
          )}
        </AgendamentoModal>
      </section>

      <button
        onClick={() => setOpenCreate(true)}
        className="fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA] flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)] md:hidden"
        aria-label="Novo agendamento"
      >
        <CalendarPlus size={28} className="text-white" />
      </button>

      <AgendamentoModalDeletar
        isOpen={openDelete}
        onClose={() => {
          setOpenDelete(false);
          setAgendamentoSelecionado(null);
        }}
        onConfirm={confirmarDeleteAgendamento}
      />

      <AgendamentoModalConcluir
        isOpen={openConcluir}
        onClose={() => {
          setOpenConcluir(false);
          setAgendamentoSelecionado(null);
        }}
        onConfirm={confirmarConcluirAgendamento}
      />
    </div>
  );
}