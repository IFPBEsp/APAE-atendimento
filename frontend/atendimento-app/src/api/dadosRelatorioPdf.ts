import dados from "../../data/verificacao.json";
import { getAuth } from "firebase/auth";

export type PacientePdfDTO = {
  nome: string;
  dataNascimento: string;
  endereco: string;
  responsaveis: string[];
};

export type ProfissionalPdfDTO = {
  nome: string;
  crm: string;
};

function formatarDataBr(dataIso: string): string {
  return new Date(dataIso).toLocaleDateString("pt-BR");
}


async function getFirebaseToken(): Promise<string> {
  const auth = getAuth();
  const user = auth.currentUser;

  if (!user) {
    throw new Error("Usuário não autenticado");
  }

  return await user.getIdToken();
}

export async function buscarPacienteParaPdf(
  pacienteId: string
): Promise<PacientePdfDTO> {
  const token = await getFirebaseToken();

  const res = await fetch(
    `${dados.urlBase}/pacientes/${pacienteId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
      },
    }
  );

  if (!res.ok) {
    throw new Error("Erro ao buscar paciente");
  }

  const paciente = await res.json();

  return {
    nome: paciente.nomeCompleto,
    dataNascimento: formatarDataBr(paciente.dataDeNascimento),
    endereco: paciente.endereco,
    responsaveis: paciente.responsaveis,
  };
}

export async function buscarProfissionalParaPdf(
  profissionalId: string
): Promise<ProfissionalPdfDTO> {
  const token = await getFirebaseToken();

  const res = await fetch(
    `${dados.urlBase}/profissionais/${profissionalId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
      },
    }
  );

  if (!res.ok) {
    throw new Error("Erro ao buscar profissional");
  }

  const profissional = await res.json();

  return {
    nome: profissional.nomeCompleto,
    crm: profissional.crm,
  };
}