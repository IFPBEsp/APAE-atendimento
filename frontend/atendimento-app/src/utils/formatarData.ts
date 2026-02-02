export function formatarData(data: string): string {
  const [dia, mes, ano] = data.split("-");
  return `${dia}/${mes}/${ano}`;
}

export function isoParaBR(iso: string): string {
  if (!iso) return "";
  const [ano, mes, dia] = iso.split("-");
  return `${dia}-${mes}-${ano}`;
}

export function brParaISO(dataBR: string): string {
  if (!dataBR) return "";
  const [dia, mes, ano] = dataBR.split("-");
  return `${ano}-${mes}-${dia}`;
}
