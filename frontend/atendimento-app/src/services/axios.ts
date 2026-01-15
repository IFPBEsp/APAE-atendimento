import axios from "axios";
import dados from "../../data/verificacao.json";
export const api = axios.create({
  baseURL: dados.urlBase,
  headers: {
    Accept: "application/json",
  },
});

api.interceptors.request.use((config) => {
  if (config.data instanceof FormData) {
    // NÃO define Content-Type
    delete config.headers["Content-Type"];
  } else {
    config.headers["Content-Type"] = "application/json";
  }

  return config;
});

