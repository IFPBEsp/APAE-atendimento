"use client";
import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, ClipboardPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { AnexoModal } from "@/features/anexo/components/novoAnexoModal";
import AnexoCard from "@/features/anexo/components/anexoCard";
import {
  AnexoViewModal,
  AnexoDeleteModal,
} from "@/features/anexo/components/anexoModal";
import { useAnexos } from "../hooks/useAnexo";
import AnexoForm from "./anexoForm";
import { useRelatorioPDF } from "@/features/relatorio/hooks/useRelatorioPDF";


const nunitoFont = Nunito({ weight: "700" });

export default function AnexoPage() {
  const router = useRouter();
  const params = useParams();
  const pacienteId = typeof params.id === "string" ? params.id : "";

    const {
      anexos,
      loading,
      dataSelecionada,
      open,
      reportToDelete,
      reportToView, 
      setDataSelecionada,
      setReportToView,
      setReportToDelete,
      setOpen,
      enviarAnexo,
      deletarAnexo,
      enviando,
      deletando
    } = useAnexos(pacienteId);

const { isLoadingPDF, dadosPDF } = useRelatorioPDF(pacienteId);

      const anexosFiltrados = dataSelecionada
    ? anexos.filter((r) => r.data === dataSelecionada)
    : anexos;


    
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
        <h1
          className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}
        >
          {isLoadingPDF || !dadosPDF
              ? "Carregando nome paciente..."
              : dadosPDF?.paciente.nome}
              
        </h1>

        {loading && (
          <p className="text-center text-gray-500 mt-20">
            Carregando anexos...
          </p>
        )}

        {!loading && anexosFiltrados.length === 0 && (
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

        <AnexoModal open={open} onOpenChange={setOpen}>
          <AnexoForm onSubmit={enviarAnexo} />
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
        onConfirm={() =>
          reportToDelete && deletarAnexo(reportToDelete.objectName)
        }
        disabled={enviando || deletando}
      />

      <AnexoViewModal
        isOpen={!!reportToView}
        onClose={() => setReportToView(null)}
        titulo={reportToView?.titulo ?? ""}
        data={reportToView}
        descricao={reportToView?.descricao ?? ""}
        disabled={enviando}
      />
    </div>
  );
}
