import {
  AnexoEnvioFormData,
  RelatorioEnvioFormData,
} from "@/features/anexo/types";

export function construirArquivoFormData(
  data: AnexoEnvioFormData | RelatorioEnvioFormData,
): FormData {
  const formData = new FormData();

  if (!data.arquivo?.[0]) {
    throw new Error("Arquivo obrigatório");
  }

  const file = data.arquivo[0];

  formData.append("file", file);

  const metadata = {
    data: data.data,
    tipoArquivo: data.tipoArquivo,
    pacienteId: data.pacienteId,
    titulo: data.titulo,
    descricao: data.descricao,
  };

  formData.append("metadata", JSON.stringify(metadata));

  return formData;
}
