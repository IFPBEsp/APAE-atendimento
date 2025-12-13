import dados from "../../data/verificacao.json";
export async function handleDownload (objectName: string, bucket: string){ 
  const response = await fetch(`${dados.urlBase}/pressigned/${bucket}?objectName=${objectName}`);
  const presignedUrl = await response.text();
  window.location.href = presignedUrl;
};
