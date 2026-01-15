export function formatarData(data: string): string {
  const [ano, mes, dia] = data.split("-");
  return `${dia}-${mes}-${ano}`;
}
