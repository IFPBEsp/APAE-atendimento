"use client";

import {useRouter, useParams} from "next/navigation";
import { ArrowLeft, ClipboardPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import RelatorioForm from "@/components/forms/relatorioForm";
import { RelatorioModal } from "@/components/modals/novoRelatorioModal";
import RelatorioCard from "@/components/cards/relatorioCard";
import { RelatorioViewModal, RelatorioDeleteModal } from "@/components/modals/relatorioModal";
import { Relatorio, RelatorioResponse } from "@/types/Relatorio";
import { RelatorioEnvioFormData, TipoArquivo } from "@/components/forms/anexoForm";
import dados from "../../../../../data/verificacao.json"
import { toast } from "sonner";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";
import { buscarArquivos } from "@/api/buscarArquivos";
import { enviarArquivo } from "@/api/enviarArquivo";
import { apagarAnexo } from "@/api/apagarAnexo";
import { handleDownload } from "@/api/salvarAnexo";
import { obterNomePaciente } from "@/api/nomePaciente";

const nunitoFont = Nunito({ weight: "700" });

export default function RelatorioPage() {
  const router = useRouter();
  const [nomePaciente, setNomePaciente] = useState("Loren Ipsun");
  const params = useParams();
  const pacienteIdStr = Array.isArray(params.id) ? params.id[0] : params.id;
  const [relatorios, setRelatorios] = useState<Relatorio[]>([]);

  async function obterResultadoBuscarRelatorio() : Promise<Relatorio[]>{
     if (!pacienteIdStr) return [];
      const resposta = await buscarArquivos(
         pacienteIdStr,
         TipoArquivo.relatorio
       ) as RelatorioResponse[];
       return resposta.map((e: RelatorioResponse , i) => ({
           id: ++i,
           ...e
         }));
  }

  async function reloadRelatorio() {
  const relatoriosResult = await obterResultadoBuscarRelatorio();
  setRelatorios(relatoriosResult);
}

  const handleUpdate = async (objectName: string) => {
    if (!objectName) return;
    await handleDownload(objectName);
  }


  useEffect(() => {    
        (async () => {
          try{
   const [relatoriosResponse, nomePaciente] = await Promise.all([await obterResultadoBuscarRelatorio(), await obterNomePaciente(pacienteIdStr)]);
          setNomePaciente(nomePaciente);
          setRelatorios(relatoriosResponse);
          }catch(error){
            const mensagem = error instanceof Error ? error.message : "Erro inesperado";
            toast.error(mensagem);
          }
        })()
    }, [])
 

  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Relatorio | null>(null);
  const [reportToView, setReportToView] = useState<Relatorio | null>(null);

  const handleDelete = async (objectName: string) => {
    if (reportToDelete) {
      setRelatorios((prev) => prev.filter((r) => r.id !== reportToDelete.id));
      setReportToDelete(null);
    }
    await apagarAnexo(objectName);
  };

  async function enviarArquivoRelatorio (data: RelatorioEnvioFormData){
     try{
      const request : RelatorioEnvioFormData = {
      ...data,
      pacienteId: pacienteIdStr,
      tipoArquivo: TipoArquivo.relatorio,
      profissionalId: dados.idProfissional
    }

    const respostaCriacao = await handleCreateRelatorio(request);

    if(respostaCriacao.sucesso) {
       toast.success(respostaCriacao.mensagem || "Relatório criado com sucesso!");
       return;
    }

    toast.error(respostaCriacao.mensagem || "Erro ao enviar o relatório.")
    }catch(error){
const mensagem = error instanceof Error ? error.message : String(error);
  toast.error(mensagem || "Erro inesperado ao enviar o relatório.");
    }

  }

  const relatoriosFiltrados =
    dataSelecionada
      ? relatorios.filter((r) => r.data === dataSelecionada)
      : relatorios;

  async function handleCreateRelatorio(data: RelatorioEnvioFormData) {
    try{
    validarTamanhoArquivo(data.arquivo);
    const formData : FormData = construirArquivoFormData(data);
    await enviarArquivo(formData);
    await reloadRelatorio();
    return {
      sucesso: true,
      mensagem: "Relatório enviado com sucesso!"
    }
    }catch(error){
 let mensagem = "Erro inesperado";

  if (error instanceof Error) {
    mensagem = error.message; 
  }
     return {
      sucesso: false,
      mensagem
    };
    }finally{
      setOpen(false);
    }
   
   
  }

  return (
    <div className="min-h-screen w-full bg-[#F8FAFD]">
      <Header />

      <section className="px-5 pt-4 mx-auto w-full">
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => router.back()}
            className="h-[38px] px-4 rounded-full flex items-center gap-2 bg-[#EDF2FB] text-sm text-gray-700 cursor-pointer"
          >
            <ArrowLeft size={18} />
            Voltar
          </button>

          <div className="flex items-center gap-3">
            <Button
              onClick={() => setOpen(true)}
              className="hidden cursor-pointer md:flex items-center bg-[#165BAA] hover:bg-[#13447D] text-white gap-2 px-4 h-[38px] rounded-full text-sm shadow-sm active:scale-95"
            >
              <ClipboardPlus size={18} />
              Gerenciar relatórios
            </Button>

            <Input
              type="date"
              value={dataSelecionada}
              onChange={(e) => setDataSelecionada(e.target.value)}
              className="bg-white border border-[#3B82F6] rounded-full w-[150px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6]"
            />
          </div>
        </div>
      </section>

      <section className="bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">
        <h1 className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}>
          {nomePaciente}
        </h1>

        {relatoriosFiltrados.length === 0 && (
          <div className="text-center mt-20">
            <p
              className={`text-[#344054] text-[15px] font-medium ${nunitoFont.className}`}
            >
              Nenhum relatório encontrado para esta data.
            </p>

            {dataSelecionada && (
              <Button
                variant="link"
                onClick={() => setDataSelecionada("")}
                className="text-[#165BAA] underline text-sm hover:opacity-80 pt-0 cursor-pointer"
              >
                Limpar filtro
              </Button>
            )}

            {!dataSelecionada && (
              <Button
                variant="link"
                onClick={() => setOpen(true)}
                className="text-[#165BAA] underline text-sm hover:opacity-80 pt-0 cursor-pointer"
              >
                Adicionar relatório.
              </Button>
            )}
          </div>
        )}

        {relatoriosFiltrados.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-2">
            {relatoriosFiltrados.map((item) => (
              <RelatorioCard
                key={item.id}
                id={item.id}
                titulo={item.titulo}
                data={item.data}
                fileName={item.nomeArquivo}
                imageUrl={item.presignedUrl}
                onView={() => setReportToView(item)}
                onDelete={() => setReportToDelete(item)}
              />
            ))}
          </div>
        )}

        <RelatorioModal open={open} onOpenChange={setOpen}>
          <RelatorioForm onSubmit={enviarArquivoRelatorio} />
        </RelatorioModal>

      </section>

      <button
        onClick={() => setOpen(true)}
        className="
          fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA]
          flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)]
          active:scale-95 md:hidden"
      >
        <ClipboardPlus size={28} className="text-white" />
      </button>

      <RelatorioDeleteModal
        isOpen={!!reportToDelete}
        onClose={() => setReportToDelete(null)}
        onConfirm={() => reportToDelete && handleDelete(reportToDelete.objectName)}
      />

      <RelatorioViewModal
        isOpen={!!reportToView}
        onClose={() => setReportToView(null)}
        titulo={reportToView?.titulo ?? ""}
        data={reportToView}
        descricao={reportToView?.descricao ?? ""}
        onUpdate={() => reportToView && handleUpdate(reportToView.objectName)}
      />
    </div>
  );
}
