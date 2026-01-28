import dados from "../../data/verificacao.json";
import { getAuth } from "firebase/auth";

export type CriarAgendamentoPayload = {
  profissionalId: string;
  pacienteId: string;
  data: string;
  hora: string;
  numeroAtendimento: number;
};

// 🔐 Helper centralizado
async function getFirebaseToken(): Promise<string> {
  const auth = getAuth();
  const user = auth.currentUser;

  if (!user) {
    throw new Error("Usuário não autenticado");
  }

  return await user.getIdToken();
}

export async function listarAgendamentos(profissionalId: string) {
  const token = await getFirebaseToken();

  const res = await fetch(
    `${dados.urlBase}/agendamento/${profissionalId}`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
      },
    }
  );

  if (!res.ok) {
    const text = await res.text().catch(() => null);
    throw new Error(text || "Erro ao buscar agendamentos");
  }

  return res.json();
}

export async function criarAgendamento(payload: CriarAgendamentoPayload) {
  const token = await getFirebaseToken();

  const res = await fetch(
    `${dados.urlBase}/agendamento`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
        Accept: "application/json",
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
  const token = await getFirebaseToken();

  const res = await fetch(
    `${dados.urlBase}/agendamento/${profissionalId}/${pacienteId}/${agendamentoId}`,
    {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
      },
    }
  );

  if (!res.ok) {
    const text = await res.text().catch(() => null);
    throw new Error(text || "Erro ao deletar agendamento");
  }

  return res.text();
}
