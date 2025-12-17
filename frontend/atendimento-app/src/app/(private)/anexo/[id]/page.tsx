"use client";

import {useRouter, useParams} from "next/navigation";
import { ArrowLeft, ClipboardPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import AnexoForm, {AnexoEnvioFormData, TipoArquivo } from "@/components/forms/anexoForm";
import { AnexoModal } from "@/components/modals/novoAnexoModal";
import AnexoCard from "@/components/cards/anexoCard";
import { AnexoViewModal, AnexoDeleteModal } from "@/components/modals/anexoModal";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";
import { enviarArquivo } from "@/api/enviarArquivo";
import {Anexo, AnexoResponse} from "../../../../types/Anexo";
import { buscarArquivos } from "@/api/buscarArquivos";
import dados from "../../../../../data/verificacao.json"
import { validarTipoArquivo } from "@/services/validarTipoArquivo";
import {toast} from "sonner";
import { handleDownload } from "@/api/salvarAnexo";
import { apagarAnexo } from "@/api/apagarAnexo";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { obterNomePaciente } from "@/api/nomePaciente";


const nunitoFont = Nunito({ weight: "700" });

export default function AnexoPage() { 
  const [nomePaciente, setNomePaciente] = useState("Loren Ipsun");
  const [anexos, setAnexos] = useState<Anexo[]>([]);
  const router = useRouter();
  const params = useParams();
  const pacienteIdStr = Array.isArray(params.id) ? params.id[0] : params.id;


  async function obterResultadoBuscarAnexos() : Promise<Anexo[]> {
    if (!pacienteIdStr) return [];
      const resposta = await buscarArquivos(
    pacienteIdStr,
    TipoArquivo.anexo
  ) as AnexoResponse[];

  return resposta.map((e: AnexoResponse , i) => ({
    id: ++i,
    ...e
  }));
}

  useEffect(() => {    
      (async () => {
        try{
 const [anexosResult, nomePaciente] = await Promise.all([await obterResultadoBuscarAnexos(), await obterNomePaciente(pacienteIdStr)]);
        setNomePaciente(nomePaciente);
        setAnexos(anexosResult);  
        }catch(error){
          const mensagem = error instanceof Error ? error.message : "Erro inesperado";
          toast.error(mensagem);
        }
      })()
  }, [])
  
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Anexo | null>(null);
  const [reportToView, setReportToView] = useState<Anexo | null>(null);

  const handleDelete = async (objectName: string) => {
    if (reportToDelete) {
      setAnexos((prev) => prev.filter((r) => r.id !== reportToDelete.id));
      setReportToDelete(null);
    }
     await apagarAnexo(objectName);
  };

  const handleUpdate = async (objectName: string) => {
    if (!objectName) return;
    await handleDownload(objectName);
  }

  const anexosFiltrados =
    dataSelecionada
      ? anexos.filter((r) => r.data === dataSelecionada)
      : anexos;

  async function enviarArquivoAnexo(data: AnexoEnvioFormData) {
  try {
    const request : AnexoEnvioFormData = {
      ...data,
      pacienteId: pacienteIdStr,
      tipoArquivo: TipoArquivo.anexo,
      profissionalId: dados.idProfissional
    }
    const respostaCriacao = await handleCreateAnexo(request);
    if (respostaCriacao.sucesso) {
      toast.success(respostaCriacao.mensagem || "Anexo enviado com sucesso!");
      return;
    }

    toast.error(respostaCriacao.mensagem || "Erro ao enviar o anexo.");

  } catch (err) {
      console.error("Erro inesperado:", err);
  const mensagem = err instanceof Error ? err.message : String(err);
  toast.error(mensagem || "Erro inesperado ao enviar o anexo.");
  }
}

  async function reloadAnexos() {
  const anexosResult = await obterResultadoBuscarAnexos();
  setAnexos(anexosResult);
}

  async function handleCreateAnexo(data: AnexoEnvioFormData) {
   
    try{
    validarTipoArquivo(data.arquivo);
    validarTamanhoArquivo(data.arquivo);
    const formData : FormData = construirArquivoFormData(data);
    await enviarArquivo(formData);
    await reloadAnexos();
     return {
      sucesso: true,
      mensagem: "Anexo enviado com sucesso!"
    }
    }catch(error: unknown){
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
              Adicionar anexo
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

        {anexosFiltrados.length === 0 && (
          <div className="text-center mt-20">
            <p
              className={`text-[#344054] text-[15px] font-medium ${nunitoFont.className}`}
            >
              Nenhum anexo encontrado para esta data.
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
                Adicionar anexo.
              </Button>
            )}
          </div>
        )}

        {anexosFiltrados.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-2">
            {anexosFiltrados.map((item) => (
              <AnexoCard
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

        <AnexoModal
          open={open}
          onOpenChange={setOpen}
        >
          <AnexoForm
            onSubmit={enviarArquivoAnexo}
          />
        </AnexoModal>

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

      <AnexoDeleteModal
        isOpen={!!reportToDelete}
        onClose={() => setReportToDelete(null)}
        onConfirm={() => reportToDelete && handleDelete(reportToDelete.objectName)}
      />

      <AnexoViewModal
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
