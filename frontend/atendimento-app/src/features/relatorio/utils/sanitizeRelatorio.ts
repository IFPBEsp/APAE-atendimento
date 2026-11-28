export const tituloRegex = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()']*$/u;

export const descricaoRegex =
  /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()'%&#]*$/u;

export const normalizar = (s = "") => s.trim().replace(/\s+/g, " ");

export const sanitizeFilename = (n = "") => n.replace(/[^a-zA-Z0-9._-]/g, "_");

export function obterDataHojeISO(): string {
  const agora = new Date();
  const offset = agora.getTimezoneOffset() * 60000;
  return new Date(agora.getTime() - offset).toISOString().split("T")[0];
}

export function validarDataISO(dataISO: string) {
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  const min = new Date(hoje);
  min.setFullYear(hoje.getFullYear() - 30);
  const d = new Date(dataISO + "T00:00:00");
  if ( isNaN(d.getTime()) ) {
    throw new Error("Data inválida.");
  }
  if (d > hoje) {
    throw new Error("A data não pode estar no futuro");
  }
  if (d < min) {
    throw new Error("Data fora do intervalo de 30 anos até hoje.");
  }
  return dataISO;
}

export function validarTexto(titulo: string, descricao: string) {
  const t = normalizar(titulo);
  const d = normalizar(descricao);
  if (!tituloRegex.test(t)) throw new Error("Título inválido.");
  if (!descricaoRegex.test(d)) throw new Error("Descrição inválida.");
  return { titulo: t, descricao: d };
}

export function validarArquivo(file?: File) {
  if (!file) throw new Error("Nenhum arquivo selecionado.");
  if (!(file.type === "application/pdf" || file.type.startsWith("image/"))) {
    throw new Error("Apenas PDF ou imagem são permitidos.");
  }
  const nomeSanitizado = sanitizeFilename(file.name);
  return new File([file], nomeSanitizado, { type: file.type });
}
