import {
  AnexoEnvioFormData,
  RelatorioEnvioFormData,
} from "../features/anexo/components/anexoForm";
import { sanitizeFilename } from "@/features/relatorio/utils/sanitizeRelatorio";

export function construirArquivoFormData(
  data: AnexoEnvioFormData | RelatorioEnvioFormData,
): FormData {
  const formData: FormData = new FormData();

  if (data?.arquivo?.[0] && data?.arquivo?.length > 0) {
    const arquivo = data.arquivo[0];
    formData.append("file", arquivo, sanitizeFilename(arquivo.name));
  }
  const metadata = {
    data: data.data,
    tipoArquivo: data.tipoArquivo,
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
