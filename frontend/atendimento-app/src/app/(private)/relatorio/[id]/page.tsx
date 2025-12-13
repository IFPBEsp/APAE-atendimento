"use client";

import { useRouter } from "next/navigation";
import { ArrowLeft, ClipboardPlus } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import RelatorioForm, { RelatorioFormData } from "@/components/forms/relatorioForm";
import { RelatorioModal } from "@/components/modals/novoRelatorioModal";
import RelatorioCard from "@/components/cards/relatorioCard";
import { RelatorioViewModal, RelatorioDeleteModal } from "@/components/modals/relatorioModal";
import { Relatorio } from "@/types/Relatorio";


const nunitoFont = Nunito({ weight: "700" });

export default function RelatorioPage() {
  const router = useRouter();

  const nomePaciente = "Fulano de Tal de Lorem Ipsum Santos";

  const [relatorios, setRelatorios] = useState<Relatorio[]>(
    Array.from({ length: 1 }).map((_, i) => ({
      id: i,
      titulo: "Lorem Ipsum", data: "2025-11-24",
      descricao: "Nullam varius tempor massa et iaculis. Praesent sodales orci ut ultrices tempor. Quisque ac mauris gravida, dictum ipsum sit amet, bibendum turpis. Mauris dictum orci quis quam tincidunt imperdiet. Cras auctor aliquam tortor a luctus. Morbi tincidunt lacus vulputate risus dignissim porttitor.",
      fileName: "nome_do_arquivo_lorem_ipsum_da_silva.jpg",
      imageUrl: i === 0 ? "https://placehold.co/426x552/png" : undefined,
    })
    ));

  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Relatorio | null>(null);
  const [reportToView, setReportToView] = useState<Relatorio | null>(null);

  const handleDelete = () => {
    if (reportToDelete) {
      setRelatorios((prev) => prev.filter((r) => r.id !== reportToDelete.id));
      setReportToDelete(null);
    }
  };

  const relatoriosFiltrados =
    dataSelecionada
      ? relatorios.filter((r) => r.data === dataSelecionada)
      : relatorios;

  function handleCreateRelatorio(data: RelatorioFormData) {
    console.log("Novo relatório recebido:", data);
    const novoId = relatorios.length + 1;

    let preview = undefined;
    let fileName = "";

    if (data.arquivo && data.arquivo[0]) {
      preview = URL.createObjectURL(data.arquivo[0]);
      fileName = data.arquivo[0].name;
    }

    const novoRelatorio: Relatorio = {
      id: novoId,
      titulo: data.titulo || "Sem título",
      descricao: data.descricao,
      data: data.data,
      fileName: fileName,
      imageUrl: preview, 
    };

    setRelatorios((prev) => [novoRelatorio, ...prev]);

    // Mudar futuramente:
    // await api.post("/rota", data);
    // depois atualiza a lista

    setOpen(false);
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
                fileName={item.fileName}
                imageUrl={item.imageUrl}
                onView={() => setReportToView(item)}
                onDelete={() => setReportToDelete(item)}
              />
            ))}
          </div>
        )}

        <RelatorioModal open={open} onOpenChange={setOpen}>
          <RelatorioForm onSubmit={handleCreateRelatorio} />
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
        onConfirm={handleDelete}
      />

      <RelatorioViewModal
        isOpen={!!reportToView}
        onClose={() => setReportToView(null)}
        titulo={reportToView?.titulo ?? ""}
        data={reportToView}
        descricao={reportToView?.descricao ?? ""}
      />
    </div>
  );
}
