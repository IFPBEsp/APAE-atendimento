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
import { CalendarClock, Search, AlertCircle } from "lucide-react";
import { PacienteCard } from "@/features/home/components/pacienteCard";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { useHome } from "../hooks/useHome";


export default function HomePage() {
  const router = useRouter();

  const {
    medicoNome,
    pacientes,
    loading,
    erro,
    busca,
    setBusca,
    filtro,
    setFiltro,
  } = useHome();

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
                value={busca}
                onChange={(e) => setBusca(e.target.value)}
                className="bg-white border border-[#3B82F6] pl-9 pr-3 py-2 rounded-full text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]"
              />
              <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
            </div>

            <Select onValueChange={(value) => setFiltro(value as "nome" | "cpf" | "cidade" | "")}>
              <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full w-[130px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6] cursor-pointer">
                <SelectValue placeholder="Filtrar por..." />
              </SelectTrigger>
              <SelectContent className="border border-[#3B82F6] rounded-xl">
                <SelectItem className="cursor-pointer" value="nome">Nome</SelectItem>
                <SelectItem className="cursor-pointer" value="cpf">CPF</SelectItem>
                <SelectItem className="cursor-pointer" value="cidade">Cidade</SelectItem>
              </SelectContent>
            </Select>

            <Button
              onClick={() => router.push("/agenda")}
              className="hidden cursor-pointer md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-[38px]  rounded-full text-sm shadow-sm active:scale-95"
            >
              <CalendarClock size={18} />
              Agenda
            </Button>
          </div>
        </section>

        <section className="w-full bg-white rounded-t-3xl p-6 flex flex-col gap-4 sm:grid sm:grid-cols-2 sm:gap-6 min-h-[400px]">
          {loading && 
            <p className="col-span-2 flex flex-col items-center justify-center text-lg font-medium text-gray-600">Carregando pacientes...</p>
          } 
          
          {erro && (
            <div className="col-span-2 flex flex-col items-center justify-center py-12 gap-3">
              <AlertCircle size={48} className="text-red-500" />
              <p className="text-red-600 text-lg font-medium">Erro ao carregar pacientes</p>
              <Button
                onClick={() => window.location.reload()}
                className="mt-2 bg-[#165BAA] hover:bg-[#13447D] text-white"
              >
                Tentar novamente
              </Button>
            </div>
          )}

          {!loading && !erro && pacientes.length === 0 && (
            <p className="col-span-2 flex flex-col items-center justify-center text-lg font-medium text-gray-600">
              Nenhum paciente encontrado
            </p>
          )}
          
          {!loading &&
            !erro &&
            pacientes.map((pac) => (
                <PacienteCard
                    key={pac.id}
                    {...pac}
                    onViewAtendimentos={() => router.push(`/atendimento/${pac.id}`)}
                    onViewRelatorios={() => router.push(`/relatorio/${pac.id}`)}
                    onViewAnexos={() => router.push(`/anexo/${pac.id}`)}
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