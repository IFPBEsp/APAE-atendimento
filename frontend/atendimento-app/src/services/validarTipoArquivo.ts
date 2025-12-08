export function validarTipoArquivo(files: FileList | undefined){
    if(!files || files.length <= 0){
        throw new Error("Nenhum arquivo selecionado.");
    }
    const file = files[0];
    const allowedTypes = ["image/jpeg", "image/jpg","image/png","application/pdf"];
    if(!allowedTypes.includes(file.type)){
        throw new Error("Tipo de Arquivo inválido. Envie somente PDF, PNG, JPEG, JPG.");
    }
}