"use client";

import Image from "next/image";
import Link from "next/link";
import { User } from "lucide-react";
import { MeusDadosModal } from "../modals/meusDados";

export default function Header() {

  return (
    <>
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

          <MeusDadosModal
            trigger={
              <button className="text-[#344054] hover:cursor-pointer font-medium">
                <User/>
              </button>
            }
          />

        </div>
      </header>

    </>
  );
}
