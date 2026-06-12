import { api } from "@/services/axios";

export type LoginPayload = {
  email: string;
  password: string;
};

export type LoginResponse = {
  success: boolean;
  message: string;
  primeiroAcesso?: boolean | null;
  redirectTo?: string | null;
};

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>("/auth/login", payload);
  return data;
}

export async function redefinirSenha(novaSenha: string): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>("/auth/redefinir-senha", {
    novaSenha,
  });
  return data;
}

export async function logout(): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>("/auth/logout");
  return data;
}

export async function me(): Promise<LoginResponse> {
  const { data } = await api.get<LoginResponse>("/auth/me");
  return data;
}
