import data from "../../data/verificacao.json";

export async function apagarAnexo(objectName?: string) {
  try {
    console.log(`${data.urlBase}/arquivo/delete/?objectName=${objectName}`)
    const response = await fetch(
      `${data.urlBase}/arquivo/delete/?objectName=${objectName}`,
      {
        method: "DELETE",
      }
    );

    if (!response.ok) {
      throw new Error("Erro ao apagar anexo");
    }

  } catch (error) {
    throw error;
  }
}