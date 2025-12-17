import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "../ui/textarea";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { useState } from "react";
import { Upload, CirclePlus, Info, FileText } from "lucide-react";
import { RelatorioEnvioFormData } from "./anexoForm";
import { pdf} from "@react-pdf/renderer";
import { TemplateRelatorio } from "../pdf/templateRelatorio";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";
import { PacientePdfDTO, ProfissionalPdfDTO } from "@/api/dadosRelatorioPdf";

export type RelatorioFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList;
  descricao: string;
};

interface RelatorioFormProps {
  onSubmit: (data: RelatorioEnvioFormData) => void;
  dadosPdf: {
    paciente: PacientePdfDTO;
    profissional: ProfissionalPdfDTO;
  } | null;

  carregandoPdf: boolean;
}

export default function RelatorioForm({ 
  onSubmit,
  dadosPdf,
  carregandoPdf,
}: RelatorioFormProps) {
  const { register, handleSubmit, watch, setValue } = useForm<RelatorioEnvioFormData>({
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
  const existeTemplate = titulo?.trim().length > 0 && descricao?.trim().length > 0;

  const podeEnviarAnexo = existeArquivo && existeTemplate;
  const podeGerarPdf = !existeArquivo && existeTemplate;

  const gerarPdfEAnexar = async () => {
    if (!dadosPdf) return;

    const blob = await pdf(
      <TemplateRelatorio
        paciente={dadosPdf.paciente}
        profissional={dadosPdf.profissional}
        titulo={titulo}
        descricao={descricao}
      />
    ).toBlob();

    const file = new File(
      [blob], `Relatorio-${titulo || "relatorio"}.pdf`, { type: "application/pdf"}
    );

    const fileList = {
      0: file,
      length: 1,
      item: () => file,
    } as unknown as FileList;

    setValue("arquivo", fileList, { shouldValidate: true});
  };

  const previewUrl = arquivo?.[0] ? URL.createObjectURL(arquivo[0]) : null;
  const renderizar = (previewUrl && arquivo) && renderizarFormatoArquivo(arquivo[0].type, previewUrl);

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

        <div>
          <Input
            placeholder="Insira o título do relatório*"
            className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
            {...register("titulo")}
          />

          <Dialog>
            <DialogTrigger asChild className="cursor-pointer">
              <button type="button">
                <Info size={16} className="text-gray-500" />
              </button>
            </DialogTrigger>

            <DialogContent className="rounded-2xl">
              <DialogHeader>
                <DialogTitle>Como funciona o template?</DialogTitle>
                <DialogDescription>
                  Ao gerar o relatório por template, o sistema cria um arquivo
                  padronizado automaticamente, incluindo cabeçalho e informações
                  essenciais.
                </DialogDescription>
              </DialogHeader>
            </DialogContent>
          </Dialog>

        </div>

      </div>

      <Textarea
        placeholder="Insira a descrição do relatório"
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
          {previewUrl ? 
            (renderizar)
          : (
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
          disabled={!podeEnviarAnexo}
          className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
        >
          <CirclePlus className="mr-1" />
          Adicionar Relatório
        </Button>
      </DialogFooter>


      <div className="w-full">
        <Button
          type="button"
          onClick={gerarPdfEAnexar}
          disabled={!podeGerarPdf || carregandoPdf}
          className="w-full rounded-[30px] bg-[#0D4F97]"
        >
          <FileText className="mr-2"/>
          {carregandoPdf ? "Gerando PDF..." : "Gerar PDF"}
        </Button>
      </div>
    </form>
  );
}
