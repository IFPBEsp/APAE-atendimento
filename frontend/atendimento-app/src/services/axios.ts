import axios from "axios";
import { getAuth } from "firebase/auth";
import dados from "../../data/verificacao.json";

export const api = axios.create({
  baseURL: dados.urlBase,
  headers: {
    Accept: "application/json",
  },
});

api.interceptors.request.use(async (config) => {
  // Content-Type (mantém seu comportamento)
  if (config.data instanceof FormData) {
    delete config.headers["Content-Type"];
  } else {
    config.headers["Content-Type"] = "application/json";
  }

  // 🔐 Firebase ID Token
  const auth = getAuth();
  const user = auth.currentUser;

  if (user) {
    const token = await user.getIdToken(); // 👈 OBRIGATÓRIO
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});