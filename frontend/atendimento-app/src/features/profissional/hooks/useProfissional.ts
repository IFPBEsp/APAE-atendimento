import { useQuery } from "@tanstack/react-query";
import { getProfissional } from "../services/profissionalService";

export function useProfissional() {
  return useQuery({
    queryKey: ["profissional"],
    queryFn: getProfissional,
  });
}
