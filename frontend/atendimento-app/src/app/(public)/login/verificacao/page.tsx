"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
} from "@/components/ui/input-otp";
import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Check } from "lucide-react";
import dados from "../../../../../data/verificacao.json";

export default function VerificacaoPage() {
  const [value, setValue] = useState("");
  const [status, setStatus] = useState<"idle" | "error" | "success">("idle");
  const router = useRouter();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (value !== dados.codigo) {
      setStatus("error");
      return;
    } else setStatus("success");
    setTimeout(() => {
      document.cookie = "verified=true; path=/;";
      router.push("/home");
    }, 700);
  };

  const borderColor =
    status === "error"
      ? "border-red-500 focus:ring-red-500"
      : status === "success"
      ? "border-green-500 focus:ring-green-500"
      : "border-[#0D4F97] focus:ring-blue-600";

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

        <p className="text-base text-[#344054] mt-3">
          Verifique o código no seu e-mail para efetuar o login, ou{" "}
          <Link href="/login" className="text-[#0D4F97] underline font-medium">
            edite seu e-mail
          </Link>
          .
        </p>

        <div className="mt-8 flex justify-center">
          <InputOTP
            maxLength={4}
            value={value}
            onChange={(val) => {
              if (/^\d*$/.test(val)) setValue(val);
            }}
          >
            <InputOTPGroup className="flex gap-3">
              {[0, 1, 2, 3].map((i) => (
                <InputOTPSlot
                  key={i}
                  index={i}
                  className={`w-12 h-15 border-1 rounded-md text-center text-xl font-semibold text-[#344054] focus:outline-none transition-all ${borderColor}`}
                />
              ))}
            </InputOTPGroup>
          </InputOTP>
        </div>

        <div className="h-6 mt-3">
          {status === "error" && (
            <p className="text-red-500 text-sm">
              Código inválido. Verifique e tente novamente.
            </p>
          )}
          {status === "success" && (
            <p className="text-green-600 text-sm">
              Código validado com sucesso.
            </p>
          )}
        </div>

        <Button
          onClick={handleSubmit}
          disabled={value.length < 4}
          className={`active:scale-[0.98] text-[18px] w-full h-[46px] rounded-full bg-[#165BAA] hover:bg-[#13447D] hover:cursor-pointer text-white mt-8 disabled:bg-[#B0C6DE] disabled:cursor-not-allowed`}
          style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
        >
          {status === "success" ? <Check size={22} /> : "Verificar"}
        </Button>

        <button
          onClick={() => console.log("Reenviar código")}
          className="text-base text-[#344054] mt-6 underline cursor-pointer"
        >
          Enviar novamente
        </button>
      </div>
    </div>
  );
}
