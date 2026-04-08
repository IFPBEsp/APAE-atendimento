import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "../../../components/ui/textarea";
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
import { pdf } from "@react-pdf/renderer";
import { TemplateRelatorio } from "../../../components/pdf/templateRelatorio";
import { renderizarFormatoArquivo } from "@/utils/renderizarFormatoArquivo";
import { PacientePdfDTO, ProfissionalPdfDTO } from "@/features/relatorio/types";
import { RelatorioEnvioFormData } from "../types";
import {
  validarTexto,
  validarDataISO,
  validarArquivo,
} from "@/features/relatorio/utils/sanitizeRelatorio";
import { toast } from "sonner";

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
  const {register, handleSubmit, watch, setValue, formState: {errors}, setError, clearErrors} =
      useForm<RelatorioEnvioFormData>({
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

  const validarData = (dataSelecionada: string) => {
    const data = new Date(dataSelecionada);
    const hoje = new Date();
    const trintaAnosAtras = new Date();
    trintaAnosAtras.setFullYear(hoje.getFullYear() - 30);

    if (data > hoje || data < trintaAnosAtras) {
      return "Data inválida ou fora do limite de permitido (30 anos)";
    }
    return true;
  };

  const existeArquivo = arquivo && arquivo.length > 0;
  const existeTemplate =
      titulo?.trim().length > 0 && descricao?.trim().length > 0;

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
        />,
    ).toBlob();

    const file = new File([blob], `Relatorio-${titulo || "relatorio"}.pdf`, {
      type: "application/pdf",
    });

    const fileList = {
      0: file,
      length: 1,
      item: () => file,
    } as unknown as FileList;

    setValue("arquivo", fileList, {shouldValidate: true});
  };

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

    const extensoesBloqueadas = [".exe", ".bat", ".zip", ".mp3", ".bin", ".sh"];
    const isInvalido = extensoesBloqueadas.some(ext => file.name.toLowerCase().endsWith(ext));

    if (isInvalido) {
      setError("arquivo", {type: "manual", message: "Formato não suportado. Envie apenas PDF ou Imagens."});
      return;
    }

    clearErrors("arquivo");
    const fileList = {0: file, length: 1, item: () => file} as unknown as FileList;
    setValue("arquivo", fileList, {shouldValidate: true});
  };

  const onSubmitLocal = (data: RelatorioEnvioFormData) => {
    try {
      const { titulo, descricao } = validarTexto(data.titulo, data.descricao);
      const dataValida = validarDataISO(data.data);
      const arquivo = data.arquivo?.[0]
        ? validarArquivo(data.arquivo[0])
        : undefined;

      onSubmit({
        ...data,
        data: dataValida,
        titulo,
        descricao,
        arquivo: arquivo
          ? ({
              0: arquivo,
              length: 1,
              item: () => arquivo,
            } as unknown as FileList)
          : data.arquivo,
      });
    } catch (error) {
      const msg =
        error instanceof Error ? error.message : "Erro ao validar anexo";
      toast.error(msg);
    }
  };

  return (
      <form
          onSubmit={handleSubmit(onSubmit)}
          className="grid gap-6 pt-5 text-[#344054]"
      >
        <div className="grid gap-2">
          <Label htmlFor="dataInput">
            Data<span className="text-[#F28C38]">*</span>
          </Label>

          <Input
              id="dataInput"
              type="date"
              className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
              {...register("data", {
                required: "A data é obrigatória",
                validate: validarData
              })}
          />

          {errors.data && <span className="text-red-500 text-xs px-2">{errors.data.message as string}</span>}

          <div className="flex flex-col w-full">
            <div className="flex w-full">
              <Input
                  placeholder="Insira o título do relatório*"
                  className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] w-full"
                  {...register("titulo", {
                    required: "O título é obrigatório",
                    pattern: {
                      value: /^(?!\s+$)[a-zA-Z0-9\s\-_À-ÿ]+$/,
                      message: "O título não pode ser vazio ou conter caracteres especiais."
                    }
                  })}
              />

              <Dialog>
                <DialogTrigger asChild className="cursor-pointer">
                  <button type="button">
                    <Info size={16} className="text-gray-500"/>
                  </button>
                </DialogTrigger>

                <DialogContent className="rounded-2xl">
                  <DialogHeader>
                    <DialogTitle>Como criar um relatório?</DialogTitle>
                    <DialogDescription asChild>
                      <div className="space-y-4 text-sm">
                        Você pode criar um relatório de duas formas:
                        <br/>
                        <br/>
                        <strong>1. Gerar relatório por template</strong>
                        <br/>
                        <br/>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>
                            Preencha <strong>Título</strong> e{" "}
                            <strong>Descrição</strong>.
                          </li>
                          <li>
                            O botão <strong>Gerar PDF</strong> será habilitado.
                          </li>
                          <li>
                            Ao clicar, o sistema gera o PDF e o anexa
                            automaticamente.
                          </li>
                          <li>
                            Depois, clique em <strong>Adicionar Relatório</strong>{" "}
                            para salvar.
                          </li>
                        </ul>
                        <br/>
                        <strong>2. Enviar um arquivo</strong>
                        <br/>
                        <br/>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>
                            Preencha <strong>Título</strong> e{" "}
                            <strong>Descrição</strong>.
                          </li>
                          <li>Anexe um arquivo.</li>
                          <li>
                            O botão <strong>Adicionar Relatório</strong> será
                            habilitado.
                          </li>
                          <li>Você deve clicar-lo para salvar.</li>
                        </ul>
                        <br/>
                      </div>
                    </DialogDescription>
                  </DialogHeader>
                </DialogContent>
              </Dialog>
            </div>
            {errors.titulo && <span className="text-red-500 text-xs mt-1">{errors.titulo.message as string}</span>}
          </div>
        </div>

        <Textarea
            placeholder="Insira a descrição do relatório"
            className="min-h-[100px] w-full rounded-[30px] border border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] px-5 py-3 text-sm"
            {...register("descricao")}
        />

        <div className="grid gap-2">
          <Label>Inserir arquivo</Label>

          {errors.arquivo && <span className="text-red-500 text-xs">{errors.arquivo.message as string}</span>}

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
                      <Upload className="h-4 w-4 text-white"/>
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
              data-testid="arquivo-input"
              accept=".pdf, image/jpeg, image/png"
              className="hidden"
              {...register("arquivo", {
                onChange: (e) => {
                  const file = e.target.files?.[0];
                  if (file) {
                    const extensoesBloqueadas = [".exe", ".bat", ".zip", ".mp3", ".bin", ".sh"];
                    if (extensoesBloqueadas.some(ext => file.name.toLowerCase().endsWith(ext))) {
                      setError("arquivo", {
                        type: "manual",
                        message: "Formato não suportado. Envie apenas PDF ou Imagens."
                      });
                      setValue("arquivo", undefined);
                    } else {
                      clearErrors("arquivo");
                    }
                  }
                }
              })}
          />
        </div>

        <DialogFooter>
          <Button
              type="submit"
              disabled={!podeEnviarAnexo}
              className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
          >
            <CirclePlus className="mr-1"/>
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