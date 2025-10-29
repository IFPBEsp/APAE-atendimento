import Image from "next/image";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem } from "../ui/dropdown-menu";
import { Button } from "../ui/button";
import { LogOut } from "lucide-react";
import Link from "next/link";

export default function Header() {
  return (
    <header className="w-full border-b bg-[#F8FAFD] shadow-sm">
      <div className="flex items-center justify-between px-6 py-3 w-full">

        <div className="flex items-center gap-3">
        <Link href="/pacientes" className="flex items-center gap-3">
          <Image
            src="/APAE-logo.svg"
            alt="Logo APAE"
            width={40}
            height={40}
            priority
          />
          <span className="font-bold text-lg text-[#344054]">APAE</span>
        </Link>
        </div>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-10 w-10 p-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-6 w-6 text-[#344054]"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent
            side="bottom"
            align="end"
            className="w-70 bg-white border-none shadow-lg rounded-xl py-3 flex flex-col items-center gap-3"
          >
          <Link href="/pacientes">
            <DropdownMenuItem
              className="text-[#344054] text-sm hover:bg-transparent focus:bg-transparent"
            >
              Pacientes
            </DropdownMenuItem>
          </Link>

          <Link href="/informacoes-pessoais">
            <DropdownMenuItem
              className="text-[#344054] text-sm hover:bg-transparent focus:bg-transparent"
            >
              Informações pessoais
            </DropdownMenuItem>
          </Link>

            <DropdownMenuItem
              className="text-red-500 text-sm font-medium hover:bg-transparent focus:bg-transparent flex items-center gap-2"
            >
              <LogOut className="w-4 h-4" />
              Sair
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}