"use client";

import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, Plus } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import Header from "@/components/shared/header";
import AtendimentoCard from "@/components/atendimentos/atendimentoCard";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

const nunitoFont = Nunito({ weight: "700" });
const baloo2Font = Baloo_2({ weight: "500" });

export default function AtendimentoPage() {
    const router = useRouter();
    const { id } = useParams();

    const atendimentos: any[] = [];
    const nomePaciente = "Fulano de Tal de Lorem Ipsum da Silva Santos";

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

              <button
                onClick={() => console.log("Implementar novo atendimento")}
                className="hidden md:flex items-center bg-[#165BAA] text-white gap-2 px-4 h-[38px]  rounded-full text-sm shadow-sm active:scale-95"
              >
                <Plus size={18} />
                Novo atendimento
              </button>

              <Select>
                <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full w-[130px] text-gray-600 text-sm">
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


        <section className="w-full bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">

          <h1 className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}>
            {nomePaciente}
          </h1>

          {atendimentos.length === 0 && (
            <div className="text-center mt-20">
              <p
                className={`text-[#344054] text-[15px] font-medium mb-3  ${nunitoFont.className}`}
              >
                Não existem atendimentos para
                <br />
                este paciente.
              </p>

              <button
                onClick={() => console.log("Implementar novo atendimento")}
                className="text-[#165BAA] underline text-sm hover:opacity-80"
              >
                Criar novo atendimento.
              </button>
            </div>
          )}

          {atendimentos.length > 0 && (
            <div className="flex flex-col gap-4">
              {atendimentos.map((a) => (
                <AtendimentoCard key={a.id} {...a} />
              ))}
            </div>
          )}
        </section>

        <button
          onClick={() => console.log("Implementar novo atendimento")}
          className="
            fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA]
            flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)]
            active:scale-95 md:hidden
          "
        >
          <Plus size={28} className="text-white" />
        </button>
      </div>
    );
}