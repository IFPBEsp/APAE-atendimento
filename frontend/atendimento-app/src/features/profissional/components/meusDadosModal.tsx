"use client";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogClose,
} from "../../../components/ui/dialog";
import { Button } from "../../../components/ui/button";
import { LogOut, X } from "lucide-react";

import { useProfissional } from "../hooks/useProfissional";

interface UserData {
  nome: string;
  crm: string;
  celular: string;
  email: string;
}

interface MeusDadosModalProps {
  trigger: React.ReactNode;
}

function logout() {
  document.cookie = "verified=; path=/; max-age=0";
  window.location.href = "/login";
}

export function MeusDadosModal({ trigger }: MeusDadosModalProps) {
  const {data: profissional, isLoading } = useProfissional();

  if (isLoading || !profissional) return null;

  return (
    <Dialog>
      <DialogTrigger asChild>{trigger}</DialogTrigger>

      <DialogContent className="max-w-[90%] sm:max-w-[500px] p-0 border-none rounded-[30px] bg-white font-sans">
        <div className="absolute right-5 top-5 z-10">
          <DialogClose className="rounded-full p-1 hover:bg-gray-100 transition-colors outline-none cursor-pointer">
            <X className="h-6 w-6" />
          </DialogClose>
        </div>

        <div className="px-8 py-8 flex flex-col items-center">
          <DialogHeader className="mb-4 w-full">
            <DialogTitle className="text-2xl font-bold text-[#344054] text-center">
              Meus dados
            </DialogTitle>
          </DialogHeader>

          <div className="text-center mb-8 px-4">
            <h2 className="font-bold text-lg text-[#344054] leading-snug">
              {profissional.nomeCompleto}
            </h2>
          </div>

          <div className="w-full grid grid-cols-2 gap-y-5 gap-x-4 text-left mb-8">
            <div>
              <p className="font-bold text-sm text-[#344054]">CRM</p>
              <p className="text-[#475467] text-sm mt-1">{profissional.crm}</p>
            </div>

            <div>
              <p className="font-bold text-sm text-[#344054]">Contato</p>
              <p className="text-[#475467] text-sm mt-1">{profissional.contato}</p>
            </div>

            <div>
              <p className="font-bold text-sm text-[#344054]">Email</p>
              <p className="text-[#475467] text-sm mt-1 break-words">
                {profissional.email}
              </p>
            </div>
          </div>

          <Button
            onClick={logout}
            className="w-full bg-[#F45D6C] hover:bg-[#D44D54] text-white font-medium rounded-full h-12 text-base shadow-none cursor-pointer"
          >
            <LogOut className="mr-2 h-5 w-5 rotate-180" />
            Sair
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
