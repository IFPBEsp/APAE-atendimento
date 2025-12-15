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

  /** Dados iniciais vindos do agendamento */
  initialData?: {
    pacienteNome?: string;
    data?: string;
    numeracao?: number;
  };

  /** Campos que devem ficar bloqueados */
  lockedFields?: {
    paciente?: boolean;
    data?: boolean;
    numeracao?: boolean;
  };
}

export default function AtendimentoForm({
  onSubmit,
  initialData,
  lockedFields,
}: AtendimentoFormProps) {
  const { register, handleSubmit, control } = useForm<AtendimentoFormData>({
    defaultValues: {
      data:
        initialData?.data ??
        new Date().toISOString().split("T")[0],
      numeracao: initialData?.numeracao ?? 1,
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
      {/* Paciente (somente quando vier do agendamento) */}
      {initialData?.pacienteNome && (
        <div className="grid gap-2">
          <Label>Paciente</Label>
          <Input
            value={initialData.pacienteNome}
            disabled
            className="rounded-[30px] bg-gray-100 border-[#B2D7EC] cursor-not-allowed"
          />
        </div>
      )}

      <div className="grid grid-cols-2 gap-3">
        {/* Data */}
        <div className="grid gap-2">
          <Label>
            Data <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="date"
            disabled={lockedFields?.data}
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] disabled:bg-gray-100"
            {...register("data", { required: true })}
          />
        </div>

        {/* Numeração */}
        <div className="grid gap-2">
          <Label>
            Numeração <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="number"
            min={1}
            max={10}
            disabled={lockedFields?.numeracao}
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] disabled:bg-gray-100"
            {...register("numeracao", {
              required: true,
              valueAsNumber: true,
            })}
          />
        </div>
      </div>

      {/* Tópicos */}
      {fields.map((field, index) => (
        <div key={field.id} className="grid gap-2">
          <div className="flex items-center justify-between">
            <Label className="flex-1">
              <Input
                required
                placeholder="Insira o título do tópico *"
                className="p-0 rounded-none border-0 border-b border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
                {...register(`topicos.${index}.titulo`, {
                  required: true,
                })}
              />
            </Label>

            {fields.length > 1 && (
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="h-6 w-6 cursor-pointer"
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
