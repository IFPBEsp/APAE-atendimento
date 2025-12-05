import {AnexoFormData} from "../components/forms/anexoForm"

export function construirArquivoFormData (data: AnexoFormData) : FormData {
    const formData: FormData = new FormData();

    formData.append("file", data.data);
    formData.append("metadata", new Blob (
        [JSON.stringify({
             name: data?.arquivo?.[0]?.name,              
             titulo: data?.titulo,
             descricao: data.descricao
        })]
    ))

    console.log(formData);
    return formData;
}