import { User, MoreVertical } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem} from "@/components/ui/dropdown-menu";

interface PacienteCardProps {
  nome: string;
  cpf: string;
  endereco: string;
  contato: string;
  dataNascimento: string;
  transtornos: string[];
  responsaveis: string[];
}

export function PacienteCard({
  nome,
  cpf,
  endereco,
  contato,
  dataNascimento,
  transtornos,
  responsaveis,
}: PacienteCardProps) {
  return (
    <Card className="w-full max-w-md rounded-2xl shadow-md border border-[#EAECF0] bg-white relative">
      
      <DropdownMenu>
          <DropdownMenuTrigger className="absolute top-2 right-2 p-1 rounded-full hover:bg-gray-100">
            <MoreVertical className="w-5 h-5 text-gray-500" />
          </DropdownMenuTrigger>
          <DropdownMenuContent 
            className="w-50 bg-white rounded-xl shadow-lg border border-gray-200 p-5 "
            sideOffset={8}
          >
          <DropdownMenuItem className="justify-center" onClick={() => console.log("Ver consultas")}>
            Ver consultas
          </DropdownMenuItem>
          <DropdownMenuItem className="justify-center" onClick={() => console.log("Criar relatório")}>
            Criar relatório
          </DropdownMenuItem>
          <DropdownMenuItem className="justify-center" onClick={() => console.log("Adicionar anexo")}>
            Adicionar anexo
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <CardContent className="p-4 flex flex-col gap-4">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-full bg-[#F2F4F7] flex items-center justify-center">
            <User className="w-6 h-6 text-gray-500" />
          </div>
          <h2 className="font-semibold text-[#344054] text-sm text-left">
            {nome}
          </h2>
        </div>

        <div className="text-left text-sm text-[#344054] leading-relaxed">
          <p><strong>CPF:</strong> {cpf}</p>
          <p><strong>Endereço:</strong> {endereco}</p>
          <p><strong>Contato:</strong> {contato}</p>
          <p><strong>Data de nascimento:</strong> {dataNascimento}</p>
          <p><strong>Transtornos:</strong> {transtornos.join(", ")}</p>
          <p><strong>Responsáveis:</strong> {responsaveis.join(", ")}</p>
        </div>
      </CardContent>
    </Card>
  );
}