import { 
  User, 
  IdCard, 
  Calendar, 
  MapPin, 
  Phone, 
  Users, 
  Activity, 
  ClipboardList, 
  FileText, 
  Paperclip,
  PlusCircle
} from "lucide-react";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Paciente } from "../types";
import { formatarData } from "@/utils/formatarData";

interface PacienteCardProps extends Paciente {
  onViewAtendimentos?: () => void;
  onViewRelatorios?: () => void;
  onViewAnexos?: () => void;
  onCreateAtendimento?: () => void;
  modo?: "meus" | "todos";
}

export function PacienteCard({
  nomeCompleto,
  cpf,
  endereco,
  contato,
  dataDeNascimento,
  fotoPreAssinada,
  transtornos,
  responsaveis,
  onViewAtendimentos,
  onViewRelatorios,
  onViewAnexos,
  onCreateAtendimento,
  modo = "meus",
}: PacienteCardProps) {
  return (
    <Card className="group w-full max-w-md md:max-w-4xl md:h-fit rounded-2xl shadow-sm hover:shadow-md border border-[#EAECF0] border-t-[6px] border-t-[#165BAA] bg-white relative flex flex-col transition-shadow duration-300 overflow-hidden">
      
      <CardContent className="p-5 flex flex-col md:flex-row md:gap-8 flex-1 relative">
        <div className="flex items-center gap-3 md:flex-col md:items-start md:w-36 shrink-0">
          <Avatar className="w-24 h-24 md:w-32 md:h-32 rounded-xl bg-[#F2F4F7]">
            {fotoPreAssinada ? (
              <AvatarImage src={fotoPreAssinada} alt={`Foto de ${nomeCompleto}`} className="object-cover" />
            ) : (
              <AvatarFallback>
                <User className="w-10 h-10 text-gray-400" />
              </AvatarFallback>
            )}
          </Avatar>

          <h2
            className="font-bold text-[#101828] text-base md:hidden text-left line-clamp-2"
            title={nomeCompleto}
          >
            {nomeCompleto}
          </h2>
        </div>

        <div className="flex flex-col justify-start flex-1 min-w-0 pt-1">
          <h2
            className="hidden md:block font-bold text-[#101828] text-xl mb-4 text-left truncate"
            title={nomeCompleto}
          >
            {nomeCompleto}
          </h2>
          
          <div className="text-left text-sm text-[#475467] leading-relaxed space-y-3">
            
            <div className="flex flex-wrap items-center gap-x-8 gap-y-3">
              <div className="flex items-center gap-2">
                <IdCard className="w-4 h-4 text-[#165BAA] shrink-0" />
                <span><span className="font-semibold text-[#344054]">CPF:</span> {cpf}</span>
              </div>
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-[#165BAA] shrink-0" />
                <span>{formatarData(dataDeNascimento)}</span>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Phone className="w-4 h-4 text-[#165BAA] shrink-0" />
              <span>{contato}</span>
            </div>
            
            <div className="flex items-center gap-2">
              <MapPin className="w-4 h-4 text-[#165BAA] shrink-0" />
              <span className="truncate" title={endereco}>{endereco}</span>
            </div>
            
            <div className="flex items-center gap-2">
              <Users className="w-4 h-4 text-[#165BAA] shrink-0" />
              <span className="truncate" title={responsaveis.join(", ")}>
                <span className="font-semibold text-[#344054]">Responsáveis:</span> {responsaveis.join(", ")}
              </span>
            </div>
            
            <div className="flex items-center gap-2 mt-2">
              <Activity className="w-4 h-4 text-[#165BAA] shrink-0" />
              <span className="font-medium text-[#165BAA] bg-[#165BAA]/10 px-2.5 py-0.5 rounded-md truncate max-w-fit" title={transtornos.join(", ")}>
                {transtornos.join(", ")}
              </span>
            </div>
          </div>
        </div>
      </CardContent>

      {modo === "meus" ? (
        <CardFooter className="px-5 pb-5 pt-0 flex flex-wrap gap-3 md:justify-start md:ml-[11rem] opacity-0 translate-y-4 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-300 ease-out">
          <Button 
            variant="outline" 
            className="flex-1 md:flex-none text-[#165BAA] border-[#165BAA]/30 bg-white hover:bg-[#165BAA] hover:text-white transition-colors duration-200 flex items-center gap-2"
            onClick={onViewAtendimentos}
          >
            <ClipboardList className="w-4 h-4" />
            Prontuário
          </Button>
          
          <Button 
            variant="outline" 
            className="flex-1 md:flex-none text-[#165BAA] border-[#165BAA]/30 bg-white hover:bg-[#165BAA] hover:text-white transition-colors duration-200 flex items-center gap-2"
            onClick={onViewRelatorios}
          >
            <FileText className="w-4 h-4" />
            Relatórios
          </Button>
          
          <Button 
            variant="outline" 
            className="flex-1 md:flex-none text-[#165BAA] border-[#165BAA]/30 bg-white hover:bg-[#165BAA] hover:text-white transition-colors duration-200 flex items-center gap-2"
            onClick={onViewAnexos}
          >
            <Paperclip className="w-4 h-4" />
            Anexos
          </Button>
        </CardFooter>
      ) : (
        <CardFooter className="px-5 pb-5 pt-0 md:ml-[11rem]">
          <Button 
            className="bg-[#165BAA] hover:bg-[#134e8f] text-white font-medium flex items-center gap-2 px-5 py-2 rounded-xl transition-colors"
            onClick={onCreateAtendimento}
          >
            <PlusCircle className="w-4 h-4" />
            Iniciar Novo Atendimento
          </Button>
        </CardFooter>
      )}

    </Card>
  );
}