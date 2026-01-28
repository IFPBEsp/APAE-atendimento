import { api } from "@/services/axios";
import { Profissional } from "../types";

export async function getProfissional(): Promise<Profissional> {
  const profissional = JSON.parse(
  localStorage.getItem("@apae:profissional") || "null"
  );

  const { data } = await api.get<Profissional>(
    `/profissionais/${profissional.id}`
  );

  return data;
}

export async function getPrimeiroNomeProfissional(): Promise<string> {
  const raw = localStorage.getItem("@apae:profissional");

  if (!raw) {
    throw new Error("Profissional não encontrado no storage");
  }

  const profissional = JSON.parse(raw);

  if (!profissional?.id) {
    throw new Error("ID do profissional inválido");
  }

  console.log(profissional)

  const { data } = await api.get<string>(
    `/profissionais/${profissional.id}/primeiro-nome`
  );

  return data;
}