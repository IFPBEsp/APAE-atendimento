import data from "../../data/verificacao.json";

export async function apagarAnexo(objectName?: string, bucket?: string) {
  try {
    if (!bucket) {
      throw new Error("Erro ao apagar anexo");
    }
console.log(`${data.urlBase}/arquivo/delete/${bucket}?objectName=${objectName}`)
    const response = await fetch(
      `${data.urlBase}/arquivo/delete/${bucket}?objectName=${objectName}`,
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