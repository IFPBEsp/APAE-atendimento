export function validarTipoArquivo(files?: FileList){
    if(!files || files.length <= 0){
        throw new Error("Nenhum arquivo selecionado.");
    }
    const file = files[0];
    const allowedImageRegex = /^image\/.+$/
    const allowedPdf = "application/pdf";

    if(!(allowedImageRegex.test(file.type) || file.type === allowedPdf)){
        throw new Error("Tipo de Arquivo inválido. Envie somente PDF ou imagens (PNG, JPEG, JPG).");
    }
}