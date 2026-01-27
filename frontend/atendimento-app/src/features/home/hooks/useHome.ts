import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { getPacientes, FiltroPaciente } from "../services/homeService";
import { usePrimeiroNomeProfissional } from "@/features/profissional/hooks/usePrimeiroNomeProfissional";

export function useHome() {
  const { data: medicoNome, isLoading: loadingNome } = usePrimeiroNomeProfissional();

  const [busca, setBusca] = useState("");
  const [filtro, setFiltro] = useState<"nome" | "cpf" | "cidade" | "">("");

  const filtros: FiltroPaciente | undefined =
    busca && filtro ? { [filtro]: busca } : undefined;

  const {
    data: pacientes = [],
    isLoading: loadingPacientes,
    isError,
  } = useQuery({
    queryKey: ["pacientes", filtros],
    queryFn: () => getPacientes(filtros),
  });

  return {
    medicoNome: medicoNome ?? "Profissional",
    pacientes,
    loading: loadingNome || loadingPacientes,
    erro: isError,
    busca,
    setBusca,
    filtro,
    setFiltro,
  };
}
