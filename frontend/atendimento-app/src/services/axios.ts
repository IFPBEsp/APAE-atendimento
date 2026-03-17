import axios from "axios";
import { getFirebaseAuth } from "@/lib/firebase";
import { toast } from 'sonner';


export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    Accept: "application/json",
  },
});

api.interceptors.request.use(async (config) => {
  if (config.data instanceof FormData) {
    delete config.headers["Content-Type"];
  } else {
    config.headers["Content-Type"] = "application/json";
  }

  try {
    const auth = getFirebaseAuth();
    const user = auth.currentUser;

    if (user) {
      const token = await user.getIdToken();
      config.headers.Authorization = `Bearer ${token}`;
    }
  } catch (error) {
    console.error(error);
  }

  return config;
});

api.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      if (error.response && error.response.data) {
        const mensagemDoBackend = error.response.data.message;

        if (mensagemDoBackend) {
          toast.error(mensagemDoBackend);
        } else {
          toast.error("Ocorreu um erro interno no servidor.");
        }
      } else if(error.request) {
        toast.error("Não foi possível conectar ao servidor. Tente novamente mais tarde.");
      } else {
        toast.error("Erro inesperado na aplicação");
      }

      return Promise.reject(error);
    }
)