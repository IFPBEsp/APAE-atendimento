"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { Eye, EyeOff, Lock, Save } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import { isAxiosError } from "axios";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Label } from "@/components/ui/label";
import { me, redefinirSenha } from "@/services/authService";

const nunitoFont = Nunito({
  weight: "700",
  subsets: ["latin"],
});

const baloo2Font = Baloo_2({
  weight: "500",
  subsets: ["latin"],
});

export default function RedefinirSenhaPage() {
  const router = useRouter();
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmacaoSenha, setConfirmacaoSenha] = useState("");
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [mostrarConfirmacao, setMostrarConfirmacao] = useState(false);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isCheckingSession, setIsCheckingSession] = useState(true);

  useEffect(() => {
    let active = true;

    async function checkSession() {
      try {
        await me();
      } catch {
        if (active) router.replace("/login");
      } finally {
        if (active) setIsCheckingSession(false);
      }
    }

    checkSession();

    return () => {
      active = false;
    };
  }, [router]);

  const senhaValida = useMemo(() => novaSenha.length >= 8, [novaSenha]);
  const senhasIguais = useMemo(
    () => novaSenha === confirmacaoSenha,
    [novaSenha, confirmacaoSenha],
  );

  const canSubmit =
    senhaValida &&
    senhasIguais &&
    confirmacaoSenha.length > 0 &&
    !isLoading &&
    !isCheckingSession;

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");

    if (!senhaValida) {
      setError("A senha deve ter no mínimo 8 caracteres.");
      return;
    }

    if (!senhasIguais) {
      setError("As senhas informadas não são iguais.");
      return;
    }

    setIsLoading(true);

    try {
      await redefinirSenha(novaSenha);
      router.replace("/home");
    } catch (err) {
      if (isAxiosError(err)) {
        const message = (err.response?.data as { message?: string } | undefined)
          ?.message;
        setError(message || "Não foi possível redefinir a senha.");
      } else {
        setError("Falha ao processar a redefinição. Tente novamente.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (isCheckingSession) {
    return (
      <main className="min-h-screen bg-[#F8FAFD] flex items-center justify-center">
        <p className="text-sm text-[#475467]">Validando sessão...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-[#F8FAFD] flex flex-col">
      <header className="w-full border-b bg-[#F8FAFD] shadow-sm">
        <div className="flex items-center gap-3 px-6 py-3">
          <Image
            src="/APAE-logo.svg"
            alt="Logo APAE"
            width={40}
            height={40}
            priority
          />
          <span className="font-bold text-lg text-[#344054]">APAE</span>
        </div>
      </header>

      <section className="flex flex-1 items-center justify-center px-4 py-10">
        <Card className="w-full max-w-[420px] rounded-[28px] border border-[#EAECF0] bg-white shadow-sm">
          <CardContent className="p-7 sm:p-8">
            <div className="mb-7 text-center">
              <h1
                className={`text-2xl text-[#344054] ${baloo2Font.className}`}
              >
                Criar nova senha
              </h1>
              <p className="mt-2 text-sm font-medium text-[#667085]">
                Defina sua senha para continuar acessando o Atendimento.
              </p>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="flex flex-col gap-5">
                <div className="grid gap-2">
                  <Label
                    htmlFor="novaSenha"
                    className={`font-bold text-gray-700 ${nunitoFont.className}`}
                  >
                    Nova senha <span className="text-[#F28C38]">*</span>
                  </Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                    <InputGroupInput
                      id="novaSenha"
                      className={nunitoFont.className}
                      type={mostrarSenha ? "text" : "password"}
                      value={novaSenha}
                      placeholder="********"
                      minLength={8}
                      autoComplete="new-password"
                      onChange={(event) => {
                        setNovaSenha(event.target.value);
                        setError("");
                      }}
                      aria-invalid={error !== ""}
                      required
                    />
                    <InputGroupAddon>
                      <Lock />
                    </InputGroupAddon>
                    <button
                      type="button"
                      className="mr-3 text-[#667085] hover:text-[#344054]"
                      onClick={() => setMostrarSenha((value) => !value)}
                      aria-label={mostrarSenha ? "Ocultar senha" : "Mostrar senha"}
                    >
                      {mostrarSenha ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </InputGroup>
                </div>

                <div className="grid gap-2">
                  <Label
                    htmlFor="confirmacaoSenha"
                    className={`font-bold text-gray-700 ${nunitoFont.className}`}
                  >
                    Confirmar senha <span className="text-[#F28C38]">*</span>
                  </Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                    <InputGroupInput
                      id="confirmacaoSenha"
                      className={nunitoFont.className}
                      type={mostrarConfirmacao ? "text" : "password"}
                      value={confirmacaoSenha}
                      placeholder="********"
                      minLength={8}
                      autoComplete="new-password"
                      onChange={(event) => {
                        setConfirmacaoSenha(event.target.value);
                        setError("");
                      }}
                      aria-invalid={error !== ""}
                      required
                    />
                    <InputGroupAddon>
                      <Lock />
                    </InputGroupAddon>
                    <button
                      type="button"
                      className="mr-3 text-[#667085] hover:text-[#344054]"
                      onClick={() => setMostrarConfirmacao((value) => !value)}
                      aria-label={
                        mostrarConfirmacao ? "Ocultar senha" : "Mostrar senha"
                      }
                    >
                      {mostrarConfirmacao ? (
                        <EyeOff size={18} />
                      ) : (
                        <Eye size={18} />
                      )}
                    </button>
                  </InputGroup>
                </div>
              </div>

              <div className="h-7 mt-[8px] mb-[18px]">
                {error ? <p className="text-sm text-red-600">{error}</p> : null}
              </div>

              <Button
                type="submit"
                disabled={!canSubmit}
                className={`active:scale-[0.98] w-full h-[46px] rounded-full bg-[#165BAA] disabled:bg-[#B0C6DE] hover:bg-[#13447D] text-[18px] hover:cursor-pointer ${baloo2Font.className}`}
                style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.18)" }}
              >
                <Save />
                {isLoading ? "Salvando..." : "Salvar senha"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}
