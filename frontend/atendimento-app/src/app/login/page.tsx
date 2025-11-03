'use client';

import { useState } from 'react';
import Image from 'next/image';
import { Mail, LogIn, Info } from 'lucide-react';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [touched, setTouched] = useState(false);

  const isValidEmail = (value: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  const hasError = touched && !isValidEmail(email) && email !== '';

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isValidEmail(email)) return;
    alert(`Login com o e-mail: ${email}`);
  };

  return (
    <div
      className="
        flex items-center justify-center min-h-screen
        bg-cover bg-center bg-no-repeat
      "
      style={{ backgroundImage: "url('/background-login-apae.svg')" }}
    >
     
      <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-sm text-center">

        {/* Logo */}
        <div className="flex flex-col items-center mb-6">
          <Image src="/logo-apae.svg" alt="Logo APAE" width={60} height={60} />
        </div>

        {/* Formulário */}
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="text-left">
            <label className="block font-bold text-[18px] text-gray-700 mb-1">
              Email do profissional <span className="text-red-500">*</span>
            </label>
            <div
              className={`relative flex items-center h-[46px] rounded-full border transition-colors ${
                hasError
                  ? 'border-red-500'
                  : email
                  ? 'border-blue-600'
                  : 'border-[#D9E1E8]'
              }`}
            >
              <Mail
                size={20}
                className={`absolute left-4 ${
                  hasError
                    ? 'text-red-500'
                    : email
                    ? 'text-blue-600'
                    : 'text-gray-400'
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
                    ? 'text-red-500'
                    : email
                    ? 'text-blue-600'
                    : 'text-gray-400'
                }`}
              />
            </div>
            {hasError && (
              <p className="text-red-500 text-sm mt-1">Email inválido.</p>
            )}
          </div>

          {/* Botão principal */}
          <button
            type="submit"
            disabled={!isValidEmail(email)}
            className="
              w-full h-[46px]
              bg-blue-600 text-white text-[18px] font-medium
              rounded-full
              flex items-center justify-center gap-3
              transition
              hover:bg-blue-700
              disabled:bg-blue-300 disabled:cursor-not-allowed
            "
          >
            <LogIn size={25} />
            Entrar
          </button>

          {/* Separador */}
          <div className="flex items-center my-3">
            <hr className="flex-grow border-gray-300" />
            <span className="mx-2 text-gray-500 text-sm">ou</span>
            <hr className="flex-grow border-gray-300" />
          </div>

          {/* Botão Google */}
          <button
            type="button"
            className="
              w-full h-[46px]
              border border-gray-300 rounded-full
              flex items-center justify-center gap-[12px]
              text-gray-700 font-medium
              transition-all duration-150
              hover:bg-gray-100 hover:shadow-md hover:cursor-pointer
              active:scale-[0.98]
              shadow-sm
            "
          >
            <Image src="/google-icon.svg" alt="Google" width={25} height={25} />
            Entrar com o Google
          </button>
        </form>
      </div>
    </div>
  );
}
