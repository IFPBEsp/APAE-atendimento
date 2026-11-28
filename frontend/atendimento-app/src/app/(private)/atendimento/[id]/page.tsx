import { Suspense } from "react";
import AtendimentoPage from "@/features/atendimento/components/atendimentoPage";

export default function Page() {
  return (
    <Suspense fallback={null}>
      <AtendimentoPage />
    </Suspense>
  );
}
