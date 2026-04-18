"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { me } from "@/services/authService";

type AuthState = "checking" | "authenticated" | "unauthenticated";

export default function PrivateLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const [authState, setAuthState] = useState<AuthState>("checking");

  useEffect(() => {
    let active = true;

    async function validateSession() {
      try {
        await me();
        if (active) setAuthState("authenticated");
      } catch {
        if (!active) return;
        setAuthState("unauthenticated");
        router.replace("/login");
      }
    }

    validateSession();

    return () => {
      active = false;
    };
  }, [router]);

  if (authState !== "authenticated") {
    return (
      <main className="min-h-screen bg-[#F8FAFD] flex items-center justify-center">
        <p className="text-sm text-[#475467]">Validando sessão...</p>
      </main>
    );
  }

  return <>{children}</>;
}
