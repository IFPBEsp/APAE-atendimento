import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "../ui/textarea";
import { Upload, CirclePlus, Info } from "lucide-react";

export type RelatorioFormData = {
  data: string;
  titulo: string;
  arquivo?: FileList;
  descricao: string;
};

interface RelatorioFormProps {
  onSubmit: (data: RelatorioFormData) => void;
}

export default function RelatorioForm({ onSubmit }: RelatorioFormProps) {
  const { register, handleSubmit, watch } = useForm<RelatorioFormData>({
    defaultValues: {
      data: new Date().toISOString().split("T")[0],
      titulo: "",
      descricao: "",
    },
  });

  const arquivo = watch("arquivo");
  const previewUrl = arquivo && arquivo[0] ? URL.createObjectURL(arquivo[0]) : null;

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="grid gap-6 pt-5 text-[#344054]"
    >
      <div className="grid gap-2">
        <Label>
          Data<span className="text-[#F28C38]">*</span>
        </Label>

        <Input
          type="date"
          className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]
          text-black-400"
          {...register("data", { required: true })}
        />

      </div>

      <div className="grid gap-2">
        <Label>Inserir arquivo</Label>

        <Label
          htmlFor="arquivo"
          className="
          relative w-full h-[150px] flex flex-col items-center justify-center border-1 border-dashed border-[#B2D7EC] rounded-[30px] cursor-pointer bg-[#F8FAFD] overflow-hidden"
        >
          {previewUrl ? (
            <img
              src={previewUrl}
              alt="Prévia"
              className="w-[200px] h-full object-cover"
            />
          ) : (
            <button className="pointer-events-none flex items-center gap-2 px-6 py-2 rounded-full
              bg-[#0D4F97] text-white font-semibold h-9">
            <Upload className="h-4 w-4 text-white" />
              Enviar arquivo
          </button>
          )}
          
          {arquivo && arquivo[0] && (
            <p className="absolute bottom-0 left-1/2 transform -translate-x-1/2 
                          bg-white bg-opacity-70 border border-[#B2D7EC]
                          text-[#344054] text-sm px-3 py-1 rounded-full">
              {arquivo[0].name}
            </p>
          )}

        </Label>
        <Input
          id="arquivo"
          type="file"
          accept="image/*"
          className="hidden"
          {...register("arquivo")}
        />
 
    </div>

      <div className="flex items-center gap-3">
        <div className="flex-1 h-[1px] bg-gray-300"></div>
        <span className="text-sm text-gray-500">ou</span>
        <div className="flex-1 h-[1px] bg-gray-300"></div>
      </div>

      
      <div className="grid gap-2 flex items-center justify-between">

      <Label>Gerar relatório por template <Info size={15}/></Label>
      
       <Label>
          <Input
            required
            placeholder="Insira o título do relatório*"
            className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
            {...register("titulo")}
          />
        </Label>
      </div>

      <Textarea
        required
        className="min-h-[100px] w-full rounded-[30px] border border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] px-3 py-2 text-sm"
        {...register("descricao")}
      />

      <DialogFooter>
        <Button
          type="submit"
          className="w-full rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
        >
          <CirclePlus/>Adicionar Relatório
        </Button>
      </DialogFooter>
    </form>
  );
}
