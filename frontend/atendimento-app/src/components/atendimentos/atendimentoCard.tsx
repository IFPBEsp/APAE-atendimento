import { Card, CardContent } from "@/components/ui/card";

interface AtendimentoCardProps {
  id: string;
  data: string;
  numeracao: string;
  titulo: string;
  descricao: string;
}

export default function AtendimentoCard({
  data,
  numeracao,
  titulo,
  descricao,
}: AtendimentoCardProps) {
  return (
    <Card className="rounded-2xl shadow-md border border-[#EAECF0] bg-white">
      <CardContent className="p-4 space-y-2">
        <p className="text-sm text-gray-700 font-semibold">{data}</p>
        <p className="text-sm text-gray-700 font-semibold">{numeracao}</p>
        <p className="text-sm text-gray-700 font-semibold">{titulo}</p>
        <p className="text-sm text-gray-700 font-semibold">{descricao}</p>
      </CardContent>
    </Card>
  );
}