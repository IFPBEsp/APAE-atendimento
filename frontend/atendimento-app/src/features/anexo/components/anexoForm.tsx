import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "../../../components/ui/textarea";
import { useState } from "react";
import { Upload, CirclePlus } from "lucide-react";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";

export type DocumentoFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList;
  descricao: string;
};

export type DocumentoFormDataEnvio = {
  pacienteId?: string;
};

export type AnexoEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.anexo;
  };

export type RelatorioEnvioFormData = DocumentoFormData &
  DocumentoFormDataEnvio & {
    tipoArquivo: TipoArquivo.relatorio;
  };

export enum TipoArquivo {
  anexo = 1,
  relatorio = 2,
}

interface AnexoFormProps {
  onSubmit: (data: AnexoEnvioFormData) => void;
}

export default function AnexoForm({ onSubmit }: AnexoFormProps) {
  const { register, handleSubmit, watch, setValue } =
    useForm<AnexoEnvioFormData>({
      defaultValues: {
        data: new Date().toISOString().split("T")[0],
        titulo: "",
        descricao: "",
      },
    });

  const [isDragging, setIsDragging] = useState(false);

  const titulo = watch("titulo");
  const arquivo = watch("arquivo");

  const existeArquivo = arquivo && arquivo.length > 0;
  const existeTemplate =
    titulo?.trim().length > 0;

  const envioValidado = existeArquivo && existeTemplate;

  const previewUrl = arquivo?.[0] ? URL.createObjectURL(arquivo[0]) : null;
  const renderizar =
    previewUrl &&
    arquivo &&
    renderizarFormatoArquivo(arquivo[0].type, previewUrl);
  const removerArquivo = () => setValue("arquivo", undefined);

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => setIsDragging(false);

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);

    const file = e.dataTransfer.files?.[0];
    if (!file) return;

    const fileList = {
      0: file,
      length: 1,
      item: () => file,
    } as unknown as FileList;

    setValue("arquivo", fileList);
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="grid gap-6 pt-5 text-[#344054]"
    >
      <div className="grid gap-2">
        <Label>
          Data<span className="text-[#F28C38]">*</span>
        </Label>

        <Input
          type="date"
          className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
          {...register("data", { required: true })}
        />

        <Input
          placeholder="Insira o título do anexo*"
          className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
          {...register("titulo")}
        />
      </div>

      <Textarea
        placeholder="Insira a descrição do anexo"
        className="min-h-[100px] w-full rounded-[30px] border border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] px-5 py-3 text-sm"
        {...register("descricao")}
      />

      <div className="grid gap-2">
        <Label>Inserir arquivo</Label>

        <div
          className={`
            relative w-full h-[220px] flex flex-col items-center justify-center 
            border-2 border-dashed rounded-[30px] cursor-pointer bg-[#F8FAFD] overflow-hidden
            transition-colors
            ${isDragging ? "border-blue-400 bg-blue-50" : "border-[#B2D7EC]"}
          `}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          {previewUrl ? (
            renderizar
          ) : (
            <>
              <Label
                htmlFor="arquivo"
                className="flex flex-col items-center gap-2 pointer-events-auto cursor-pointer"
              >
                <button
                  type="button"
                  className="pointer-events-none flex items-center gap-2 px-6 py-2 rounded-full
                    bg-[#0D4F97] text-white font-semibold h-9"
                >
                  <Upload className="h-4 w-4 text-white" />
                  Enviar arquivo
                </button>
              </Label>

              <p className="hidden md:block text-sm text-gray-500 mt-2">
                Ou arraste arquivos até aqui
              </p>
            </>
          )}

          {arquivo?.[0] && (
            <div
              className="absolute bottom-0 left-1/2 transform -translate-x-1/2 
                bg-white bg-opacity-70 border border-[#B2D7EC]
                text-[#344054] text-sm px-3 py-1 rounded-full flex items-center gap-2"
            >
              <span className="max-w-[150px] truncate">{arquivo[0].name}</span>

              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  removerArquivo();
                }}
                className="text-gray-500 hover:text-red-500 font-bold text-xs cursor-pointer"
              >
                ✕
              </button>
            </div>
          )}
        </div>

        <Input
          id="arquivo"
          type="file"
          className="hidden"
          {...register("arquivo")}
        />
      </div>

      <DialogFooter>
        <Button
          type="submit"
          disabled={!envioValidado}
          className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
        >
          <CirclePlus className="mr-1" />
          Adicionar Anexo
        </Button>
      </DialogFooter>
    </form>
  );
}
