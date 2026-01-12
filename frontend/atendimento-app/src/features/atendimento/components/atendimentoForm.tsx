import { useForm, useFieldArray } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Plus, Check, CircleMinus } from "lucide-react";
import { DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import {
  criarAtendimento,
  editarAtendimento,
} from "../services/atendimentoService";
import dados from "../../../../data/verificacao.json";
import { Atendimento, AtendimentoPayload } from "../types";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import { useEffect } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface AtendimentoFormProps {
  atendimentos: Atendimento[];
  atendimentoEditavel?: Atendimento;
  onClose: () => void;
}

interface AtendimentoFormValues {
  data: string;
  hora: string;
  numeracao: number;
  relatorio: {
    titulo: string;
    descricao: string;
  }[];
}

function isoParaBR(iso: string): string {
  if (!iso) return "";
  const [ano, mes, dia] = iso.split("-");
  return `${dia}-${mes}-${ano}`;
}

function brParaISO(dataBR: string): string {
  if (!dataBR) return "";
  const [dia, mes, ano] = dataBR.split("-");
  return `${ano}-${mes}-${dia}`;
}

export default function AtendimentoForm({
  atendimentos,
  atendimentoEditavel,
  onClose,
}: AtendimentoFormProps) {
  const { id } = useParams();
  const pacienteId = typeof id === "string" ? id : undefined;

  function getTodayLocalDate() {
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    return new Date(now.getTime() - offset).toISOString().split("T")[0];
  }

  const queryClient = useQueryClient();

  const { register, handleSubmit, control, reset, watch, setValue } =
    useForm<AtendimentoFormValues>({
      defaultValues: atendimentoEditavel
        ? {
            data: brParaISO(atendimentoEditavel.data.replace(/\//g, "-")),
            hora: atendimentoEditavel.hora,
            numeracao: atendimentoEditavel.numeracao,
            relatorio: atendimentoEditavel.relatorio.map((r) => ({
              titulo: r.titulo,
              descricao: r.descricao,
            })),
          }
        : {
            data: getTodayLocalDate(),
            hora: new Date().toLocaleTimeString("pt-BR", {
              hour: "2-digit",
              minute: "2-digit",
            }),
            numeracao: 1,
            relatorio: [{ titulo: "", descricao: "" }],
          },
    });

  const createMutation = useMutation({
    mutationFn: criarAtendimento,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["atendimentos"] });
      toast.success("Atendimento criado com sucesso.");
      reset();
      onClose();
    },
    onError: () => {
      toast.error("Erro ao criar atendimento.");
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: string;
      payload: AtendimentoPayload;
    }) => editarAtendimento(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["atendimentos"] });
      toast.success("Atendimento atualizado com sucesso.");
      reset();
      onClose();
    },
    onError: () => {
      toast.error("Erro ao atualizar atendimento.");
    },
  });

  const dataSelecionada = watch("data");

  useEffect(() => {
    if (!dataSelecionada) return;

    const [ano, mes] = dataSelecionada.split("-");

    if (atendimentoEditavel) {
      const dataOriginalISO = brParaISO(atendimentoEditavel.data);

      if (dataSelecionada === dataOriginalISO) {
        setValue("numeracao", atendimentoEditavel.numeracao);
        return;
      }
    }

    const atendimentosDoMes = atendimentos.filter((a) => {
      const [, mesBR, anoBR] = a.data.split("/");

      if (atendimentoEditavel && a.id === atendimentoEditavel.id) {
        return false;
      }

      return mesBR === mes && anoBR === ano;
    });

    setValue("numeracao", atendimentosDoMes.length + 1);
  }, [dataSelecionada, atendimentos, atendimentoEditavel, setValue]);

  const { fields, append, remove } = useFieldArray({
    control,
    name: "relatorio",
  });

  function onSubmit(data: AtendimentoFormValues) {
    if (!pacienteId) {
      toast.error("Paciente inválido.");
      return;
    }

    const payload: AtendimentoPayload = {
      profissionalId: dados.idProfissional,
      pacienteId,
      data: isoParaBR(data.data),
      hora: data.hora,
      numeracao: data.numeracao,
      relatorio: data.relatorio,
    };

    if (atendimentoEditavel) {
      updateMutation.mutate({
        id: atendimentoEditavel.id,
        payload,
      });
    } else {
      createMutation.mutate(payload);
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
            required
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
            required
            type="time"
            className="rounded-[30px] border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
            {...register("hora", { required: true })}
          />
        </div>

        <div className="grid gap-2">
          <Label htmlFor="numeracao">Numeração</Label>
          <Input
            required
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
          {atendimentoEditavel ? "Salvar alterações" : "Criar atendimento"}
        </Button>
      </DialogFooter>
    </form>
  );
}
