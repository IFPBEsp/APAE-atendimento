import { useForm, useFieldArray } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Plus, Check, CircleMinus } from "lucide-react";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import { criarAtendimento } from "@/api/dadosAtendimentos";
import dados from "../../../data/verificacao.json";
import { Atendimento } from "@/types/Atendimento";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import { useEffect } from "react";

interface AtendimentoFormProps {
  atendimentos: Atendimento[];
  onCreated?: (novo: Atendimento) => void;
}

export default function AtendimentoForm({
  atendimentos,
  onCreated,
}: AtendimentoFormProps) {
  const { id } = useParams();

  const pacienteId = typeof id === "string" ? id : undefined;
  function getTodayLocalDate() {
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    return new Date(now.getTime() - offset).toISOString().split("T")[0];
  }
  const { register, handleSubmit, control, reset, watch, setValue } =
    useForm<Atendimento>({
      defaultValues: {
        data: new Date().toISOString().split("T")[0],
        hora: new Date().toLocaleTimeString("pt-BR", {
          hour: "2-digit",
          minute: "2-digit",
        }),
        numeracao: 1,
        relatorio: [{ titulo: "", descricao: "" }],
      },
    });

  const dataSelecionada = watch("data");

  useEffect(() => {
    if (!dataSelecionada) return;

    const [ano, mes] = dataSelecionada.split("-");

    const atendimentosDoMes = atendimentos.filter((a) => {
      const [diaBR, mesBR, anoBR] = a.data.split("/");
      return mesBR === mes && anoBR === ano;
    });

    const proximaNumeracao = atendimentosDoMes.length + 1;

    setValue("numeracao", proximaNumeracao);
  }, [dataSelecionada, atendimentos, setValue]);

  const { fields, append, remove } = useFieldArray({
    control,
    name: "relatorio",
  });

  async function onSubmit(data: Atendimento) {
    const [ano, mes, dia] = data.data.split("-");
    const dataBR = `${dia}-${mes}-${ano}`;
    if (!pacienteId) {
      toast.error("Paciente inválido.");
      return;
    }
    const payload = {
      profissionalId: dados.idProfissional,
      pacienteId,
      data: dataBR,
      hora: data.hora,
      relatorio: Object.fromEntries(
        data.relatorio.map((item) => [item.titulo, item.descricao])
      ),
    };

    try {
      const [ano, mes, dia] = data.data.split("-");
      const dataBR = `${dia}/${mes}/${ano}`;

      const response = await criarAtendimento(payload);
      const novoAtendimento: Atendimento = {
        id: response.id,
        data: dataBR,
        hora: response.hora,
        numeracao: response.numeracao ?? 1,
        relatorio: Object.entries(response.relatorio || {}).map(
          ([titulo, descricao]) => ({
            titulo,
            descricao: String(descricao),
          })
        ),
      };

      onCreated?.(novoAtendimento);
      console.log(novoAtendimento);
      toast.success("Atendimento criado com sucesso.");

      reset();
    } catch (error) {
      console.error(error);
      toast.error("Erro ao criar atendimento.");
    }
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="grid gap-4 pt-5 sm:pt-10 text-[#344054]"
    >
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
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
          <Label htmlFor="hora">
            Horário <span className="text-[#F28C38]">*</span>
          </Label>
          <Input
            type="time"
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
            {...register("hora", { required: true })}
          />
        </div>

        <div className="grid gap-2">
          <Label htmlFor="numeracao">Numeração</Label>
          <Input
            disabled
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] text-center"
            {...register("numeracao", { valueAsNumber: true })}
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
                {...register(`relatorio.${index}.titulo`, { required: true })}
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
            {...register(`relatorio.${index}.descricao`, {
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
