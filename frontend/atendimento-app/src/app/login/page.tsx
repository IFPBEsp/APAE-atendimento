"use client";

import { useState } from "react";
import Image from "next/image";
import { Mail, Info } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import {
  InputGroup,
  InputGroupAddon,
  InputGroupButton,
  InputGroupInput,
  InputGroupText,
  InputGroupTextarea,
} from "@/components/ui/input-group";

import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
const nunitoFont = Nunito({
  weight: "700",
});

const baloo2Font = Baloo_2({
  weight: "500",
});

const novoemail = "thalesluiz@hotmail.com";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");

  const [touched, setTouched] = useState(false);

  const isValidEmail = (value: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  const hasError = touched && !isValidEmail(email) && email !== "";

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email !== "algo@hotmail.com") {
      setError("E-mail inválido.");
      return;
    }
    if (!isValidEmail(email)) return;
    alert(`Login com o e-mail: ${email}`);
  };

  return (
    <div className="h-screen w-screen bg-[url('/background-login-apae.svg')] bg-no-repeat bg-cover bg-center flex items-center justify-center">
      <div className="absolute flex items-center justify-center inset-0 bg-[#0D4F97]/80">
        <Card className="w-[410px] h-[455px] rounded-[30px]">
          <CardContent>
            <form onSubmit={handleSubmit}>
              <div className="flex flex-col gap-6">
                <div className="grid gap-2 ">
                  <Label htmlFor="email">Email</Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white text-[16px] font-normal text-gray-700">
                    <InputGroupInput
                      type="email"
                      value={email}
                      placeholder="email@email.com"
                      onChange={(e) => setEmail(e.target.value)}
                      required
                    />
                    <InputGroupAddon>
                      <Mail />
                    </InputGroupAddon>
                    <InputGroupAddon align="inline-end">
                      <Info />
                    </InputGroupAddon>
                  </InputGroup>
                </div>
              </div>

              <div className="h-6 mt-[6px] mb-[19px]">
                {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
              </div>

              <Button
                type="submit"
                disabled={!isValidEmail(email)}
                className={`active:scale-[0.98] w-full h-[46px] rounded-full bg-[#165BAA] disabled:bg-[#B0C6DE] hover:bg-[#13447D] text-[18px] hover:cursor-pointer ${baloo2Font.className}`}
                style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
              >
                Entrar
              </Button>

              <div className="flex items-center my-[16px]">
                <hr className="flex-grow border-gray-300" />
                <span className="mx-2 text-gray-500 text-sm">ou</span>
                <hr className="flex-grow border-gray-300" />
              </div>

              <Button
                type="button"
                className={` active:scale-[0.98] w-full h-[46px] rounded-full border border-[#B2D7EC] bg-white  text-gray-700 text-[18px] hover:cursor-pointer hover:bg-[#F8FAFD] ${baloo2Font.className}`}
                style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
              >
                <Image
                  src="/google-icon.svg"
                  alt="Google"
                  width={25}
                  height={25}
                />
                Entrar com o Google
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>

    /* <div
      className="
        flex items-center justify-center min-h-screen
        bg-cover bg-center bg-no-repeat
        bg-[url('/background-login-apae.svg')] 
      "
    >
      <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-sm text-center">
        <div className="flex flex-col items-center mb-6">
          <Image src="/logo-apae.svg" alt="Logo APAE" width={60} height={60} />
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="text-left">
            <label
              className={`block text-[18px] text-gray-700 mb-1 ${nunitoFont.className}`}
            >
              Email do profissional <span className="text-orange-400">*</span>
            </label>
            <div
              className={`relative flex items-center h-[46px] rounded-full border transition-colors ${
                hasError
                  ? "border-[#F45D6C]"
                  : email
                  ? "border-[#0D4F97]"
                  : "border-[#D9E1E8]"
              }`}
            >
              <Mail
                size={20}
                className={`absolute left-4 ${
                  hasError
                    ? "text-[#F45D6C]"
                    : email
                    ? "text-blue-600"
                    : "text-gray-400"
                }`}
              />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                onBlur={() => setTouched(true)}
                placeholder="linda@framcreative.com"
                className="w-full h-full pl-10 pr-10 bg-transparent text-[16px] font-normal text-gray-700 outline-none rounded-full"
              />
              <Info
                size={20}
                className={`absolute right-4 ${
                  hasError
                    ? "text-[#F45D6C]"
                    : email
                    ? "text-blue-600"
                    : "text-gray-400"
                }`}
              />
            </div>
            {hasError && (
              <p className="text-[#F45D6C] text-sm mt-2.5">Email inválido.</p>
            )}
          </div>

          <Button
            type="submit"
            disabled={!isValidEmail(email)}
            className={`active:scale-[0.98] w-full h-[46px] rounded-full bg-[#165BAA] disabled:bg-[#B0C6DE] hover:bg-[#13447D] text-[18px] hover:cursor-pointer ${baloo2Font.className}`}
            style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
          >
            Entrar
          </Button>

          <div className="flex items-center">
            <hr className="flex-grow border-gray-300" />
            <span className="mx-2 text-gray-500 text-sm">ou</span>
            <hr className="flex-grow border-gray-300" />
          </div>

          <Button
            type="button"
            className={` active:scale-[0.98] w-full h-[46px] rounded-full border border-[#B2D7EC] bg-white  text-gray-700 text-[18px] hover:cursor-pointer hover:bg-[#F8FAFD] ${baloo2Font.className}`}
            style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
          >
            <Image src="/google-icon.svg" alt="Google" width={25} height={25} />
            Entrar com o Google
          </Button>
        </form>
      </div>
    </div>*/
  );
}
