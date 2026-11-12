"use client";

import { useState } from "react";
import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Mail, Eye, EyeOff, CreditCard, ArrowLeft } from "lucide-react";
import { Nunito, Baloo_2 } from "next/font/google";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { isAxiosError } from "axios";
import { toast } from "sonner";

import { Card, CardContent } from "@/components/ui/card";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { esqueciSenha } from "@/services/authService";

const nunitoFont = Nunito({
  weight: "700",
  subsets: ["latin"],
});

const baloo2Font = Baloo_2({
  weight: "500",
  subsets: ["latin"],
});

export function formatarCpf(valor: string): string {
  const apenasDigitos = valor.replace(/\D/g, "").slice(0, 11);
  return apenasDigitos
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

export const identificacaoSchema = z.object({
  email: z
    .string()
    .trim()
    .min(1, "O e-mail é obrigatório.")
    .email("Informe um e-mail válido."),
  cpf: z
    .string()
    .min(1, "O CPF é obrigatório.")
    .regex(
      /^\d{3}\.\d{3}\.\d{3}-\d{2}$/,
      "Informe um CPF válido no formato 000.000.000-00.",
    ),
});

export const novaSenhaSchema = z
  .object({
    novaSenha: z
      .string()
      .min(8, "A nova senha deve ter no mínimo 8 caracteres."),
    confirmarNovaSenha: z
      .string()
      .min(1, "Confirme a nova senha."),
  })
  .refine((data) => data.novaSenha === data.confirmarNovaSenha, {
    message: "As senhas não coincidem. Verifique e tente novamente.",
    path: ["confirmarNovaSenha"],
  });

type IdentificacaoFormData = z.infer<typeof identificacaoSchema>;
type NovaSenhaFormData = z.infer<typeof novaSenhaSchema>;

export default function EsqueciSenhaPage() {
  const router = useRouter();
  const [step, setStep] = useState<1 | 2>(1);
  const [dadosIdentificacao, setDadosIdentificacao] =
    useState<IdentificacaoFormData>({
      email: "",
      cpf: "",
    });
  const [showNovaSenha, setShowNovaSenha] = useState(false);
  const [showConfirmarNovaSenha, setShowConfirmarNovaSenha] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const formIdentificacao = useForm<IdentificacaoFormData>({
    resolver: zodResolver(identificacaoSchema),
    defaultValues: {
      email: "",
      cpf: "",
    },
    mode: "onChange",
  });

  const formNovaSenha = useForm<NovaSenhaFormData>({
    resolver: zodResolver(novaSenhaSchema),
    defaultValues: {
      novaSenha: "",
      confirmarNovaSenha: "",
    },
    mode: "onChange",
  });

  const handleIdentificacaoSubmit = (data: IdentificacaoFormData) => {
    setDadosIdentificacao(data);
    setStep(2);
  };

  const handleNovaSenhaSubmit = async (data: NovaSenhaFormData) => {
    setIsSubmitting(true);

    try {
      const response = await esqueciSenha({
        email: dadosIdentificacao.email,
        cpf: dadosIdentificacao.cpf,
        novaSenha: data.novaSenha,
        confirmarNovaSenha: data.confirmarNovaSenha,
      });

      toast.success(response.message || "Senha redefinida com sucesso!");
      router.push("/login");
    } catch (err) {
      let errorMessage =
        "Não foi possível redefinir a senha. Verifique se o E-mail e o CPF conferem.";

      if (isAxiosError(err)) {
        const apiMessage = (
          err.response?.data as { message?: string } | undefined
        )?.message;
        if (apiMessage) {
          errorMessage = apiMessage;
        }
      } else if (err instanceof Error && err.message) {
        errorMessage = err.message;
      }

      toast.error(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const {
    register: registerIdentificacao,
    handleSubmit: submitIdentificacao,
    setValue: setIdentificacaoValue,
    formState: { errors: errorsIdentificacao },
  } = formIdentificacao;

  const {
    register: registerNovaSenha,
    handleSubmit: submitNovaSenha,
    formState: { errors: errorsNovaSenha },
  } = formNovaSenha;

  const cpfRegister = registerIdentificacao("cpf");

  return (
    <div className="h-screen w-screen bg-[url('/background-login-apae.svg')] relative bg-no-repeat bg-cover bg-center flex items-center justify-center">
      <div className="absolute flex items-center justify-center inset-0 bg-[#0D4F97]/80">
        <div className="absolute z-50 mt-[-32rem] w-[128px] h-[128px] bg-white rounded-full flex items-center justify-center mx-auto mb-[0px]">
          <Image
            src="/logo-apae.svg"
            alt="APAE Logo"
            width={90}
            height={140}
            className="mt-[3rem]"
            priority
          />
        </div>

        <Card className="w-[340px] min-h-[520px] rounded-[30px] sm:w-[410px]">
          <CardContent className="flex flex-col justify-end h-full pt-24 pb-6">
            {step === 1 ? (
              <form
                onSubmit={submitIdentificacao(handleIdentificacaoSubmit)}
                noValidate
              >
                <div className="mb-4 text-center">
                  <h1
                    className={`text-xl text-gray-800 ${baloo2Font.className}`}
                  >
                    Recuperar senha
                  </h1>
                  <p className="text-xs text-gray-500 mt-1">
                    Informe seu e-mail e CPF para redefinir sua senha
                  </p>
                </div>

                <div className="flex flex-col gap-5">
                  <div className="grid gap-1.5">
                    <Label
                      htmlFor="email"
                      className={`font-bold text-gray-700 ${nunitoFont.className}`}
                    >
                      E-mail <span className="text-[#F28C38]">*</span>
                    </Label>

                    <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                      <InputGroupInput
                        id="email"
                        className={nunitoFont.className}
                        type="email"
                        placeholder="seuemail@dominio.com"
                        autoComplete="email"
                        aria-invalid={!!errorsIdentificacao.email}
                        {...registerIdentificacao("email")}
                      />
                      <InputGroupAddon align="inline-end">
                        <Mail />
                      </InputGroupAddon>
                    </InputGroup>

                    {errorsIdentificacao.email && (
                      <p className="text-xs text-red-600 px-2">
                        {errorsIdentificacao.email.message}
                      </p>
                    )}
                  </div>

                  <div className="grid gap-1.5">
                    <Label
                      htmlFor="cpf"
                      className={`font-bold text-gray-700 ${nunitoFont.className}`}
                    >
                      CPF <span className="text-[#F28C38]">*</span>
                    </Label>

                    <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                      <InputGroupInput
                        id="cpf"
                        className={nunitoFont.className}
                        type="text"
                        inputMode="numeric"
                        maxLength={14}
                        placeholder="000.000.000-00"
                        aria-invalid={!!errorsIdentificacao.cpf}
                        {...cpfRegister}
                        onChange={(e) => {
                          const mascarado = formatarCpf(e.target.value);
                          e.target.value = mascarado;
                          setIdentificacaoValue("cpf", mascarado, {
                            shouldValidate: true,
                          });
                        }}
                      />
                      <InputGroupAddon align="inline-end">
                        <CreditCard />
                      </InputGroupAddon>
                    </InputGroup>

                    {errorsIdentificacao.cpf && (
                      <p className="text-xs text-red-600 px-2">
                        {errorsIdentificacao.cpf.message}
                      </p>
                    )}
                  </div>
                </div>

                <div className="mt-6 flex flex-col gap-3">
                  <Button
                    type="submit"
                    className={`active:scale-[0.98] w-full h-[46px] rounded-full bg-[#165BAA] disabled:bg-[#B0C6DE] hover:bg-[#13447D] text-[18px] hover:cursor-pointer ${baloo2Font.className}`}
                    style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
                  >
                    Continuar
                  </Button>

                  <div className="flex justify-center">
                    <Link
                      href="/login"
                      className={`inline-flex items-center gap-1 text-sm text-[#165BAA] hover:text-[#13447D] hover:underline transition-colors ${nunitoFont.className}`}
                    >
                      <ArrowLeft size={16} />
                      Voltar para o login
                    </Link>
                  </div>
                </div>
              </form>
            ) : (
              <form
                onSubmit={submitNovaSenha(handleNovaSenhaSubmit)}
                noValidate
              >
                <div className="mb-4 text-center">
                  <h1
                    className={`text-xl text-gray-800 ${baloo2Font.className}`}
                  >
                    Criar nova senha
                  </h1>
                  <p className="text-xs text-gray-500 mt-1">
                    Digite e confirme sua nova senha de acesso
                  </p>
                </div>

                <div className="flex flex-col gap-5">
                  <div className="grid gap-1.5">
                    <Label
                      htmlFor="novaSenha"
                      className={`font-bold text-gray-700 ${nunitoFont.className}`}
                    >
                      Nova Senha <span className="text-[#F28C38]">*</span>
                    </Label>

                    <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                      <InputGroupInput
                        id="novaSenha"
                        className={nunitoFont.className}
                        type={showNovaSenha ? "text" : "password"}
                        placeholder="********"
                        autoComplete="new-password"
                        aria-invalid={!!errorsNovaSenha.novaSenha}
                        {...registerNovaSenha("novaSenha")}
                      />
                      <InputGroupAddon align="inline-end">
                        <button
                          type="button"
                          onClick={() => setShowNovaSenha((prev) => !prev)}
                          className="text-gray-500 hover:text-gray-800 focus:outline-none transition-colors flex items-center justify-center w-full h-full"
                          title={
                            showNovaSenha ? "Ocultar senha" : "Mostrar senha"
                          }
                          aria-label={
                            showNovaSenha
                              ? "Ocultar nova senha"
                              : "Mostrar nova senha"
                          }
                        >
                          {showNovaSenha ? (
                            <EyeOff size={20} />
                          ) : (
                            <Eye size={20} />
                          )}
                        </button>
                      </InputGroupAddon>
                    </InputGroup>

                    {errorsNovaSenha.novaSenha && (
                      <p className="text-xs text-red-600 px-2">
                        {errorsNovaSenha.novaSenha.message}
                      </p>
                    )}
                  </div>

                  <div className="grid gap-1.5">
                    <Label
                      htmlFor="confirmarNovaSenha"
                      className={`font-bold text-gray-700 ${nunitoFont.className}`}
                    >
                      Confirmar Nova Senha{" "}
                      <span className="text-[#F28C38]">*</span>
                    </Label>

                    <InputGroup className="h-[46px] rounded-full w-full bg-white font-normal text-gray-700 focus-within:ring-2">
                      <InputGroupInput
                        id="confirmarNovaSenha"
                        className={nunitoFont.className}
                        type={showConfirmarNovaSenha ? "text" : "password"}
                        placeholder="********"
                        autoComplete="new-password"
                        aria-invalid={!!errorsNovaSenha.confirmarNovaSenha}
                        {...registerNovaSenha("confirmarNovaSenha")}
                      />
                      <InputGroupAddon align="inline-end">
                        <button
                          type="button"
                          onClick={() =>
                            setShowConfirmarNovaSenha((prev) => !prev)
                          }
                          className="text-gray-500 hover:text-gray-800 focus:outline-none transition-colors flex items-center justify-center w-full h-full"
                          title={
                            showConfirmarNovaSenha
                              ? "Ocultar senha"
                              : "Mostrar senha"
                          }
                          aria-label={
                            showConfirmarNovaSenha
                              ? "Ocultar confirmação de senha"
                              : "Mostrar confirmação de senha"
                          }
                        >
                          {showConfirmarNovaSenha ? (
                            <EyeOff size={20} />
                          ) : (
                            <Eye size={20} />
                          )}
                        </button>
                      </InputGroupAddon>
                    </InputGroup>

                    {errorsNovaSenha.confirmarNovaSenha && (
                      <p className="text-xs text-red-600 px-2">
                        {errorsNovaSenha.confirmarNovaSenha.message}
                      </p>
                    )}
                  </div>
                </div>

                <div className="mt-6 flex flex-col gap-3">
                  <Button
                    type="submit"
                    disabled={isSubmitting}
                    className={`active:scale-[0.98] w-full h-[46px] rounded-full bg-[#165BAA] disabled:bg-[#B0C6DE] hover:bg-[#13447D] text-[18px] hover:cursor-pointer ${baloo2Font.className}`}
                    style={{ boxShadow: "4px 4px 10px rgba(0, 0, 0, 0.25)" }}
                  >
                    {isSubmitting ? "Redefinindo..." : "Redefinir senha"}
                  </Button>

                  <div className="flex justify-center">
                    <button
                      type="button"
                      onClick={() => setStep(1)}
                      className={`inline-flex items-center gap-1 text-sm text-[#165BAA] hover:text-[#13447D] hover:underline transition-colors cursor-pointer ${nunitoFont.className}`}
                    >
                      <ArrowLeft size={16} />
                      Voltar
                    </button>
                  </div>
                </div>
              </form>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
