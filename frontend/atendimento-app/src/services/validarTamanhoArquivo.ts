

export function validarTamanhoArquivo(files?: FileList){
    if(!files || files.length <= 0){
        throw new Error("Nenhum arquivo selecionado.");
    }
    const file = files[0];
    const maxSizeMB = 50;
    if (file.size > maxSizeMB * 1024 * 1024) {
    throw new Error("Tamanho de Arquivo muito grande. Envie arquivos até 50 mb");
}
}
