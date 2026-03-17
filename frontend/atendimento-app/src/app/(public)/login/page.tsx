"use client";

import { useState } from "react";

import { Mail } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import { Card, CardContent } from "@/components/ui/card";
import { useRouter } from "next/navigation";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { sendMagicLink, loginWithGoogle } from "@/services/authService";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { api } from "@/services/axios";
import { isAxiosError } from 'axios';
import { toast } from "sonner";

const nunitoFont = Nunito({
  weight: "700",
});

const baloo2Font = Baloo_2({
  weight: "500",
});

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const router = useRouter();
  const isValidEmail = (value: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      await api.post("/auth/send-link", { email });

      await sendMagicLink(email);

      toast.success("Link de acesso enviado para o seu e-mail!");
      router.push("/login/verificacao");
    } catch(err) {
      if (!isAxiosError(err))
        toast.error("Falha ao comunicar com o provedor de e-mail.");
    }
  };

  return (
    <div className="h-screen w-screen bg-[url('/background-login-apae.svg')] relative bg-no-repeat bg-cover bg-center flex items-center justify-center">
      <div className="absolute flex items-center justify-center inset-0 bg-[#0D4F97]/80">
        <div className="absolute z-50 mt-[-28rem] w-[128px] h-[128px] bg-white rounded-full flex items-center justify-center mx-auto mb-[0px]">
          <img
            src="/logo-apae.svg"
            alt="APAE Logo"
            className="w-[90px] h-[140px] mt-[3rem]"
          />
        </div>
        <Card className="w-[340px] h-[455px] rounded-[30px] sm:w-[410px]">
          <CardContent className="flex flex-col justify-end h-full pb-6 ">
            <form onSubmit={handleSubmit}>
              <div className=" flex flex-col gap-6 ">
                <div className="grid gap-2 ">
                  <Label
                    htmlFor="email"
                    className={`font-bold text-gray-700 ${nunitoFont.className}`}
                  >
                    Email do profissional{" "}
                    <span className="text-[#F28C38]">*</span>
                  </Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                    <InputGroupInput
                      className={` ${nunitoFont.className}`}
                      type="email"
                      value={email}
                      placeholder="seuemail@hotmail.com"
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
              </div>

              <div className="h-6 mt-[6px] mb-[19px]"></div>

              <Button
                type="submit"
                disabled={!isValidEmail(email)}
                className={`active:scale-[0.98] w-full h-[46px] rounded-full bg-[#165BAA] disabled:bg-[#B0C6DE] hover:bg-[#13447D] text-[18px] hover:cursor-pointer ${baloo2Font.className}`}
                style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
              >
                Entrar
              </Button>

              <div className="flex items-center my-[16px]">
                <hr className="flex-grow border-[#B2D7EC]" />
                <span className="mx-2 text-gray-500 text-sm">ou</span>
                <hr className="flex-grow border-[#B2D7EC]" />
              </div>

              <Button
                onClick={async () => {
                  try {
                    const token = await loginWithGoogle();

                    const { data } = await api.get("/auth/me");

                    document.cookie = `token=${token}; path=/; samesite=lax`;
                    toast.success(`Bem-vindo, ${data.nome || 'Profissional'}!`);
                    router.push("/home");
                  } catch (err) {
                    if (!isAxiosError(err)) {
                      toast.error("A autenticação com o Google foi cancelada ou falhou.");
                    }
                  }
                }}
                type="button"
                className={` active:scale-[0.98] w-full h-[46px] rounded-full border border-[#B2D7EC] bg-white  text-gray-700 text-[18px] hover:cursor-pointer hover:bg-[#F8FAFD] ${baloo2Font.className}`}
                style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
              >
                <img
                  src="/google-icon.svg"
                  alt="Google"
                  className="w-[25px] h-[25px]"
                />
                Entrar com o Google
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
