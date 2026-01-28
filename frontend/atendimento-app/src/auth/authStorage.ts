const STORAGE_KEY = "@apae:profissional";

export type ProfissionalStorage = {
  id: string;
};

export function saveProfissional(data: ProfissionalStorage) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
}

export function getProfissionalStorage() {
  if (typeof window === "undefined") return null;

  const raw = localStorage.getItem("@apae:profissional");
  return raw ? JSON.parse(raw) : null;
}

export function clearProfissional() {
  localStorage.removeItem(STORAGE_KEY);
}
