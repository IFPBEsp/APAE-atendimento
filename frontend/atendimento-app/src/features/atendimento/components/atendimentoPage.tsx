"use client";

import Header from "@/components/shared/header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowLeft, Plus } from "lucide-react";
import { Nunito } from "next/font/google";
import { useRouter, useParams } from "next/navigation";

import { AtendimentoModal } from "@/features/atendimento/components/atendimentoNovoModal";
import AtendimentoCard from "../components/atendimentoCard";
import AtendimentoForm from "@/features/atendimento/components/atendimentoForm";
import { useAtendimentos } from "../hooks/useAtendimentos";

const nunitoFont = Nunito({ weight: "700" });

export default function AtendimentoPage() {
  const router = useRouter();
  const { id: pacienteId } = useParams();

  const {
    paciente,
    atendimentos,
    loading,
    dataSelecionada,
    setDataSelecionada,
    atendimentosFiltrados,
    atendimentosAgrupados,
    adicionarAtendimento,
    atualizarAtendimento,
    open,
    setOpen,
  } = useAtendimentos(pacienteId as string);

  const gruposParaRenderizar = atendimentosAgrupados;

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
              <Plus size={18} />
              Novo atendimento
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
        <h1
          className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}
        >
          {paciente?.nomeCompleto}
        </h1>

        {loading && (
          <p className="text-center text-gray-500 mt-20">
            Carregando atendimentos...
          </p>
        )}

        {!loading && atendimentosFiltrados.length === 0 && (
          <div className="text-center mt-20">
            <p
              className={` text-[#344054] text-[15px] font-medium ${nunitoFont.className}`}
            >
              Não existem atendimentos para este paciente.
            </p>
            <Button
              variant="link"
              onClick={() => setOpen(true)}
              className="text-[#165BAA] underline text-sm hover:opacity-80 pt-0 cursor-pointer"
            >
              Criar novo atendimento.
            </Button>
          </div>
        )}

        <AtendimentoModal open={open} onOpenChange={setOpen}>
          <AtendimentoForm
            atendimentos={atendimentos}
            onCreated={adicionarAtendimento}
          />
        </AtendimentoModal>

        {Object.entries(gruposParaRenderizar).map(([mes, itens]) => (
          <div key={mes} className="flex flex-col gap-4">
            <h2 className="text-lg font-bold text-[#344054] capitalize">
              {mes}
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {itens.map((a) => (
                <AtendimentoCard
                  key={a.id}
                  {...a}
                  atendimentos={atendimentos}
                  onUpdated={atualizarAtendimento}
                />
              ))}
            </div>
          </div>
        ))}
      </section>

      <button
        onClick={() => setOpen(true)}
        className="fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA] flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)] active:scale-95 md:hidden"
      >
        <Plus size={28} className="text-white" />
      </button>
    </div>
  );
}
