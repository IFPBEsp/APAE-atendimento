import { useQuery } from "@tanstack/react-query";
import { getPrimeiroNomeProfissional } from "../services/profissionalService";

export function usePrimeiroNomeProfissional() {
  return useQuery({
    queryKey: ["profissional", "primeiro-nome"],
    queryFn: getPrimeiroNomeProfissional,
  });
}