import dados from "../../data/verificacao.json";

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

export async function buscarPacienteParaPdf(
  pacienteId: string
): Promise<PacientePdfDTO> {
  const res = await fetch(`${dados.urlBase}/pacientes/${pacienteId}`);

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
  const res = await fetch(`${dados.urlBase}/profissionais/${profissionalId}`);

  if (!res.ok) {
    throw new Error("Erro ao buscar profissional");
  }

  const profissional = await res.json();

  return {
    nome: profissional.nomeCompleto,
    crm: profissional.crm,
  };
}