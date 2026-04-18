import axios from "axios";
import { toast } from "sonner";

type ErrorResponse = {
  message?: string;
};

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
  withCredentials: true,
  headers: {
    Accept: "application/json",
  },
});

api.interceptors.request.use((config) => {
  if (config.data instanceof FormData) {
    delete config.headers["Content-Type"];
  } else {
    config.headers["Content-Type"] = "application/json";
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status as number | undefined;
    const url = error?.config?.url as string | undefined;
    const backendMessage = (error?.response?.data as ErrorResponse | undefined)
      ?.message;

    const isAuthEndpoint = url?.startsWith("/auth/");
    const isAuthLogin = url?.includes("/auth/login");
    const isAuthMe = url?.includes("/auth/me");

    if (status === 401) {
      if (
        typeof window !== "undefined" &&
        !isAuthLogin &&
        !isAuthMe &&
        window.location.pathname !== "/login"
      ) {
        window.location.href = "/login";
      }

      return Promise.reject(error);
    }

    if (isAuthEndpoint) {
      return Promise.reject(error);
    }

    if (backendMessage) {
      toast.error(backendMessage);
    } else if (error?.request) {
      toast.error(
        "Não foi possível conectar ao servidor. Tente novamente mais tarde.",
      );
    } else {
      toast.error("Erro inesperado na aplicação.");
    }

    return Promise.reject(error);
  },
);
