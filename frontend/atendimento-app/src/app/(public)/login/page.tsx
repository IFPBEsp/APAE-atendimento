"use client";

import { useState, useEffect } from "react";

import { Mail } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import { Card, CardContent } from "@/components/ui/card";
import { useRouter } from "next/navigation";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { 
  isSignInWithEmailLink, 
  signInWithEmailLink, 
  sendSignInLinkToEmail, 
  GoogleAuthProvider, 
  signInWithPopup, 
  deleteUser, 
  getAdditionalUserInfo 
} from "firebase/auth";
import { auth } from "@/lib/firebase";

import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";

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

  useEffect(() => {
    if (typeof window === "undefined") return;

    const finalizarLoginComLink = async () => {
      if (isSignInWithEmailLink(auth, window.location.href)) {
        let emailSalvo = window.localStorage.getItem("emailForSignIn");

        if (!emailSalvo) {
          emailSalvo = window.prompt("Confirme seu e-mail para continuar") || "";
        }

        try {
          const result = await signInWithEmailLink(
            auth,
            emailSalvo,
            window.location.href
          );

          const token = await result.user.getIdToken();
          document.cookie = `token=${token}; path=/;`;

          window.localStorage.removeItem("emailForSignIn");

          router.push("/home");
        } catch (err: any) {
          tratarErroFirebase(err.code);
        }
      }
    };

    finalizarLoginComLink();
  }, []);

  const isValidEmail = (value: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  const tratarErroFirebase = (code: string) => {
    switch (code) {
      case "auth/user-not-found":
        setError("E-mail não cadastrado.");
        break;
      case "auth/wrong-password":
        setError("Senha incorreta.");
        break;
      case "auth/invalid-email":
        setError("E-mail inválido.");
        break;
      case "auth/popup-closed-by-user":
        setError("Login cancelado pelo usuário.");
        break;
      default:
        setError("Erro ao efetuar login.");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!isValidEmail(email)) {
      setError("Formato de e-mail inválido.");
      return;
    }

    try {
      const actionCodeSettings = {
        url: "http://localhost:3000/login",
        handleCodeInApp: true,
      };

      console.log("Enviando link de login para:", email);
      await sendSignInLinkToEmail(auth, email, actionCodeSettings);
      console.log("Link de login enviado para:", email);
      window.localStorage.setItem("emailForSignIn", email);
      setError("Enviamos um link de acesso para seu e-mail.");
    } catch (error: any) {
      tratarErroFirebase(error.code);
    };
  }

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
                <hr className="flex-grow border-[#B2D7EC]" />
                <span className="mx-2 text-gray-500 text-sm">ou</span>
                <hr className="flex-grow border-[#B2D7EC]" />
              </div>

              <Button
                type="button"
                onClick={async () => {
                  try {
                    const provider = new GoogleAuthProvider();
                    const result = await signInWithPopup(auth, provider);
                    const details = getAdditionalUserInfo(result);

                    if (details?.isNewUser) {
                      await deleteUser(result.user);
                      setError("Este e-mail não está autorizado.");
                      return;
                    }

                    const token = await result.user.getIdToken();
                    document.cookie = `token=${token}; path=/;`;
                    router.push("/home");

                  } catch (err: any) {
                    tratarErroFirebase(err.code);
                  }
                }}
                className={`active:scale-[0.98] w-full h-[46px] rounded-full border border-[#B2D7EC] bg-white text-gray-700 text-[18px] hover:cursor-pointer`}
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
};
