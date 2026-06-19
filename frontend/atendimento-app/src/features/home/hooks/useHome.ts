import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import { getPacientes, FiltroPaciente, OrigemPacientes } from "../services/homeService";
import { useProfissional } from "@/features/profissional/hooks/useProfissional";
import { useDebounce } from "@/utils/useDebounce";

export function useHome() {
  const { data: profissional, isLoading: loadingNome } = useProfissional();

  const [busca, setBusca] = useState("");
  const [filtro, setFiltro] = useState<"nome" | "cpf" | "cidade">("nome");
  const [origemPacientes, setOrigemPacientes] = useState<OrigemPacientes>("meus");
  const [page, setPage] = useState(1);
  const limit = 10;

  const buscaDebounced = useDebounce(busca, 500);

  useEffect(() => {
    setPage(1);
  }, [buscaDebounced, filtro, origemPacientes]);

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
    queryKey: ["pacientes", origemPacientes, filtros],
    queryFn: () => getPacientes(filtros, origemPacientes),
    placeholderData: keepPreviousData,
  });

  return {
    medicoNome: profissional?.nomeCompleto?.trim().split(/\s+/)[0] ?? "Profissional",
    pacientes: paginatedData?.data || [],
    pagination: paginatedData?.pagination,
    loading: loadingNome || loadingPacientes,
    isFetching,
    erro: isError,
    busca,
    setBusca,
    filtro,
    setFiltro,
    origemPacientes,
    setOrigemPacientes,
    page,
    setPage,
  };
}
