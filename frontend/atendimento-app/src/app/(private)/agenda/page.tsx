"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { ArrowLeft, CalendarPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { AgendamentoModal } from "@/components/modals/agendamentoModal";
import AgendamentoForm from "@/components/forms/agendamentoForm";
import type { AgendamentoFormData } from "@/components/forms/agendamentoForm";
import AgendamentoCard from "@/components/cards/agendamentoCard";

interface Agendamento {
  id: number;
  paciente: string;
  horario: string;
  data: string;
  numeracao: number;
}

const nunitoFont = Nunito({ weight: "700" });

export default function AgendaPage() {
  const router = useRouter();

  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([
    {
      id: 1,
      paciente: "Fulano da Silva",
      numeracao: 1,
      data: "2025-11-24",
      horario: "14:00",
    },
    {
      id: 2,
      paciente: "Teste da Silva",
      numeracao: 2,
      data: "2025-12-07",
      horario: "16:20",
    },
    {
      id: 3,
      paciente: "Maria Oliveira",
      numeracao: 3,
      data: "2025-11-24",
      horario: "15:30",
    },
  ]);


  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);

  function agruparPorData(lista: Agendamento[]) {
    const grupos: Record<string, Agendamento[]> = {};

    lista.forEach((item) => {
      if (!grupos[item.data]) {
        grupos[item.data] = [];
      }
      grupos[item.data].push(item);
    });

    return grupos;
  }

  const agendamentosFiltrados = 
  dataSelecionada
    ? agendamentos.filter((a) => a.data === dataSelecionada)
    : agendamentos;

  const gruposParaRenderizar = dataSelecionada
    ? { [dataSelecionada]: agendamentosFiltrados }
    : agruparPorData(agendamentos);

  // Recebe o envio do form
  function handleCreateAgendamento(data: AgendamentoFormData) {
    console.log("Novo agendamento:", data);

    // futuramente:
    // await api.post("/agendamentos", data);

    setOpen(false);
  }

  return (
    <div className="min-h-screen w-full bg-[#F8FAFD]">
      <Header />

      <section className="px-5 pt-4 mx-auto w-full">
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => router.back()}
            className="h-[38px] px-4 rounded-full flex items-center gap-2 bg-[#EDF2FB] text-sm text-gray-700 cursor-pointer"
          >
            <ArrowLeft size={18} />
            Voltar
          </button>

          <div className="flex items-center gap-3">
            <Button
              onClick={() => setOpen(true)}
              className="hidden cursor-pointer md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-[38px] rounded-full text-sm shadow-sm active:scale-95"
            >
              <CalendarPlus size={18} />
              Novo agendamento
            </Button>

            <Input
              type="date"
              value={dataSelecionada}
              onChange={(e) => setDataSelecionada(e.target.value)}
              className="bg-white border border-[#3B82F6] rounded-full w-[150px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]"
            />
          </div>
        </div>
      </section>

      <section className="bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">
        <h1 className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}>
          Agendamentos
        </h1>

        {Object.entries(gruposParaRenderizar).map(([data, itens]) => (
          <div key={data} className="flex flex-col gap-4">
            <h2 className="text-sm font-semibold text-[#344054]">
              {new Date(data).toLocaleDateString("pt-BR")}
            </h2>

            <hr className="border-[#E5E7EB]" />

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {itens.map((item) => (
                <AgendamentoCard
                  key={item.id}
                  paciente={item.paciente}
                  horario={item.horario}
                  numeracao={item.numeracao}
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
        className="
          fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA]
          flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)]
          active:scale-95 md:hidden"
      >
        <CalendarPlus size={28} className="text-white" />
      </button>
    </div>
  );
}
