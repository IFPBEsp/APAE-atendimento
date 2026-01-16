"use client";

import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, ClipboardPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import RelatorioForm from "@/features/relatorio/components/relatorioForm";
import { RelatorioModal } from "@/features/relatorio/components/novoRelatorioModal";
import RelatorioCard from "@/features/relatorio/components/relatorioCard";
import {
  RelatorioViewModal,
  RelatorioDeleteModal,
} from "@/features/relatorio/components/relatorioModal";
import { useRelatorios } from "@/features/relatorio/hooks/useRelatorio";
import { useRelatorioPDF } from "../hooks/useRelatorioPDF";
const nunitoFont = Nunito({ weight: "700" });

export default function RelatorioPage() {
  const router = useRouter();
  const params = useParams();
  const pacienteId = typeof params.id === "string" ? params.id : "";

  const {
    relatorios,
    loading,
    dataSelecionada,
    open,
    reportToDelete,
    reportToView,
    setDataSelecionada,
    setReportToView,
    setReportToDelete,
    setOpen,
    enviarRelatorio,
    deletarRelatorio,
    baixarRelatorio,
    enviando,
    deletando,
  } = useRelatorios(pacienteId);

  const { isLoadingPDF, dadosPDF } = useRelatorioPDF(pacienteId);

  const relatoriosFiltrados = dataSelecionada
    ? relatorios.filter((r) => r.data === dataSelecionada)
    : relatorios;

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
        <h1
          className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}
        >
          {isLoadingPDF || !dadosPDF
            ? "Carregando nome paciente..."
            : dadosPDF?.paciente.nome}
        </h1>

        {loading && (
          <p className="text-center text-gray-500 mt-20">
            Carregando relatórios...
          </p>
        )}

        {!loading && relatoriosFiltrados.length === 0 && (
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
          <RelatorioForm
            onSubmit={enviarRelatorio}
            dadosPdf={dadosPDF}
            carregandoPdf={isLoadingPDF}
          />
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
        onConfirm={() =>
          reportToDelete && deletarRelatorio(reportToDelete.objectName)
        }
        disabled={enviando || deletando}
      />

      <RelatorioViewModal
        isOpen={!!reportToView}
        onClose={() => setReportToView(null)}
        titulo={reportToView?.titulo ?? ""}
        data={reportToView}
        descricao={reportToView?.descricao ?? ""}
        onUpdate={() =>
          reportToView && baixarRelatorio(reportToView.objectName)
        }
        disabled={enviando}
      />
    </div>
  );
}
