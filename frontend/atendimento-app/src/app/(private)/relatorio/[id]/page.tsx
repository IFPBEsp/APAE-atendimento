"use client";

import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, Plus, ClipboardPlus  } from "lucide-react";
import { Nunito } from "next/font/google";
import Header from "@/components/shared/header";
import { useState } from "react";
import { Input } from "@/components/ui/input";

interface Relatorio {
  id: number;
  data: string;
  arquivo?: File;
}

const nunitoFont = Nunito({ weight: "700" });

export default function RelatorioPage() {
  const router = useRouter();
  const { id } = useParams();

  const relatorios: Relatorio[] = [];

  const nomePaciente = "Fulano de Tal de Lorem Ipsum Santos";
  const [open, setOpen] = useState(false);

  return (
    <div className="min-h-screen w-full bg-[#F8FAFD]">
      <Header />

      <section className="px-5 pt-4 mx-auto w-full">
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => router.back()}
            className="h-[38px] px-4 rounded-full flex items-center gap-2 bg-[#EDF2FB] text-sm text-gray-700"
          >
            <ArrowLeft size={18} />
            Voltar
          </button>


          <Input
            type="date"
            className="bg-white border border-[#3B82F6] rounded-full w-[160px] text-gray-600 text-sm focus-visible:ring-0 focus-visible:border-[#3B82F6] outline-none"
          />
        </div>
      </section>

      <section className="bg-white rounded-t-3xl p-6 min-h-screen mx-auto flex flex-col gap-4">
        <h1 className={`text-xl text-[#344054] font-bold ${nunitoFont.className}`}>
          {nomePaciente}
        </h1>
      </section>

      <button
        onClick={() => setOpen(true)}
        className="
              fixed bottom-6 right-6 w-14 h-14 rounded-full bg-[#165BAA]
              flex items-center justify-center shadow-[4px_4px_12px_rgba(0,0,0,0.25)]
              active:scale-95 md:hidden"
      >
        <ClipboardPlus  size={28} className="text-white" />
      </button>
    </div>
  );
}
