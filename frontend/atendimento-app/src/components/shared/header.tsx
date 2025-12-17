"use client";

import Image from "next/image";
import Link from "next/link";
import { User } from "lucide-react";
import { useEffect, useState } from "react";

import { MeusDadosModal } from "../modals/meusDadosModal";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

import { Profissional } from "@/types/Profissional";

import dados from "../../../data/verificacao.json";
import { getProfissional } from "@/api/dadosProfissional";

export default function Header() {
  const [profissional, setProfissional] = useState<Profissional | null>(null);

  useEffect(() => {
    async function carregarProfissional() {
      try {
        const data = await getProfissional(dados.idProfissional);
        setProfissional(data);
      } catch (error) {
        console.error("Erro ao buscar profissional", error);
      }
    }

    carregarProfissional();
  }, []);

  return (
    <header className="w-full border-b bg-[#F8FAFD] shadow-sm">
      <div className="flex items-center justify-between px-6 py-3 w-full">
        <Link href="/home" className="flex items-center gap-3">
          <Image
            src="/APAE-logo.svg"
            alt="Logo APAE"
            width={40}
            height={40}
            priority
          />
          <span className="font-bold text-lg text-[#344054]">APAE</span>
        </Link>

        {profissional && (
          <MeusDadosModal
            trigger={
              <Avatar className="w-10 h-10 rounded-full bg-[#F2F4F7] flex items-center justify-center hover:cursor-pointer">
                <AvatarImage src="" />
                <AvatarFallback>
                  <User className="w-5 h-5 text-[#344054]" />
                </AvatarFallback>
              </Avatar>
            }
            userData={{
              nome: profissional.nomeCompleto,
              crm: profissional.crm,
              celular: profissional.contato,
              email: profissional.email,
            }}
          />
        )}
      </div>
    </header>
  );
}
