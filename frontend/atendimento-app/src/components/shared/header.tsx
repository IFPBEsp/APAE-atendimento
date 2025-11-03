"use client"

import Image from "next/image";
import Link from "next/link";
import { useState } from "react";
import { Button } from "../ui/button";
import { LogOut } from "lucide-react";
import { Menu } from "lucide-react";

export default function Header() {
  const [open, setOpen] = useState(false);

  return (
    <>
      <header className="w-full border-b bg-[#F8FAFD] shadow-sm">
        <div className="flex items-center justify-between px-6 py-3 w-full">

          <Link href="/" className="flex items-center gap-3">
            <Image
              src="/APAE-logo.svg"
              alt="Logo APAE"
              width={40}
              height={40}
              priority
            />
            <span className="font-bold text-lg text-[#344054]">APAE</span>
          </Link>

          {/* Menu mobile*/}
          <Button
            variant="ghost"
            className="h-10 w-10 p-2 md:hidden"
            onClick={() => setOpen(!open)}
          >
            <Menu className="w-6 h-6 text-[#344054]"/>
          </Button>

          {/*Menu web*/}
          <div className="hidden md:flex items-center w-full">
            <div className="flex-1 flex justify-center gap-10 text-sm font-medium">
              <Link href="/" className="text-[#344054] hover:underline">
                Pacientes
              </Link>

              <Link href="#" className="text-[#344054] hover:underline">
                Meus dados
              </Link>
            </div>

            <button className="text-red-500 text-sm font-medium flex items-center gap-2 cursor-pointer">
              <LogOut className="w-4 h-4" /> Sair
            </button>
          </div>

        </div>
      </header>

      {/* Menu dropdown mobile*/}
      {open && (
        <div className="w-full bg-white shadow-md py-4 flex flex-col items-center gap-4 border-b md:hidden">

          <Link href="/" className="text-[#344054] text-sm hover:underline">
            Pacientes
          </Link>

          <Link href="#" className="text-[#344054] text-sm hover:underline">
            Meus dados
          </Link>

          <button className="text-red-500 text-sm font-medium flex items-center gap-2 hover:underline">
            <LogOut className="w-4 h-4" /> Sair
          </button>
        </div>
      )}
    </>
  );
}