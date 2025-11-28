"use client";

import Header from "@/components/shared/header";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { CalendarClock, Search } from "lucide-react";
import { PacienteCard } from "@/components/cards/pacienteCard";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";

export default function PacientesPage() {
  const router = useRouter();

  const [medicoNome, setMedicoNome] = useState("Fulano da silva");

  const pacientes = [
    {
      id: "1",
      nome: "Fulano de Tal da Silva Santos",
      cpf: "123.456.789-00",
      endereco: "Esperança - PB, R. Hugo Feitosa Figueiredo, 76, Centro.",
      contato: "(83) 9 1234-5678",
      dataNascimento: "10/01/2001",
      transtornos: ["Autismo", "TDAH"],
      responsaveis: ["Fulano da Silva", "Cicrano de Tal"],
    },
    {
      id: "2",
      nome: "Maria das Dores Souza",
      cpf: "987.654.321-00",
      endereco: "Esperança - PB, Rua Principal, 123.",
      contato: "(83) 9 9999-8888",
      dataNascimento: "15/02/1998",
      transtornos: ["Autismo", "TDAH"],
      responsaveis: ["Fulano da Silva", "Cicrano de Tal"],
    },
    {
      id: "3",
      nome: "Ana da Silva Oliveira",
      cpf: "987.654.321-00",
      endereco: "Esperança - PB, Rua Principal, 123.",
      contato: "(83) 9 9999-8888",
      dataNascimento: "15/02/1998",
      transtornos: ["Autismo", "TDAH"],
      responsaveis: ["Fulano da Silva", "Cicrano de Tal"],
    },
  ];

  return (
    <>
      <Header />
      <main className="bg-[#F8FAFD] flex flex-col items-center text-center">
        <section className="w-full px-4 sm:px-8 mt-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between pb-5">
          <div className="text-center flex flex-col items-center sm:flex-row sm:items-center sm:gap-2 sm:text-left">
            <h1 className="font-semibold text-[#344054] text-xl">
              Olá {medicoNome}!
            </h1>
            <h1 className="font-semibold text-[#344054] text-lg sm:text-xl sm:inline-block">
              Esses são seus pacientes
            </h1>
          </div>

          <div className="flex flex-row justify-between gap-2 w-full max-w-md sm:w-auto sm:justify-end">
            <div className="relative flex-1 sm:w-64">
              <Input
                type="text"
                placeholder="Pesquisar por paciente"
                className="bg-white border border-[#3B82F6] pl-9 pr-3 py-2 rounded-full text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]"
              />
              <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
            </div>

            <Select>
              <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full w-[130px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]">
                <SelectValue placeholder="Filtrar por..." />
              </SelectTrigger>
              <SelectContent className="border border-[#3B82F6] rounded-xl">
                <SelectItem value="todos">Todos</SelectItem>
                <SelectItem value="nome">Nome</SelectItem>
                <SelectItem value="cpf">CPF</SelectItem>
                <SelectItem value="cidade">Cidade</SelectItem>
              </SelectContent>
            </Select>

            <Button
              onClick={() =>  router.push("/agenda")}
              className="hidden cursor-pointer md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-[38px]  rounded-full text-sm shadow-sm active:scale-95"
            >
              <CalendarClock size={18} />
              Agenda
            </Button>
          </div>
        </section>

        <section className="w-full bg-white rounded-t-3xl p-6 flex flex-col gap-4 sm:grid sm:grid-cols-2 sm:gap-6">
          {pacientes.map((pac) => (
            <PacienteCard
              key={pac.id}
              {...pac}
              onViewAtendimentos={() => router.push(`/atendimento/${pac.id}`)}
              onViewRelatorios={() => router.push(`/relatorio/${pac.id}`)}
            />
          ))}
        </section>

        <button
          onClick={() => router.push("/agenda")}
          className="
              fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA]
              flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)]
              active:scale-95 md:hidden"
        >
          <CalendarClock size={28} className="text-white" />
        </button>
      </main>
    </>
  );
}
