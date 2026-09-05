"use client";

import Image from "next/image";
import Link from "next/link";
import { User } from "lucide-react";

import { MeusDadosModal } from "../../features/profissional/components/meusDadosModal";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

export default function Header() {
  return (
    <header className="w-full bg-[#124587] shadow-md border-b-0 relative">
      <div className="flex items-center justify-end px-6 py-3 w-full min-h-[64px]">
        
        <Link 
          href="/home" 
          className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 flex items-center gap-3"
        >
          <Image
            src="/APAE-logo.svg"
            alt="Logo APAE"
            width={40}
            height={40}
            priority
            className="rounded-full bg-white/10"
          />
          <span className="font-semibold text-xl text-white tracking-wide">
            Sistema APAE
          </span>
        </Link>

        <MeusDadosModal
          trigger={
            <Avatar className="w-10 h-10 rounded-full bg-white flex items-center justify-center hover:cursor-pointer transition-all hover:bg-gray-100 shadow-sm border border-transparent hover:border-gray-300">
              <AvatarImage src="" />
              <AvatarFallback>
                <User className="w-5 h-5 text-[#124587]" />
              </AvatarFallback>
            </Avatar>
          }
        />
      </div>
    </header>
  );
}