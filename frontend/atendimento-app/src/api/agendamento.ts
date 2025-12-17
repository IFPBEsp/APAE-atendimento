import dados from "../../data/verificacao.json";

export type CriarAgendamentoPayload = {
  profissionalId: string;
  pacienteId: string;
  data: string;
  hora: string;
  numeroAtendimento: number;
};

export async function listarAgendamentos(profissionalId: string) {
  const res = await fetch(
    `${dados.urlBase}/agendamento/${profissionalId}`
  );

  if (!res.ok) {
    const text = await res.text().catch(() => null);
    throw new Error(text || "Erro ao buscar agendamentos");
  }

  return res.json();
}

export async function criarAgendamento(payload: CriarAgendamentoPayload) {
  const res = await fetch(
    `${dados.urlBase}/agendamento`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        profissionalId: payload.profissionalId,
        pacienteId: payload.pacienteId,
        data: payload.data,
        hora: payload.hora,
        numeroAtendimento: payload.numeroAtendimento,
      }),
    }
  );

  if (!res.ok) {
    const text = await res.text().catch(() => null);
    throw new Error(text || "Erro ao criar agendamento");
  }

  return res.json();
}

export async function deletarAgendamento(
  profissionalId: string,
  pacienteId: string,
  agendamentoId: string
) {
  const res = await fetch(
    `${dados.urlBase}/agendamento/${profissionalId}/${pacienteId}/${agendamentoId}`,
    {
      method: "DELETE",
    }
  );

  if (!res.ok) {
    const text = await res.text().catch(() => null);
    throw new Error(text || "Erro ao deletar agendamento");
  }

  return res.text();
}
