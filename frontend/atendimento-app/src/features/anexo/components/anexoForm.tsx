"use client";

import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import { useState, useEffect } from "react";
import { Upload, CirclePlus } from "lucide-react";
import { toast } from "sonner";
import {
  normalizar,
  validarTexto,
  validarDataISO,
  validarArquivo,
} from "@/features/relatorio/utils/sanitizeRelatorio";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";

export type DocumentoFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList;
  descricao: string;
};

export enum TipoArquivo {
  anexo = 1,
  relatorio = 2,
}

export type AnexoEnvioFormData = DocumentoFormData & {
  pacienteId?: string;
  tipoArquivo: TipoArquivo.anexo;
};

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
  const descricao = watch("descricao");
  const arquivo = watch("arquivo");

  const existeArquivo = arquivo && arquivo.length > 0;
  const existeTexto = titulo?.trim().length > 0 && descricao?.trim().length > 0;
  const podeEnviar = existeArquivo && existeTexto;

  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  useEffect(() => {
    if (arquivo && arquivo.length > 0 && arquivo[0] instanceof File) {
      const url = URL.createObjectURL(arquivo[0]);
      setPreviewUrl(url);
      
      return () => URL.revokeObjectURL(url);
    }
    setPreviewUrl(null);
  }, [arquivo]);
  
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

  const onSubmitLocal = (data: AnexoEnvioFormData) => {
    try {
      const tituloNorm = normalizar(data.titulo);
      const descNorm = normalizar(data.descricao);
      validarTexto(tituloNorm, descNorm);
      validarDataISO(data.data);

      const file = data.arquivo?.[0];
      if (!file) throw new Error("Selecione um arquivo.");
      const fileValidado = validarArquivo(file);

      onSubmit({
        ...data,
        titulo: tituloNorm,
        descricao: descNorm,
        arquivo: {
          0: fileValidado,
          length: 1,
          item: () => fileValidado,
        } as unknown as FileList,
        tipoArquivo: TipoArquivo.anexo,
      });
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "Erro ao validar anexo");
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmitLocal)}
      className="grid gap-6 pt-5 text-[#344054]"
    >
      {/* DATA + TÍTULO */}
      <div className="grid gap-2">
        <Label htmlFor="dataInput">
          Data<span className="text-[#F28C38]">*</span>
        </Label>

        <Input
          id="dataInput"
          type="date"
          className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
          {...register("data")}
        />

        <Input
          placeholder="Insira o título do anexo*"
          className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
          {...register("titulo")}
        />
      </div>

      {/* DESCRIÇÃO */}
      <Textarea
        placeholder="Insira a descrição do anexo"
        className="min-h-[100px] w-full rounded-[30px] border border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] px-5 py-3 text-sm"
        {...register("descricao")}
      />

      {/* ARQUIVO */}
      <div className="grid gap-2">
        <Label htmlFor="arquivo">Inserir arquivo</Label>

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

      {/* BOTÃO */}
      <DialogFooter>
        <Button
          type="submit"
          disabled={!podeEnviar}
          className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
        >
          <CirclePlus className="mr-1" />
          Adicionar Anexo
        </Button>
      </DialogFooter>
    </form>
  );
}
