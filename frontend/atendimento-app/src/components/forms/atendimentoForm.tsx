import { useForm, useFieldArray } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Plus, Check, CircleMinus } from "lucide-react";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";

type Topico = {
  titulo: string;
  descricao: string;
};

export type AtendimentoFormData = {
  data: string;
  numeracao: number;
  topicos: Topico[];
};

interface AtendimentoFormProps {
  onSubmit: (data: AtendimentoFormData) => void;
}

export default function AtendimentoForm({ onSubmit }: AtendimentoFormProps) {
  const { register, handleSubmit, control } = useForm<AtendimentoFormData>({
    defaultValues: {
      data: new Date().toISOString().split("T")[0],
      numeracao: 1,
      topicos: [{ titulo: "", descricao: "" }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: "topicos",
  });

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="grid gap-4 pt-5 sm:pt-10 text-[#344054]"
    >
      <div className="grid grid-cols-2 gap-3">
        <div className="grid gap-2">
          <Label htmlFor="data">
            Data <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="date"
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
            {...register("data", { required: true })}
          />
        </div>

        <div className="grid gap-2">
          <Label htmlFor="numeracao">
            Numeração <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="number"
            min={1}
            max={10}
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
            {...register("numeracao", { required: true, valueAsNumber: true })}
          />
        </div>
      </div>

      {fields.map((field, index) => (
        <div key={field.id} className="grid gap-2">
          <div className="flex items-center justify-between">
            <Label>
              <Input
                required
                placeholder="Insira o título do tópico *"
                className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
                {...register(`topicos.${index}.titulo`, { required: true })}
              />
            </Label>

            {fields.length > 1 && (
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="h-6 w-6"
                onClick={() => remove(index)}
              >
                <CircleMinus className="h-4 w-4" />
              </Button>
            )}
          </div>

          <Textarea
            required
            className="min-h-[100px] w-full rounded-[30px] border border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] px-3 py-2 text-sm"
            placeholder="Insira a descrição do tópico"
            {...register(`topicos.${index}.descricao`, {
              required: index === 0,
            })}
          />
        </div>
      ))}

      <DialogFooter className="flex-col sm:flex-row sm:justify-center gap-2">
        <Button
          type="button"
          variant="outline"
          className="w-full flex-1 sm:w-auto rounded-[30px] shadow-md border-[#0D4F97] text-[#0D4F97] cursor-pointer"
          onClick={() => append({ titulo: "", descricao: "" })}
        >
          <Plus className="mr-2 h-4 w-4" />
          Adicionar tópico
        </Button>

        <Button
          type="submit"
          className="w-full flex-1 sm:w-auto rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D] cursor-pointer"
        >
          <Check className="mr-2 h-4 w-4" />
          Criar atendimento
        </Button>
      </DialogFooter>
    </form>
  );
}
