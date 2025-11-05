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

  const [isFocused, setIsFocused] = useState(false);

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
          <div className="w-[100px] h-[100px] bg-white rounded-full flex items-center justify-center mx-auto mb-[0px]">
            <Image
              src="/logo-apae.svg" // ou o caminho correto da sua logo
              alt="APAE Logo"
              width={60}
              height={60}
            />
          </div>
          <CardContent className="flex flex-col justify-end h-full pb-[45px]">
            <form onSubmit={handleSubmit}>
              <div className="flex flex-col gap-6">
                <div className="grid gap-2 ">
                  <Label
                    htmlFor="email"
                    className={`font-bold text-gray-700 ${nunitoFont.className}`}
                  >
                    Email do profissional{" "}
                    <span className="text-[#F28C38]">*</span>
                  </Label>

                  <InputGroup className="h-[46px] rounded-full w-full bg-white text-[16px] font-normal text-gray-700 focus-within:ring-2 ">
                    <InputGroupInput
                      type="email"
                      value={email}
                      placeholder="email@email.com"
                      onChange={(e) => {
                        setEmail(e.target.value);
                        setError("");
                      }}
                      aria-invalid={error !== ""}
                      required
                      className="focus:outline-none"
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
  );
}
