import { useState, useEffect } from "react";
import { Paciente } from "../types";
import { api } from "@/services/axios";

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

        // utilizando o endpoint de testes
        const response = await api.get("/profissionais/pacientes");
        
        setPacientes(response.data);
        setLoading(false);

      } catch (err) {
        console.error("Erro ao carregar pacientes do Back-end:", err);
        setErro(true);
        setLoading(false);
      }
    }

    fetchPacientes();
  }, []);

  // Utilizando filtros 'null' por conta dos atributos especificamente selecionados (@Transient)
  const pacientesFiltrados = pacientes.filter((pac) => {
    if (!busca) return true;
    const termo = busca.toLowerCase();

    if (filtro === "cpf") {
        return pac.cpf?.includes(termo);
    }
    
    if (filtro === "cidade") {
        const cidade = pac.cidade || ""; 
        return cidade.toLowerCase().includes(termo);
    }

    return pac.nomeCompleto?.toLowerCase().includes(termo);
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