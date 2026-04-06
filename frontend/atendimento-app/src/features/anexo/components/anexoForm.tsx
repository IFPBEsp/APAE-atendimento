import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "../../../components/ui/textarea";
import { useState, useMemo } from "react";
import { Upload, CirclePlus } from "lucide-react";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";

const regexTitulo = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()']*$/u;
const regexDescricao = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()'%&#]*$/u;

// Data de fundação da APAE Esperança: 21/09/1993
const DATA_FUNDACAO_APAE = new Date(1993, 8, 21); // Meses são base 0 em JS (8 = Setembro)

const schema = z.object({
  data: z.string().refine((val) => {
    const dataSelecionada = new Date(val);
    const hoje = new Date();
    hoje.setHours(23, 59, 59, 999);
    const limite30Anos = new Date();
    limite30Anos.setFullYear(hoje.getFullYear() - 30);
    limite30Anos.setHours(0, 0, 0, 0);

    return dataSelecionada >= limite30Anos && dataSelecionada <= hoje && dataSelecionada >= DATA_FUNDACAO_APAE;
  }, { message: "Data fora do período permitido (últimos 30 anos) ou anterior à fundação da APAE (21/09/1993)." }),
  
  titulo: z.string()
    .transform(val => val.trim().replace(/\s{2,}/g, " "))
    .refine(val => val.length > 0, "O título é obrigatório")
    .refine(val => regexTitulo.test(val), "Título possui caracteres inválidos ou é composto apenas por números."),

  descricao: z.string()
    .transform(val => val.trim().replace(/\s{2,}/g, " "))
    .refine(val => val === "" || regexDescricao.test(val), "Descrição possui caracteres inválidos ou é composta apenas por números.")
    .optional(),

  arquivo: z.any()
    .refine((files) => files?.length > 0, "O arquivo é obrigatório")
    .refine((files) => {
      const type = files?.[0]?.type;
      return ["application/pdf", "image/jpeg", "image/png", "image/gif"].includes(type);
    }, "Apenas PDF ou Imagens (JPEG/PNG/GIF) são permitidos."),
});

export type DocumentoFormData = z.infer<typeof schema>;

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
  const { register, handleSubmit, watch, setValue, formState: { errors, isValid } } =
    useForm<AnexoEnvioFormData>({
      resolver: zodResolver(schema),
      mode: "onChange",
      defaultValues: {
        data: new Date().toISOString().split("T")[0],
        titulo: "",
        descricao: "",
      },
    });

  const [isDragging, setIsDragging] = useState(false);

  const arquivo = watch("arquivo");

  const previewUrl = useMemo(() => {
    return arquivo?.[0] ? URL.createObjectURL(arquivo[0]) : null;
  }, [arquivo]);

  const renderizar =
    previewUrl &&
    arquivo &&
    renderizarFormatoArquivo(arquivo[0].type, previewUrl);
  
  const removerArquivo = () => setValue("arquivo", undefined, { shouldValidate: true });

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

    setValue("arquivo", fileList, { shouldValidate: true });
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
          className={`rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] ${errors.data ? 'border-red-500' : ''}`}
          {...register("data")}
        />
        {errors.data && <span className="text-red-500 text-xs">{errors.data.message}</span>}

        <Input
          placeholder="Insira o título do anexo*"
          className={`p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] ${errors.titulo ? 'border-red-500' : ''}`}
          {...register("titulo")}
        />
        {errors.titulo && <span className="text-red-500 text-xs">{errors.titulo.message}</span>}
      </div>

      <Textarea
        placeholder="Insira a descrição do anexo"
        className={`min-h-[100px] w-full rounded-[30px] border border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] px-5 py-3 text-sm ${errors.descricao ? 'border-red-500' : ''}`}
        {...register("descricao")}
      />
      {errors.descricao && <span className="text-red-500 text-xs">{errors.descricao.message}</span>}

      <div className="grid gap-2">
        <Label>Inserir arquivo</Label>

        <div
          className={`
            relative w-full h-[220px] flex flex-col items-center justify-center 
            border-2 border-dashed rounded-[30px] cursor-pointer bg-[#F8FAFD] overflow-hidden
            transition-colors
            ${isDragging ? "border-blue-400 bg-blue-50" : "border-[#B2D7EC]"}
            ${errors.arquivo ? "border-red-500" : ""}
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
                <div
                  className="flex items-center gap-2 px-6 py-2 rounded-full
                    bg-[#0D4F97] text-white font-semibold h-9"
                >
                  <Upload className="h-4 w-4 text-white" />
                  Enviar arquivo
                </div>
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
          accept=".pdf,image/*"
          onChange={(e) => setValue("arquivo", e.target.files, { shouldValidate: true })}
        />
        {errors.arquivo && <span className="text-red-500 text-xs text-center">{(errors.arquivo as any).message}</span>}
      </div>

      <DialogFooter>
        <Button
          type="submit"
          disabled={!isValid}
          className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <CirclePlus className="mr-1" />
          Adicionar Anexo
        </Button>
      </DialogFooter>
    </form>
  );
}
