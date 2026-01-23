import {buscarPacienteParaPdf, buscarProfissionalParaPdf} from "@/api/dadosRelatorioPdf";
import {useQuery} from "@tanstack/react-query";
import dados from "@/../data/verificacao.json";
import { RelatorioPDFData } from "../types";


export function useRelatorioPDF(pacienteId: string) {
  const { data, isLoading, isError } = useQuery<RelatorioPDFData>({
    queryKey: ["relatoriosPDF", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const profissionalId = dados.idProfissional;

      const [paciente, profissional] = await Promise.all([
        buscarPacienteParaPdf(pacienteId),
        buscarProfissionalParaPdf(profissionalId),
      ]);

      // Garantia forte
      if (!paciente || !profissional) {
        throw new Error("Dados do PDF incompletos");
      }

      return { paciente, profissional };
    },
  });

  return {
    isLoadingPDF: isLoading,
    isErrorPDF: isError,
    dadosPDF: data ?? null,
  };
}






 

 