"use client";

import { useState } from "react";
import { useParams, useSearchParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Check, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { isoParaBR, brParaISO } from "@/utils/formatarData";
import { criarAtendimento, editarAtendimento } from "../services/atendimentoService";
import { calcularNumeracao } from "../utils/calcularNumeracao";
import { Atendimento, AtendimentoPayload } from "../types";

const LIMITE_CONDICAO = 100;

interface AtendimentoFormProps {
  atendimentos?: Atendimento[];
  atendimentoEditavel?: Atendimento;
  onClose: () => void;
  pacienteId?: string;
  pacienteNome?: string;
  agendamentoId?: string;
  dataInicial?: string;
  horaInicial?: string;
}

interface LinhaAtendimento {
  id: string;
  condicao: string;
  data: string;
  hora: string;
}

interface LinhaAtendimentoRowProps {
  linha: LinhaAtendimento;
  indice: number;
  podeRemover: boolean;
  onUpdate: (id: string, campo: "condicao" | "data" | "hora", valor: string) => void;
  onRemove: (id: string) => void;
}

function LinhaAtendimentoRow({
  linha,
  indice,
  podeRemover,
  onUpdate,
  onRemove,
}: Readonly<LinhaAtendimentoRowProps>) {
  const hasHora = Boolean(linha.hora);
  const isPM = hasHora && Number(linha.hora.split(":")[0]) >= 12;

  return (
    <div className="w-full rounded-2xl border-2 border-[#98A2B3] bg-white p-4 shadow-sm flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <span className="text-[13px] font-bold uppercase tracking-wide text-[#165BAA]">
          Atendimento #{indice + 1}
        </span>

        {podeRemover && (
          <Button
            type="button"
            variant="ghost"
            onClick={() => onRemove(linha.id)}
            className="h-8 px-2 text-[13px] font-semibold text-[#EF4444] hover:bg-red-50 hover:text-[#EF4444] flex items-center gap-1 cursor-pointer"
          >
            <Trash2 className="w-[14px] h-[14px] shrink-0" />
            Remover
          </Button>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <label htmlFor={`condicao-${linha.id}`} className="text-[14px] font-bold text-[#0A2540]">
          Condição do paciente
        </label>
        <div className="relative w-full flex items-center">
          <Input
            id={`condicao-${linha.id}`}
            value={linha.condicao}
            onChange={(e) => onUpdate(linha.id, "condicao", e.target.value)}
            maxLength={LIMITE_CONDICAO}
            placeholder="campo vazio"
            className="h-11 w-full rounded-xl border-[#D0D5DD] bg-white text-[#101828] font-medium placeholder:italic placeholder:text-gray-400 text-[14px] shadow-sm focus-visible:ring-1 focus-visible:ring-[#165BAA] focus-visible:border-[#165BAA] focus-visible:ring-offset-0 pl-3 pr-14"
          />

          <span className="absolute right-3 text-[11px] font-medium text-[#667085] pointer-events-none select-none">
            {linha.condicao.length}/{LIMITE_CONDICAO}
          </span>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col gap-1.5">
          <label htmlFor={`data-${linha.id}`} className="text-[14px] font-bold text-[#0A2540]">
            Data
          </label>
          <div className="flex items-center w-full h-11 rounded-xl border border-[#D0D5DD] bg-white shadow-sm focus-within:ring-1 focus-within:ring-[#165BAA] focus-within:border-[#165BAA] transition-colors">
            <Input
              id={`data-${linha.id}`}
              type="date"
              value={linha.data}
              onChange={(e) => onUpdate(linha.id, "data", e.target.value)}
              className="w-full h-full px-3 border-none shadow-none focus-visible:ring-0 bg-transparent text-[#101828] font-medium text-[14px] cursor-pointer"
            />
          </div>
        </div>

        <div className="flex flex-col gap-1.5">
          <label htmlFor={`hora-${linha.id}`} className="text-[14px] font-bold text-[#0A2540]">
            Hora
          </label>
          <div className="flex items-center w-full h-11 rounded-xl border border-[#D0D5DD] bg-white shadow-sm focus-within:ring-1 focus-within:ring-[#165BAA] focus-within:border-[#165BAA] transition-colors">
            <Input
              id={`hora-${linha.id}`}
              type="time"
              value={linha.hora}
              onChange={(e) => onUpdate(linha.id, "hora", e.target.value)}
              className="flex-1 min-w-0 h-full px-3 border-none shadow-none focus-visible:ring-0 bg-transparent text-[#101828] font-medium text-[14px] cursor-pointer"
            />
            <span className={`pr-3 text-[13px] font-bold text-[#667085] select-none ${hasHora ? "" : "opacity-0"}`}>
              {isPM ? "PM" : "AM"}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

function normalizarDataParaISO(valor?: string): string {
  if (!valor) return "";
  const limpo = valor.replace(/\//g, "-");
  if (/^\d{4}-\d{2}-\d{2}$/.test(limpo)) return limpo;
  if (/^\d{2}-\d{2}-\d{4}$/.test(limpo)) return brParaISO(limpo);
  return limpo;
}

function resolverDadosIniciais(
  props: Readonly<AtendimentoFormProps>,
  params: ReturnType<typeof useParams>,
  searchParams: ReturnType<typeof useSearchParams>
) {
  const agendamentoId = props.agendamentoId ?? searchParams?.get("agendamentoId") ?? undefined;
  const dataAgendamentoISO = normalizarDataParaISO(
    props.dataInicial ?? searchParams?.get("data") ?? undefined
  );
  const horaBruta = props.horaInicial ?? searchParams?.get("hora") ?? searchParams?.get("horario") ?? "";
  const horaAgendamento = horaBruta.slice(0, 5);
  const pacienteNome = props.pacienteNome || searchParams?.get("paciente") || "Nome não informado";
  const paramId = typeof params?.id === "string" ? params.id : "";
  const pacienteId = props.pacienteId ?? paramId;

  return { agendamentoId, dataAgendamentoISO, horaAgendamento, pacienteNome, pacienteId };
}

function criarLinhasIniciais(
  atendimentoEditavel: Atendimento | undefined,
  dataAgendamentoISO: string,
  horaAgendamento: string
): LinhaAtendimento[] {
  if (atendimentoEditavel) {
    const relatorioPrincipal = atendimentoEditavel.relatorio?.[0];
    const textoCondicao = relatorioPrincipal?.descricao || relatorioPrincipal?.titulo || "";
    return [
      {
        id: atendimentoEditavel.id,
        condicao: textoCondicao.slice(0, LIMITE_CONDICAO),
        data: brParaISO(atendimentoEditavel.data.replace(/\//g, "-")),
        hora: atendimentoEditavel.hora,
      },
    ];
  }
  return [
    {
      id: crypto.randomUUID(),
      condicao: "",
      data: dataAgendamentoISO,
      hora: horaAgendamento,
    },
  ];
}

export default function AtendimentoForm(props: Readonly<AtendimentoFormProps>) {
  const { atendimentos = [], atendimentoEditavel, onClose } = props;
  const params = useParams();
  const searchParams = useSearchParams();
  const {
    agendamentoId,
    dataAgendamentoISO,
    horaAgendamento,
    pacienteNome,
    pacienteId,
  } = resolverDadosIniciais(props, params, searchParams);

  const queryClient = useQueryClient();

  const [linhas, setLinhas] = useState<LinhaAtendimento[]>(() =>
    criarLinhasIniciais(atendimentoEditavel, dataAgendamentoISO, horaAgendamento)
  );

  function finalizarSalvamento(mensagem: string) {
    toast.success(mensagem);
    queryClient.invalidateQueries({ queryKey: ["atendimentos"] });
    queryClient.invalidateQueries({ queryKey: ["agendamentos"] });
    onClose();
  }

  const criarMutation = useMutation({
    mutationFn: (payloads: AtendimentoPayload[]) =>
      Promise.all(payloads.map((payload) => criarAtendimento(payload))),
    onSuccess: (_data, payloads) =>
      finalizarSalvamento(payloads.length > 1 ? "Atendimentos criados!" : "Atendimento criado!"),
    onError: () => toast.error("Erro ao salvar."),
  });

  const editarMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: AtendimentoPayload }) =>
      editarAtendimento(id, payload),
    onSuccess: () => finalizarSalvamento("Atendimento atualizado!"),
    onError: () => toast.error("Erro ao salvar."),
  });

  const isSubmitting = criarMutation.isPending || editarMutation.isPending;
  const modoEdicao = Boolean(atendimentoEditavel);
  const rotuloSalvar = modoEdicao ? "Salvar alterações" : "Salvar atendimento";

  function handleUpdateLinha(id: string, campo: "condicao" | "data" | "hora", valor: string) {
    const valorTratado = campo === "condicao" ? valor.slice(0, LIMITE_CONDICAO) : valor;
    setLinhas((prev) => prev.map((linha) => (linha.id === id ? { ...linha, [campo]: valorTratado } : linha)));
  }

  function handleRemoveLinha(id: string) {
    setLinhas((prev) => prev.filter((linha) => linha.id !== id));
  }

  function handleAddLinha() {
    setLinhas((prev) => [...prev, { id: crypto.randomUUID(), condicao: "", data: "", hora: "" }]);
  }

  function validarLinhas(linhasParaValidar: LinhaAtendimento[]): boolean {
    for (let i = 0; i < linhasParaValidar.length; i++) {
      const linha = linhasParaValidar[i];
      if (!linha.condicao.trim()) {
        toast.error(`Preencha a condição na linha ${i + 1}.`);
        return false;
      }
      if (linha.condicao.length > LIMITE_CONDICAO) {
        toast.error(`A condição na linha ${i + 1} deve ter no máximo ${LIMITE_CONDICAO} caracteres.`);
        return false;
      }
      if (!linha.data) {
        toast.error(`Selecione a data na linha ${i + 1}.`);
        return false;
      }
      if (!linha.hora) {
        toast.error(`Selecione a hora na linha ${i + 1}.`);
        return false;
      }
    }
    return true;
  }

  function handleSave() {
    if (!pacienteId) {
      toast.error("Paciente inválido.");
      return;
    }
    if (linhas.length === 0 || !validarLinhas(linhas)) return;

    if (atendimentoEditavel) {
      const linha = linhas[0];
      const payload: AtendimentoPayload = {
        pacienteId,
        data: isoParaBR(linha.data),
        hora: linha.hora,
        numeracao: String(atendimentoEditavel.numeracao),
        relatorio: [{ titulo: linha.condicao.trim(), descricao: linha.condicao.trim() }],
      };
      editarMutation.mutate({ id: atendimentoEditavel.id, payload });
      return;
    }

    const payloads: AtendimentoPayload[] = linhas.map((linha, index) => ({
      pacienteId,
      ...(agendamentoId && index === 0 ? { agendamentoId } : {}),
      data: isoParaBR(linha.data),
      hora: linha.hora,
      numeracao: calcularNumeracao(atendimentos, linha.data, index),
      relatorio: [{ titulo: linha.condicao.trim(), descricao: linha.condicao.trim() }],
    }));
    criarMutation.mutate(payloads);
  }

  return (
    <div className="w-full pt-1 flex flex-col gap-4">
      <div className="flex flex-col gap-1.5">
        <label htmlFor="paciente-nome" className="text-[14px] font-bold text-[#0A2540]">
          Nome do paciente
        </label>
        <Input
          id="paciente-nome"
          value={pacienteNome}
          disabled
          readOnly
          title="Nome do paciente"
          className="h-11 w-full rounded-xl border-[#D0D5DD] bg-white text-[#101828] font-medium text-[14px] shadow-sm disabled:opacity-100 cursor-not-allowed focus-visible:ring-0 truncate px-3"
        />
      </div>

      <div className="flex flex-col gap-4 max-h-[50vh] overflow-y-auto p-1">
        {linhas.map((linha, indice) => (
          <LinhaAtendimentoRow
            key={linha.id}
            linha={linha}
            indice={indice}
            podeRemover={!modoEdicao && linhas.length > 1}
            onUpdate={handleUpdateLinha}
            onRemove={handleRemoveLinha}
          />
        ))}
      </div>

      <hr className="w-full border-t border-[#E4E7EC]" />

      <div className={`grid grid-cols-1 gap-4 w-full ${modoEdicao ? "" : "sm:grid-cols-2"}`}>
        {!modoEdicao && (
          <Button
            type="button"
            variant="outline"
            onClick={handleAddLinha}
            className="h-[52px] w-full rounded-full border-2 border-[#165BAA] text-[#165BAA] hover:bg-blue-50 bg-white font-bold text-[15px] shadow-sm flex items-center justify-center gap-2 transition-all active:scale-95 cursor-pointer"
          >
            <Plus className="w-5 h-5" />
            Adicionar atendimento
          </Button>
        )}

        <Button
          type="button"
          onClick={handleSave}
          disabled={isSubmitting}
          className="h-[52px] w-full rounded-full bg-[#165BAA] hover:bg-[#134e8f] text-white font-bold text-[15px] shadow-md flex items-center justify-center gap-2 transition-all active:scale-95 cursor-pointer"
        >
          <Check className="w-5 h-5 text-white" />
          {isSubmitting ? "Salvando..." : rotuloSalvar}
        </Button>
      </div>
    </div>
  );
}