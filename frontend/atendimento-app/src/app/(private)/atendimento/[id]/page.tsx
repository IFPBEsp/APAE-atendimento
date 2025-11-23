"use client";

import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, Plus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import AtendimentoCard from "@/components/cards/atendimentoCard";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import AtendimentoForm from "@/components/forms/atendimentoForm";
import { AtendimentoModal } from "@/components/modals/atendimentoModal";
import { useState } from "react";
import type { AtendimentoFormData } from "@/components/forms/atendimentoForm";

interface Atendimento {
  id: number;
  data: string;
  numeracao: number;
  titulo: string;
  descricao: string;
}

const nunitoFont = Nunito({ weight: "700" });

export default function AtendimentoPage() {
  const router = useRouter();
  const { id } = useParams();

  const atendimentos: Atendimento[] = [];
  /*{ id: 1, 
    data: "01/11/2025", 
    numeracao: 1, 
    titulo: "Título do tópico", 
    descricao: "Nullam varius tempor massa et iaculis. Praesent sodales orci ut ultrices tempor. 
    Quisque ac mauris gravida, dictum ipsum sit amet, bibendum turpis...", 
  }, 
  { id: 2, 
    data: "28/10/2025", 
    numeracao: 2, 
    titulo: "Outro atendimento", 
    descricao: "Nullam varius tempor massa et iaculis. Praesent sodales orci ut ultrices tempor. 
    Quisque ac mauris gravida, dictum ipsum sit amet, bibendum turpis...", 
  }, 
  { id: 3, 
    data: "01/10/2025", 
    numeracao: 3, 
    titulo: "Título do tópico", 
    descricao: "Nullam varius tempor massa et iaculis. Praesent sodales orci ut ultrices tempor. 
    Quisque ac mauris gravida, dictum ipsum sit amet, bibendum turpis...", 
  },*/
  function agruparPorMes(lista: Atendimento[]) {
    const meses: Record<string, Atendimento[]> = {};

    lista.forEach((item) => {
      const [, mes, ano] = item.data.split("/");

      const nomeMes = new Date(Number(ano), Number(mes) - 1).toLocaleString(
        "pt-BR",
        { month: "long" }
      );

      const chave = `${nomeMes} ${ano}`;

      if (!meses[chave]) meses[chave] = [];
      meses[chave].push(item);
    });

    return meses;
  }

  const atendimentosPorMes = agruparPorMes(atendimentos);
  const nomePaciente = "Fulano de Tal de Lorem Ipsum da Silva Santos";

  const [open, setOpen] = useState(false);

  //Recebe os dados do formulário:
  function handleCreateAtendimento(data: AtendimentoFormData) {
    console.log("Novo atendimento recebido:", data);

    // Mudar futuramente:
    // await api.post("/rota", data);
    // depois atualiza a lista

    setOpen(false);
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
              className="hidden cursor-pointer md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-[38px]  rounded-full text-sm shadow-sm active:scale-95"
            >
              <Plus size={18} />
              Novo atendimento
            </Button>

            <Select>
              <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full w-[130px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]">
                <SelectValue placeholder="Filtrar por..." />
              </SelectTrigger>
              <SelectContent className="border border-[#3B82F6] rounded-xl">
                <SelectItem value="todos">Todos</SelectItem>
                <SelectItem value="numeracao">Numeração</SelectItem>
                <SelectItem value="data">Data</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </section>

      <section className="bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">
        <h1
          className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}
        >
          {nomePaciente}
        </h1>

        {atendimentos.length === 0 && (
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
          <AtendimentoForm onSubmit={handleCreateAtendimento} />
        </AtendimentoModal>

        {Object.entries(atendimentosPorMes).map(([mes, itens]) => (
          <div key={mes} className="flex flex-col gap-4">
            <h2 className="text-lg font-bold text-[#344054] capitalize">
              {mes}
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {itens.map((a) => (
                <AtendimentoCard key={a.id} {...a} />
              ))}
            </div>
          </div>
        ))}
      </section>

      <button
        onClick={() => setOpen(true)}
        className="
            fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA]
            flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)]
            active:scale-95 md:hidden"
      >
        <Plus size={28} className="text-white" />
      </button>
    </div>
  );
}
