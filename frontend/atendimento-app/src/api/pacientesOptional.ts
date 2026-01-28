import dados from "../../data/verificacao.json";
import { getAuth } from "firebase/auth";

export type PacienteOption = {
  id: string;
  nome: string;
};

// 🔐 Helper para obter o token Firebase
async function getFirebaseToken(): Promise<string> {
  const auth = getAuth();
  const user = auth.currentUser;

  if (!user) {
    throw new Error("Usuário não autenticado");
  }

  return await user.getIdToken();
}

export async function getPacientesPorProfissional(): Promise<PacienteOption[]> {
  const token = await getFirebaseToken();

  const profissional = JSON.parse(
  localStorage.getItem("@apae:profissional") || "null"
  );

  const response = await fetch(
    `${dados.urlBase}/profissionais/${profissional.id}/pacientes-option`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    },
  );

  if (!response.ok) {
    throw new Error("Erro ao buscar pacientes do profissional");
  }

  return response.json();
}
