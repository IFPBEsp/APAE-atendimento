"use client";

import { useEffect, useState } from "react";
import { Mail, Lock } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import { Card, CardContent } from "@/components/ui/card";
import { useRouter } from "next/navigation";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { isAxiosError } from "axios";
import { login, me } from "@/services/authService";

const nunitoFont = Nunito({
  weight: "700",
});

const baloo2Font = Baloo_2({
  weight: "500",
});

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isCheckingSession, setIsCheckingSession] = useState(true);

  const router = useRouter();

  useEffect(() => {
    let active = true;

    async function checkSession() {
      try {
        await me();
        if (active) router.replace("/home");
      } catch {
      } finally {
        if (active) setIsCheckingSession(false);
      }
    }

    checkSession();

    return () => {
      active = false;
    };
  }, [router]);

  const isValidEmail = (value: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  const canSubmit =
    isValidEmail(email) &&
    password.length >= 8 &&
    !isLoading &&
    !isCheckingSession;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      await login({ email, password });
      router.replace("/home");
    } catch (err) {
      if (isAxiosError(err)) {
        const message = (err.response?.data as { message?: string } | undefined)
          ?.message;
        setError(message || "Credenciais inválidas.");
      } else {
        setError("Falha ao processar login. Tente novamente.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (isCheckingSession) {
    return (
      <div className="h-screen w-screen bg-[url('/background-login-apae.svg')] relative bg-no-repeat bg-cover bg-center flex items-center justify-center">
        <div className="absolute flex items-center justify-center inset-0 bg-[#0D4F97]/80">
          <p className="text-white font-semibold">Validando sessão...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen w-screen bg-[url('/background-login-apae.svg')] relative bg-no-repeat bg-cover bg-center flex items-center justify-center">
      <div className="absolute flex items-center justify-center inset-0 bg-[#0D4F97]/80">
        <div className="absolute z-50 mt-[-32rem] w-[128px] h-[128px] bg-white rounded-full flex items-center justify-center mx-auto mb-[0px]">
          <img
            src="/logo-apae.svg"
            alt="APAE Logo"
            className="w-[90px] h-[140px] mt-[3rem]"
          />
        </div>

        <Card className="w-[340px] h-[520px] rounded-[30px] sm:w-[410px]">
          <CardContent className="flex flex-col justify-end h-full pb-6">
            <form onSubmit={handleSubmit}>
              <div className="flex flex-col gap-6">
                <div className="grid gap-2">
                  <Label
                    htmlFor="email"
                    className={`font-bold text-gray-700 ${nunitoFont.className}`}
                  >
                    Email do profissional{" "}
                    <span className="text-[#F28C38]">*</span>
                  </Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                    <InputGroupInput
                      id="email"
                      className={nunitoFont.className}
                      type="email"
                      value={email}
                      placeholder="seuemail@dominio.com"
                      onChange={(e) => {
                        setEmail(e.target.value);
                        setError("");
                      }}
                      aria-invalid={error !== ""}
                      required
                    />
                    <InputGroupAddon>
                      <Mail />
                    </InputGroupAddon>
                  </InputGroup>
                </div>

                <div className="grid gap-2">
                  <Label
                    htmlFor="password"
                    className={`font-bold text-gray-700 ${nunitoFont.className}`}
                  >
                    Senha <span className="text-[#F28C38]">*</span>
                  </Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                    <InputGroupInput
                      id="password"
                      className={nunitoFont.className}
                      type="password"
                      value={password}
                      placeholder="********"
                      minLength={8}
                      onChange={(e) => {
                        setPassword(e.target.value);
                        setError("");
                      }}
                      aria-invalid={error !== ""}
                      required
                    />
                    <InputGroupAddon>
                      <Lock />
                    </InputGroupAddon>
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
                style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
              >
                {isLoading ? "Entrando..." : "Entrar"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
