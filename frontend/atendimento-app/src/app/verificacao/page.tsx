"use client";

import { Button } from "@/components/ui/button";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import Image from "next/image";
import Link from "next/link";

export default function VerificacaoPage(){
    return (
        <div className="relative min-h-screen w-full flex items-center justify-center">
            <Image
                src="/background-login-apae.svg"
                alt="Background"
                fill
                className="object-cover"
            />

            <div className="absolute inset-0 bg-[#0D4F97]/80" />

            <div className="relative z-10 w-[88%] max-w-xs bg-white rounded-2xl p-6 text-center shadow-md">
                <h1 className="text-[36px] font-semibold text-[#344054]">Verificação</h1>

                <p className="text-sm text-[#344054] mt-3">
                    Verifique o código no seu e-mail para efetuar o login, ou{" "}
                    <Link href="/" className="text-blue-600 underline font-medium">
                        edite seu e-mail
                    </Link>
                    .
                </p>

                <div className="mt-6 flex justify-center">
                    <InputOTP maxLength={4}>
                      <InputOTPGroup className="flex gap-3">
                        {[0, 1, 2, 3].map((i) => (
                            <InputOTPSlot
                              key={i}
                              index={i}
                              className="w-12 h-15 border-1 border-blue-500 rounded-md text-center text-xl font-semibold text-[#344054] focus:outline-none focus:ring-2 focus:ring-blue-600"
                            />
                        ))}
                      </InputOTPGroup>
                    </InputOTP>
                </div>

                <Button className="w-full rounded-full bg-blue-600 hover:bg-blue-700 text-white mt-6">
                    Verificar
                </Button>

                <button
                    onClick={() => console.log("Reenviar código")}
                    className="text-sm text-[#344054] mt-5 underline"
                >
                    Enviar novamente
                </button>
            </div>
        </div>
    );
}