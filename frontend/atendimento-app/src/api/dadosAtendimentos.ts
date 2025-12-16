import dados from "../../data/verificacao.json";
import {
  Atendimento,
  AtendimentoPayload,
  GrupoAtendimentosApi,
} from "@/types/Atendimento";

export async function getAtendimentos(
  pacienteId: string
): Promise<GrupoAtendimentosApi[]> {
  const res = await fetch(
    `${dados.urlBase}/atendimentos/${dados.idProfissional}/${pacienteId}`
  );

  if (!res.ok) {
    throw new Error("Erro ao buscar atendimentos");
  }

  return res.json();
}

export async function criarAtendimento(
  payload: AtendimentoPayload
): Promise<Atendimento> {
  const url = `${dados.urlBase}/atendimentos`;

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    throw new Error("Erro ao criar atendimento");
  }

  return res.json();
}

export async function editarAtendimento(
  atendimentoId: string,
  payload: AtendimentoPayload
): Promise<Atendimento> {
  const url = `${dados.urlBase}/atendimentos/${atendimentoId}`;

  const res = await fetch(url, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    throw new Error("Erro ao editar atendimento");
  }

  return res.json();
}

export async function deletarAtendimento(
  profissionalId: string,
  pacienteId: string,
  atendimentoId: string
): Promise<void> {
  const url = `${dados.urlBase}/atendimentos/${profissionalId}/${pacienteId}/${atendimentoId}`;

  const res = await fetch(url, { method: "DELETE" });

  if (!res.ok) {
    throw new Error("Erro ao deletar atendimento");
  }
}
