export const tituloRegex = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()']*$/u;

export const descricaoRegex =
  /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()'%&#]*$/u;

export const normalizar = (s = "") => s.trim().replace(/\s+/g, " ");

export const sanitizeFilename = (n = "") => n.replace(/[^a-zA-Z0-9._-]/g, "_");

export function validarDataISO(dataISO: string) {
  const hoje = new Date();
  const min = new Date();
  min.setFullYear(hoje.getFullYear() - 30);
  const d = new Date(dataISO + "T00:00:00");
  if (d < min || d > hoje)
    throw new Error("Data fora do intervalo de 30 anos até hoje.");
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
