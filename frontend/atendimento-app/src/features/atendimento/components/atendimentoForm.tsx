"use client";

import { useState, useRef } from "react";
import { useParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Check, Pencil, Trash2, Clock, Calendar } from "lucide-react";
import { toast } from "sonner";
import { useQueryClient } from "@tanstack/react-query";
import { isoParaBR, brParaISO } from "@/utils/formatarData";
import { criarAtendimento, editarAtendimento } from "../services/atendimentoService";
import { Atendimento, AtendimentoPayload } from "../types";

interface AtendimentoFormProps {
  atendimentos?: Atendimento[];
  atendimentoEditavel?: Atendimento;
  onClose: () => void;
  pacienteId?: string;
  pacienteNome?: string;
}

interface LinhaAtendimento {
  id: string;
  condicao: string;
  data: string;
  hora: string;
  isEditing: boolean;
}

export default function AtendimentoForm({
  atendimentos = [],
  atendimentoEditavel,
  onClose,
  pacienteId: pacienteIdProp,
  pacienteNome = "Nome não informado",
}: AtendimentoFormProps) {
  const params = useParams();
  const pacienteId = pacienteIdProp ?? (typeof params?.id === "string" ? params.id : "");
  const queryClient = useQueryClient();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const dateInputRefs = useRef<{ [key: string]: HTMLInputElement | null }>({});
  const timeInputRefs = useRef<{ [key: string]: HTMLInputElement | null }>({});

  const [linhas, setLinhas] = useState<LinhaAtendimento[]>(() => {
    if (atendimentoEditavel) {
      return [
        {
          id: atendimentoEditavel.id,
          condicao:
            atendimentoEditavel.relatorio?.[0]?.descricao ||
            atendimentoEditavel.relatorio?.[0]?.titulo ||
            "",
          data: brParaISO(atendimentoEditavel.data.replace(/\//g, "-")),
          hora: atendimentoEditavel.hora,
          isEditing: true,
        },
      ];
    }
    return [
      {
        id: crypto.randomUUID(),
        condicao: "",
        data: "",
        hora: "",
        isEditing: true,
      },
    ];
  });

  function handleUpdateLinha(id: string, campo: "condicao" | "data" | "hora", valor: string) {
    setLinhas((prev) => prev.map((linha) => (linha.id === id ? { ...linha, [campo]: valor } : linha)));
  }

  function handleEditLinha(id: string) {
    setLinhas((prev) => prev.map((linha) => (linha.id === id ? { ...linha, isEditing: true } : linha)));
    setTimeout(() => { document.getElementById(`condicao-${id}`)?.focus(); }, 50);
  }

  function handleRemoveLinha(id: string) {
    if (linhas.length === 1) {
      setLinhas([{ id: crypto.randomUUID(), condicao: "", data: "", hora: "", isEditing: true }]);
      return;
    }
    setLinhas((prev) => prev.filter((linha) => linha.id !== id));
  }

  function handleAddLinha() {
    setLinhas((prev) => [...prev, { id: crypto.randomUUID(), condicao: "", data: "", hora: "", isEditing: true }]);
  }

  function calcularNumeracao(dataISO: string, offset: number = 0): string {
    if (!dataISO) return "1";
    const [ano, mes] = dataISO.split("-");
    const atendimentosDoMes = atendimentos.filter((a) => {
      const [, mesBR, anoBR] = a.data.split("/");
      return mesBR === mes && anoBR === ano;
    });
    return String(atendimentosDoMes.length + 1 + offset);
  }

  async function handleSave() {
    if (!pacienteId) { toast.error("Paciente inválido."); return; }
    if (linhas.length === 0) return;

    for (let i = 0; i < linhas.length; i++) {
      const linha = linhas[i];
      if (!linha.condicao.trim()) { toast.error(`Preencha a condição na linha ${i + 1}.`); return; }
      if (!linha.data) { toast.error(`Selecione a data na linha ${i + 1}.`); return; }
      if (!linha.hora) { toast.error(`Selecione a hora na linha ${i + 1}.`); return; }
    }

    setIsSubmitting(true);
    try {
      if (atendimentoEditavel) {
        const linha = linhas[0];
        const payload: AtendimentoPayload = {
          pacienteId,
          data: isoParaBR(linha.data),
          hora: linha.hora,
          numeracao: String(atendimentoEditavel.numeracao),
          relatorio: [{ titulo: linha.condicao.trim(), descricao: linha.condicao.trim() }],
        };
        await editarAtendimento(atendimentoEditavel.id, payload);
        toast.success("Atendimento atualizado!");
      } else {
        await Promise.all(
          linhas.map((linha, index) => {
            const payload: AtendimentoPayload = {
              pacienteId,
              data: isoParaBR(linha.data),
              hora: linha.hora,
              numeracao: calcularNumeracao(linha.data, index),
              relatorio: [{ titulo: linha.condicao.trim(), descricao: linha.condicao.trim() }],
            };
            return criarAtendimento(payload);
          })
        );
        toast.success(linhas.length > 1 ? "Atendimentos criados!" : "Atendimento criado!");
      }
      queryClient.invalidateQueries({ queryKey: ["atendimentos"] });
      onClose();
    } catch {
      toast.error("Erro ao salvar.");
    } finally {
      setIsSubmitting(false);
    }
  }

  const gridClasses = "grid grid-cols-[1.4fr_1.4fr_140px_130px_195px] gap-3 items-center w-full";

  return (
    <div className="w-full pt-1">
      <div className="w-full overflow-x-auto">
        <div className="min-w-[850px] px-1">
          

          <div className={`${gridClasses} pb-2 text-left`}>
            <span className="text-[14px] font-bold text-[#0A2540] pl-1">Nome do paciente</span>
            <span className="text-[14px] font-bold text-[#0A2540] pl-1">Condição do paciente</span>
            <span className="text-[14px] font-bold text-[#0A2540] text-center w-full block">Data</span>
            <span className="text-[14px] font-bold text-[#0A2540] text-center w-full block">Hora</span>
            <span className="text-[14px] font-bold text-[#0A2540] text-center w-full block">Ações</span>
          </div>

          <div className="flex flex-col gap-3 max-h-[360px] overflow-y-auto overflow-x-hidden p-1 pr-2">
            {linhas.map((linha) => {
              const hasHora = !!linha.hora;
              const isPM = hasHora && Number(linha.hora.split(":")[0]) >= 12;

              return (
                <div key={linha.id} className={gridClasses}>
                  
                  <Input
                    value={pacienteNome}
                    disabled
                    readOnly
                    title="Nome do paciente"
                    className="h-11 w-full rounded-xl border-[#D0D5DD] bg-white text-[#101828] font-medium text-[14px] shadow-sm disabled:opacity-100 cursor-not-allowed focus-visible:ring-0 truncate px-3"
                  />

                  <Input
                    id={`condicao-${linha.id}`}
                    value={linha.condicao}
                    onChange={(e) => handleUpdateLinha(linha.id, "condicao", e.target.value)}
                    disabled={!linha.isEditing}
                    placeholder="campo vazio"
                    className="h-11 w-full rounded-xl border-[#D0D5DD] bg-white text-[#101828] font-medium placeholder:italic placeholder:text-gray-400 text-[14px] shadow-sm focus-visible:ring-1 focus-visible:ring-[#165BAA] focus-visible:border-[#165BAA] focus-visible:ring-offset-0 px-3"
                  />

                  <div className={`flex items-center justify-center gap-1.5 w-full h-11 rounded-xl border border-[#D0D5DD] bg-white shadow-sm focus-within:ring-1 focus-within:ring-[#165BAA] focus-within:border-[#165BAA] transition-colors ${!linha.isEditing ? "opacity-70 pointer-events-none" : ""}`}>
                    <Input
                      ref={(el) => { dateInputRefs.current[linha.id] = el; }}
                      type="date"
                      value={linha.data}
                      onChange={(e) => handleUpdateLinha(linha.id, "data", e.target.value)}
                      disabled={!linha.isEditing}
                      className="w-[95px] h-full p-0 border-none shadow-none focus-visible:ring-0 bg-transparent text-[#101828] font-medium text-[14px] text-center [&::-webkit-calendar-picker-indicator]:hidden cursor-pointer"
                    />
                    <Calendar 
                      className="w-[16px] h-[16px] text-[#667085] shrink-0 cursor-pointer hover:text-[#165BAA] transition-colors"
                      onClick={() => {
                        if (linha.isEditing && dateInputRefs.current[linha.id]) {
                          try {
                            dateInputRefs.current[linha.id]?.showPicker();
                          } catch {
                            dateInputRefs.current[linha.id]?.click();
                          }
                        }
                      }}
                    />
                  </div>

                  <div className={`flex items-center justify-center gap-1.5 w-full h-11 rounded-xl border border-[#D0D5DD] bg-white shadow-sm focus-within:ring-1 focus-within:ring-[#165BAA] focus-within:border-[#165BAA] transition-colors ${!linha.isEditing ? "opacity-70 pointer-events-none" : ""}`}>
                    <Input
                      ref={(el) => { timeInputRefs.current[linha.id] = el; }}
                      type="time"
                      value={linha.hora}
                      onChange={(e) => handleUpdateLinha(linha.id, "hora", e.target.value)}
                      disabled={!linha.isEditing}
                      className="w-[50px] h-full p-0 border-none shadow-none focus-visible:ring-0 bg-transparent text-[#101828] font-medium text-[14px] text-center [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-time-picker-indicator]:hidden cursor-pointer"
                    />
                    <span className={`text-[13px] font-bold text-[#667085] select-none ${!hasHora ? 'opacity-0' : ''}`}>
                      {hasHora && isPM ? "PM" : "AM"}
                      {!hasHora && "AM"}
                    </span>
                    <Clock 
                      className="w-[16px] h-[16px] text-[#667085] shrink-0 cursor-pointer hover:text-[#165BAA] transition-colors"
                      onClick={() => {
                        if (linha.isEditing && timeInputRefs.current[linha.id]) {
                          try {
                            timeInputRefs.current[linha.id]?.showPicker();
                          } catch {
                            timeInputRefs.current[linha.id]?.click();
                          }
                        }
                      }}
                    />
                  </div>

                  <div className="flex items-center justify-center gap-2 w-full">
                    <Button
                      type="button"
                      variant="outline"
                      onClick={() => handleEditLinha(linha.id)}
                      className="h-11 px-4 w-[90px] rounded-xl border border-[#2563EB] text-[#2563EB] hover:bg-blue-50 font-semibold text-[14px] bg-white shadow-sm flex items-center justify-center gap-1.5 transition-colors cursor-pointer"
                    >
                      <Pencil className="w-[15px] h-[15px] shrink-0" />
                      Editar
                    </Button>

                    <Button
                      type="button"
                      variant="outline"
                      onClick={() => handleRemoveLinha(linha.id)}
                      className="h-11 px-3 w-[100px] rounded-xl border border-[#EF4444] text-[#EF4444] hover:bg-red-50 font-semibold text-[14px] bg-white shadow-sm flex items-center justify-center gap-1.5 transition-colors cursor-pointer"
                    >
                      <Trash2 className="w-[15px] h-[15px] shrink-0" />
                      Remover
                    </Button>
                  </div>
                </div>
              );
            })}
          </div>

          <div className="h-11 mt-3 w-full bg-transparent" />

          <hr className="w-full border-t border-[#E4E7EC] mt-3 mb-6" />

          <div className="grid grid-cols-2 gap-4 w-full">
            <Button
              type="button"
              variant="outline"
              onClick={handleAddLinha}
              className="h-[52px] w-full rounded-full border-2 border-[#165BAA] text-[#165BAA] hover:bg-blue-50 bg-white font-bold text-[15px] shadow-sm flex items-center justify-center gap-2 transition-all active:scale-95 cursor-pointer"
            >
              <Plus className="w-5 h-5" />
              Adicionar atendimento
            </Button>

            <Button
              type="button"
              onClick={handleSave}
              disabled={isSubmitting}
              className="h-[52px] w-full rounded-full bg-[#165BAA] hover:bg-[#134e8f] text-white font-bold text-[15px] shadow-md flex items-center justify-center gap-2 transition-all active:scale-95 cursor-pointer"
            >
              <Check className="w-5 h-5 text-white" />
              {isSubmitting ? "Salvando..." : "Salvar atendimento"}
            </Button>
          </div>

        </div>
      </div>
    </div>
  );
}