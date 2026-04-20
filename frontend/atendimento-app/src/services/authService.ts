import { api } from "@/services/axios";

export type LoginPayload = {
  email: string;
  password: string;
};

export type LoginResponse = {
  success: boolean;
  message: string;
};

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>("/auth/login", payload);
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
