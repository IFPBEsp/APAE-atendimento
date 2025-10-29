'use client';

import Header from "@/components/shared/header";

export default function PacientesPage() {
    return(
        <>
        <Header/>
        <main className="bg-[#F8FAFD] p-6 flex flex-col items-center text-center">
            {/* 🔹 Cabeçalho da página */}
            <section className="mt-6">
                <h1 className="font-semibold text-[#344054] text-xl">
                    Olá Dr. Fulano!
                </h1>
                <h1 className="font-semibold text-[#344054] text-lg">
                    Esses são seus pacientes
                </h1>
            </section>

            {/* 🔹 Barra de busca e filtros */}
            <section className="flex flex-col sm:flex-row justify-between gap-3">
           
            </section>

            {/* 🔹 Lista de pacientes */}
            <section>
                
            </section>
        </main>
        </>
    );
}