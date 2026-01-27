import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080",
});

export async function setAuthToken(token: string) {
  api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

export default api;
