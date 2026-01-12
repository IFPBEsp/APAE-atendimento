export function formatarData(data: string): string {
  const [dia, mes, ano] = data.split("-");
  return `${dia}/${mes}/${ano}`;
}
