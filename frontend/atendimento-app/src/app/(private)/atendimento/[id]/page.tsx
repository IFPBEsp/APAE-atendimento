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
import { AtendimentoModal } from "@/components/modals/novoAtendimentoModal";
import { useEffect, useState } from "react";
import { Atendimento } from "@/types/Atendimento";
import { getAtendimentos } from "@/api/dadosAtendimentos";
import { getPacientes } from "@/api/dadosPacientes";
import { Paciente } from "@/types/Paciente";

const nunitoFont = Nunito({ weight: "700" });

export default function AtendimentoPage() {
  const [paciente, setPaciente] = useState<Paciente | null>(null);
  const router = useRouter();
  const { id: pacienteId } = useParams();

  const [atendimentos, setAtendimentos] = useState<Atendimento[]>([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    async function fetchData() {
      if (typeof pacienteId !== "string") return;
      try {
        const pacientes = await getPacientes();
        const nomePaciente = pacientes.find(
          (p) => String(p.id) === String(pacienteId)
        );

        if (nomePaciente) {
          setPaciente(nomePaciente);
        }

        const raw = await getAtendimentos(pacienteId);

        const todos = raw.flatMap((grupo) => grupo.atendimentos);

        const convertidos: Atendimento[] = todos.map((item) => {
          const [dia, mes, ano] = item.data.split("-");
          const dataBR = `${dia}/${mes}/${ano}`;

          const [hora, minuto] = item.hora.split(":");
          const horaFormatada = `${hora}:${minuto}`;

          return {
            id: item.id,
            data: dataBR,
            hora: horaFormatada,
            numeracao: item.numeracao ?? 1,
            relatorio: Object.entries(item.relatorio ?? {}).map(
              ([titulo, descricao]) => ({
                titulo,
                descricao,
              })
            ),
          };
        });
        console.log(convertidos);
        setAtendimentos(convertidos);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }

    fetchData();
  }, [pacienteId]);

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

            <Select>
              <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full w-[130px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6] cursor-pointer">
                <SelectValue placeholder="Filtrar por..." />
              </SelectTrigger>
              <SelectContent className="border border-[#3B82F6] rounded-xl">
                <SelectItem className="cursor-pointer" value="todos">
                  Todos
                </SelectItem>
                <SelectItem className="cursor-pointer" value="numeracao">
                  Numeração
                </SelectItem>
                <SelectItem className="cursor-pointer" value="data">
                  Data
                </SelectItem>
              </SelectContent>
            </Select>
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

        {!loading && atendimentos.length === 0 && (
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
            onCreated={(novo) => {
              setAtendimentos((prev) => [novo, ...prev]);
              setOpen(false);
            }}
          />
        </AtendimentoModal>

        {Object.entries(atendimentosPorMes).map(([mes, itens]) => (
          <div key={mes} className="flex flex-col gap-4">
            <h2 className="text-lg font-bold text-[#344054] capitalize">
              {mes}
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {itens.map((a) => (
                <AtendimentoCard
                  key={a.id}
                  {...a}
                  onDeleted={(id) => {
                    setAtendimentos((prev) =>
                      prev.filter((item) => item.id !== id)
                    );
                  }}
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
