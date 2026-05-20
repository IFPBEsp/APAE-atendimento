import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import { getPacientes, FiltroPaciente } from "../services/homeService";
import { usePrimeiroNomeProfissional } from "@/features/profissional/hooks/usePrimeiroNomeProfissional";
import { useDebounce } from "@/utils/useDebounce";

export function useHome() {
  const { data: medicoNome, isLoading: loadingNome } = usePrimeiroNomeProfissional();

  const [busca, setBusca] = useState("");
  const [filtro, setFiltro] = useState<"nome" | "cpf" | "cidade">("nome");
  const [page, setPage] = useState(1);
  const limit = 1;

  const buscaDebounced = useDebounce(busca, 500);

  useEffect(() => {
    setPage(1);
  }, [buscaDebounced, filtro]);

  const filtros: FiltroPaciente = { page, limit };
  if (buscaDebounced && filtro) {
    filtros[filtro] = buscaDebounced;
  }

  const termoBusca = buscaDebounced.trim();

  if (termoBusca) {
    filtros[filtro] = termoBusca;
  }

  const {
    data: paginatedData,
    isLoading: loadingPacientes,
    isFetching,
    isError,
  } = useQuery({
    queryKey: ["pacientes", filtros],
    queryFn: () => getPacientes(filtros),
    placeholderData: keepPreviousData,
  });

  return {
    medicoNome: medicoNome ?? "Profissional",
    pacientes: paginatedData?.data || [],
    pagination: paginatedData?.pagination,
    loading: loadingNome || loadingPacientes,
    isFetching,
    erro: isError,
    busca,
    setBusca,
    filtro,
    setFiltro,
    page,
    setPage,
  };
}