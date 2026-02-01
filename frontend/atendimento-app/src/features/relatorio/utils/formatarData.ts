export function formatarDataBr(dataIso: string): string {
  return new Date(dataIso).toLocaleDateString("pt-BR");
}
