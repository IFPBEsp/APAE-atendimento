import dados from "../../data/verificacao.json";
export async function handleDownload (objectName: string, bucket: string, fileName: string){
  const response = await fetch(`${dados.urlBase}/pressigned/${bucket}?objectName=${objectName}&fileName=${fileName}`);
  const presignedUrl = await response.text();
  window.location.href = presignedUrl;
};
