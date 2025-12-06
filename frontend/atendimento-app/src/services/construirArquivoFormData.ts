    import {AnexoFormData} from "../components/forms/anexoForm"
    import dados from "../../data/verificacao.json"

    export function construirArquivoFormData (data: AnexoFormData) : FormData {
        const formData: FormData = new FormData();

        if(data?.arquivo?.[0] && data?.arquivo?.length > 0){
          const arquivo = data.arquivo[0];
          formData.append("file", arquivo, arquivo.name);
        }
        console.log(data.data)
          const metadata = {
                data: data.data,
                tipoArquivo: dados.tipoArquivo,
                profissionalId: dados.idProfissional,
                pacienteId: dados.idPaciente,
                titulo: data.titulo,
                descricao: data.descricao,
            };
        formData.append("metadata", new Blob (
            [JSON.stringify(metadata)],{type: "application/json"}
        )
    );

        console.log(formData);
        return formData;
    }