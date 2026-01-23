export function filtroData(dataInput: string, dataBackend: string): boolean {
  if (!dataInput || !dataBackend) return false;

  const [anoInput, mesInput, diaInput] = dataInput.split("-").map(Number);

  const [diaBack, mesBack, anoBack] = dataBackend.split("-").map(Number);

  return anoInput === anoBack && mesInput === mesBack && diaInput === diaBack;
}
