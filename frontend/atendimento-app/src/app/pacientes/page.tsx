'use client';

import Header from "@/components/shared/header";
import { Input } from "@/components/ui/input";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select";
import { Search } from "lucide-react";

export default function PacientesPage() {
    return(
        <>
        <Header/>
        <main className="bg-[#F8FAFD] p-6 flex flex-col items-center text-center">
            
            <section className="mt-6">
                <h1 className="font-semibold text-[#344054] text-xl">
                    Olá Dr. Fulano!
                </h1>
                <h1 className="font-semibold text-[#344054] text-lg">
                    Esses são seus pacientes
                </h1>
            </section>

           
            <section className="flex flex-row justify-between gap-2 mt-6 w-full max-w-md">
                
                <div className="relative flex-1">
                    <Input
                    type="text"
                    placeholder="Pesquisar por paciente"
                    className="bg-white border border-[#3B82F6] pl-9 pr-3 py-2 rounded-full focus:ring-2 focus:ring-[#3B82F6] text-sm"
                    />
                    <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400"/>
                </div>

                
                <Select>
                    <SelectTrigger className="bg-white border border-[#3B82F6] rounded-full focus:ring-2 focus:ring-[#3B82F6] w-[130px] text-gray-600 text-sm">
                        <SelectValue placeholder="Filtrar por..."/>
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="todos">Todos</SelectItem>
                        <SelectItem value="nome">Nome</SelectItem>
                        <SelectItem value="cpf">CPF</SelectItem>
                        <SelectItem value="cidade">Cidade</SelectItem>
                    </SelectContent>
                </Select>

                
            </section>

            {/* 🔹 Lista de pacientes */}
            <section>

            </section>
        </main>
        </>
    );
}