import { isoParaBR } from "@/utils/formatarData";
import {
  AnexoEnvioFormData,
  RelatorioEnvioFormData,
} from "../features/anexo/components/anexoForm";

export function construirArquivoFormData(
  data: AnexoEnvioFormData | RelatorioEnvioFormData,
): FormData {
  const formData: FormData = new FormData();

  if (data?.arquivo?.[0] && data?.arquivo?.length > 0) {
    const arquivo = data.arquivo[0];
    formData.append("file", arquivo, arquivo.name);
  }
  const metadata = {
    data: isoParaBR(data.data),
    tipoArquivo: data.tipoArquivo,
    profissionalId: data.profissionalId,
    pacienteId: data.pacienteId,
    titulo: data.titulo,
    descricao: data.descricao,
  };
  formData.append(
    "metadata",
    new Blob([JSON.stringify(metadata)], { type: "application/json" }),
  );

  return formData;
}
