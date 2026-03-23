import { useState, useEffect } from "react";
import { Paciente } from "../types";

export function useHome() {
  const [medicoNome] = useState("Doutor(a)");
  const [pacientes, setPacientes] = useState<Paciente[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState(false);
  const [busca, setBusca] = useState("");
  const [filtro, setFiltro] = useState<"nome" | "cpf" | "cidade" | "">("");

  useEffect(() => {
    async function fetchPacientes() {
      try {
        setLoading(true);
        setErro(false);

        // Quando Paulo terminar, eu troco pra:
        // const response = await api.get('/patients');
        // setPacientes(response.data);

        const response = await fetch("/mock-dados.json");

        if (!response.ok) {
          console.warn("Arquivo não encontrado, usando fallback local.");
          const fallbackData = [
            {
              id: "1",
              nomeCompleto: "João Silva",
              cpf: "111",
              endereco: "Rua da Energisa",
              contato: "8585",
              dataDeNascimento: "2015-05-20",
              transtornos: ["TEA"],
              responsaveis: ["Maria"],
              fotoPreAssinada: "",
            },
          ];
          setPacientes(await Promise.resolve(fallbackData));
          setLoading(false);
          return;
        }

        const data = await response.json();

        setTimeout(() => {
          setPacientes(data);
          setLoading(false);
        }, 1500);
      } catch (err) {
        console.error(err);
        setErro(true);
        setLoading(false);
      }
    }

    fetchPacientes();
  }, []);

  const pacientesFiltrados = pacientes.filter((pac) => {
    if (!busca) return true;
    const termo = busca.toLowerCase();

    if (filtro === "cpf") return pac.cpf.includes(termo);
    if (filtro === "cidade") return pac.endereco.toLowerCase().includes(termo);

    return pac.nomeCompleto.toLowerCase().includes(termo);
  });

  return {
    medicoNome,
    pacientes: pacientesFiltrados,
    loading,
    erro,
    busca,
    setBusca,
    setFiltro,
  };
}
