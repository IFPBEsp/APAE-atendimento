import { User, MoreVertical } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { 
  DropdownMenu, 
  DropdownMenuTrigger, 
  DropdownMenuContent, 
  DropdownMenuItem} from "@/components/ui/dropdown-menu";
import {
  Avatar,
  AvatarImage,
  AvatarFallback
} from "@/components/ui/avatar";

interface PacienteCardProps {
  id?: string;
  nome: string;
  cpf: string;
  endereco: string;
  contato: string;
  dataNascimento: string;
  transtornos: string[];
  responsaveis: string[];
  onViewAtendimentos?: () => void;
  onViewRelatorios?: () => void;
}

export function PacienteCard({
  nome,
  cpf,
  endereco,
  contato,
  dataNascimento,
  transtornos,
  responsaveis,
  onViewAtendimentos,
  onViewRelatorios
}: PacienteCardProps) {
  return (
    <Card className="w-full max-w-md md:max-w-4xl rounded-2xl shadow-md border border-[#EAECF0] bg-white relative">

      <DropdownMenu>
        <DropdownMenuTrigger className="absolute top-2 right-2 p-1 rounded-full hover:bg-gray-100">
          <MoreVertical className="w-5 h-5 text-gray-500" />
        </DropdownMenuTrigger>
        <DropdownMenuContent 
          className="w-48 bg-white rounded-xl shadow-lg border border-gray-200 p-4"
          sideOffset={8}
        >
          <DropdownMenuItem 
            className="justify-center"
            onClick={onViewAtendimentos}
          >
              Ver Atendimentos
          </DropdownMenuItem>

          <DropdownMenuItem 
            className="justify-center"
            onClick={onViewRelatorios}
          >
            Criar relatório
          </DropdownMenuItem>
          
          <DropdownMenuItem className="justify-center">Adicionar anexo</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <CardContent className="p-4 flex flex-col md:flex-row md:gap-6">

        <div className="flex items-center gap-3 md:flex-col md:items-start md:gap-4 md:w-40 pb-2">

            <Avatar className="w-12 h-12 rounded-full bg-[#F2F4F7] flex items-center justify-center md:w-40 md:h-50 md:rounded-xl">
              <AvatarImage src=""/>
              <AvatarFallback>
                <User className="w-6 h-6 text-gray-500 md:w-10 md:h-10" />
              </AvatarFallback>
            </Avatar>


          {/* Nome visível SOMENTE no mobile*/}
          <h2 className="font-semibold text-[#344054] text-base md:hidden text-left">
            {nome}
          </h2>
        </div>

        <div className="flex flex-col justify-center flex-1">
          {/* Nome visível SOMENTE no desktop */}
          <h2 className="hidden md:block font-semibold text-[#344054] text-lg mb-3 text-left">
            {nome}
          </h2>

          <div className="text-left text-sm text-[#344054] leading-relaxed space-y-1">
            <p><strong>CPF:</strong> {cpf}</p>
            <p><strong>Endereço:</strong> {endereco}</p>
            <p><strong>Contato:</strong> {contato}</p>
            <p><strong>Data de nascimento:</strong> {dataNascimento}</p>
            <p><strong>Transtornos:</strong> {transtornos.join(", ")}</p>
            <p><strong>Responsáveis:</strong> {responsaveis.join(", ")}</p>
          </div>

        </div>

      </CardContent>
    </Card>
  );
}