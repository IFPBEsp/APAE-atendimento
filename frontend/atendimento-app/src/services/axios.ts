import axios from "axios";
import dados from "../../data/verificacao.json";

export const api = axios.create({
  baseURL: dados.urlBase,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error.response?.data?.message || error.message || "Erro inesperado";
    return Promise.reject(new Error(message));
  }
);
