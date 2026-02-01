"use client";

import { useRouter } from "next/navigation";
import { useState, useMemo } from "react";
import { ArrowLeft, CalendarPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import { toast } from "sonner";

import Header from "@/components/shared/header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

import { AgendamentoModal } from "../components/agendamentoModal";
import { AgendamentoModalDeletar } from "../components/agendamentoModalDeletar";
import AgendamentoForm, {
  AgendamentoFormData,
} from "../components/agendamentoForm";
import AgendamentoCard from "../components/agendamentoCard";

import { useAgendamentos } from "../hooks/useAgendamentos";
import { useCriarAgendamento } from "../hooks/useCriarAgendamento";
import { useDeletarAgendamento } from "../hooks/useDeletarAgendamento";

import { agruparPorData } from "../utils/agruparPorData";

import { Agendamento } from "../types";
import dados from "../../../../data/verificacao.json";
import { isoParaBR } from "@/utils/formatarData";

const nunitoFont = Nunito({ weight: "700" });

export default function AgendamentoPage() {
  const router = useRouter();

  const [dataSelecionada, setDataSelecionada] = useState("");
  const [openCreate, setOpenCreate] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);
  const [agendamentoSelecionado, setAgendamentoSelecionado] =
    useState<Agendamento | null>(null);

  const { data: agendamentos = [], isLoading } = useAgendamentos(
    dados.idProfissional,
  );

  const criarAgendamentoMutation = useCriarAgendamento();
  const deletarAgendamentoMutation = useDeletarAgendamento();

  const agendamentosFiltrados = useMemo(() => {
    if (!dataSelecionada) return agendamentos;
    const dataBR = isoParaBR(dataSelecionada);
    return agendamentos.filter((a) => a.data === dataBR);
  }, [agendamentos, dataSelecionada]);

  const gruposParaRenderizar = useMemo(
    () => agruparPorData(agendamentosFiltrados),
    [agendamentosFiltrados],
  );

  async function handleCreateAgendamento(data: AgendamentoFormData) {
    if (!data.pacienteId) {
      toast.error("Selecione um paciente válido.");
      return;
    }

    try {
      await criarAgendamentoMutation.mutateAsync({
        profissionalId: dados.idProfissional,
        pacienteId: data.pacienteId,
        data: isoParaBR(data.data),
        hora: data.horario,
      });

      toast.success("Agendamento criado com sucesso!");
      setOpenCreate(false);
    } catch (error) {
      if (error instanceof Error) {
        if (error.message.includes("ja possui um agendamento")) {
          toast.warning("Já existe um agendamento para essa data e horário.");
          return;
        }
      }

      toast.error("Erro ao criar agendamento.");
    }
  }

  async function confirmarDeleteAgendamento() {
    if (!agendamentoSelecionado) return;

    try {
      await deletarAgendamentoMutation.mutateAsync({
        profissionalId: dados.idProfissional,
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
              onChange={(e) => setDataSelecionada(e.target.value)}
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
                  key={item.id}
                  paciente={item.paciente}
                  horario={item.horario}
                  numeroAtendimento={item.numeracao}
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

        {agendamentosFiltrados.length === 0 && !isLoading && (
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

        <AgendamentoModal open={openCreate} onOpenChange={setOpenCreate}>
          <AgendamentoForm
            agendamentos={agendamentos}
            onSubmit={handleCreateAgendamento}
          />
        </AgendamentoModal>
      </section>

      <button
        onClick={() => setOpenCreate(true)}
        className="fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA] flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)] md:hidden"
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
    </div>
  );
}
