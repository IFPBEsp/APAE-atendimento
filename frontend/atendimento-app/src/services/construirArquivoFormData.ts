    import {AnexoEnvioFormData} from "../components/forms/anexoForm"

    export function construirArquivoFormData (data: AnexoEnvioFormData) : FormData {
        const formData: FormData = new FormData();

        if(data?.arquivo?.[0] && data?.arquivo?.length > 0){
          const arquivo = data.arquivo[0];
          formData.append("file", arquivo, arquivo.name);
        }
          const metadata = {
                data: data.data,
                tipoArquivo: data.tipoArquivo,
                profissionalId: data.profissionalId,
                pacienteId: data.pacienteId,
                titulo: data.titulo,
                descricao: data.descricao,
            };
        formData.append("metadata", new Blob (
            [JSON.stringify(metadata)],{type: "application/json"}
        )
    );

        return formData;
    }