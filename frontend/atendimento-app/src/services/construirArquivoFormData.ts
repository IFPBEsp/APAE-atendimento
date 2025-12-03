import {AnexoFormData} from "../components/forms/anexoForm"

export function construirArquivoFormData (data: AnexoFormData) : FormData {
    const formData: FormData = new FormData();
    formData.append("data", data.data);
    formData.append("titulo", data.titulo);
    formData.append("descricao", data.descricao);
    
    if(data?.arquivo){
        formData.append("arquivo", data.arquivo[0]);
        formData.append("name", data.arquivo[0].name);
    }
    return formData;
}