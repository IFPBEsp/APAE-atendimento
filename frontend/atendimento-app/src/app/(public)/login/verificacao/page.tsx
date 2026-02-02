"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Check, Loader2 } from "lucide-react";

import { confirmMagicLink } from "@/services/authService";
import { api } from "@/services/axios";

export default function VerificacaoPage() {
  const [status, setStatus] = useState<"loading" | "success" | "error">(
    "loading",
  );
  const router = useRouter();

  useEffect(() => {
    const run = async () => {
      try {
        const token = await confirmMagicLink();

        if (!token) {
          setStatus("loading");
          return;
        }

        await api.get("/auth/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        document.cookie = `token=${token}; path=/; samesite=lax`;

        setStatus("success");

        setTimeout(() => {
          router.replace("/home");
        }, 1000);
      } catch {
        setStatus("error");
      }
    };

    run();
  }, [router]);

  return (
    <div className="relative min-h-screen w-full flex items-center justify-center">
      <Image
        src="/background-login-apae.svg"
        alt="Background"
        fill
        className="object-cover"
      />

      <div className="absolute inset-0 bg-[#0D4F97]/80" />

      <div className="relative z-10 w-[340px] bg-white p-5 text-center shadow-md min-h-[480px] flex flex-col justify-center rounded-[30px] sm:w-[410px]">
        <h1 className="text-[36px] font-semibold text-[#344054]">
          Verificação
        </h1>

        {status === "loading" && (
          <>
            <p className="text-base text-[#344054] mt-6">
              Verifique o link na sua caixa de entrada do email para validar o
              seu acesso.
            </p>

            <div className="flex justify-center mt-10">
              <Loader2 className="animate-spin text-[#165BAA]" size={48} />
            </div>
          </>
        )}

        {status === "success" && (
          <>
            <p className="text-green-600 text-base mt-6">
              Acesso confirmado com sucesso.
            </p>

            <div className="flex justify-center mt-10">
              <Check size={48} className="text-green-600" />
            </div>
          </>
        )}

        {status === "error" && (
          <>
            <p className="text-red-500 text-base mt-6">
              Link inválido ou expirado.
            </p>

            <Link
              href="/login"
              className="text-[#0D4F97] underline font-medium mt-6 block"
            >
              Voltar para o login
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
