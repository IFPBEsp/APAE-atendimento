export function filtroData(dataInput: string, dataBackend: string): boolean {
  if (!dataInput || !dataBackend) return false;

  const [anoInput, mesInput, diaInput] = dataInput.split("-").map(Number);

  const cleanBack = dataBackend.split("T")[0];
  const parts = cleanBack.split(/[-/]/).map(Number);
  if (parts.length < 3) return false;

  let diaBack: number;
  let mesBack: number;
  let anoBack: number;

  if (parts[0] > 1000) {
    // YYYY-MM-DD
    [anoBack, mesBack, diaBack] = parts;
  } else {
    // DD-MM-YYYY
    [diaBack, mesBack, anoBack] = parts;
  }

  return anoInput === anoBack && mesInput === mesBack && diaInput === diaBack;
}
