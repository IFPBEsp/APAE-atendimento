import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Plus, Check, CircleMinus } from "lucide-react";

export default function AtendimentoForm() {
  const [topicos, setTopicos] = useState([{ id: 1 }]);

  const adicionarTopico = () => {
    const novoId =
      topicos.length > 0 ? Math.max(...topicos.map((t) => t.id)) + 1 : 1;
    setTopicos([...topicos, { id: novoId }]);
  };

  const removerTopico = (id: number) => {
    if (topicos.length > 1) {
      setTopicos(topicos.filter((topico) => topico.id !== id));
    }
  };

  return (
    <>
      <DialogHeader>
        <DialogTitle className="text-center text-[#344054]">
          Adicionar novo atendimento
        </DialogTitle>
      </DialogHeader>

      <form className="grid gap-4 pt-5 sm:pt-10 text-[#344054]">
        <div className="grid grid-cols-2 gap-3">
          <div className="grid gap-2">
            <Label htmlFor="data">
              Data <span className="text-red-500">*</span>
            </Label>
            <Input
              id="data"
              name="data"
              type="date"
              defaultValue="2025-11-01"
              className="rounded-[30px] shadow-none border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
              required
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="numeracao">
              Numeração <span className="text-red-500">*</span>
            </Label>
            <Input
              id="numeracao"
              name="numeracao"
              type="number"
              defaultValue="1"
              min="1"
              max="10"
              className="rounded-[30px] shadow-none border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
              required
            />
          </div>
        </div>

        {topicos.map((topico, index) => (
          <div key={topico.id} className="grid gap-2">
            <div className="flex items-center justify-between">
              <Label htmlFor={`topico-${topico.id}`}>
                <Input
                  placeholder={"Insira o título do tópico *"}
                  className="p-0 rounded-none border-0 border-b-1 shadow-none border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC]"
                />
              </Label>
              {topicos.length > 1 && (
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="h-6 w-6"
                  onClick={() => removerTopico(topico.id)}
                >
                  <CircleMinus className="h-4 w-4" />
                </Button>
              )}
            </div>
            <textarea
              id={`topico-${topico.id}`}
              name={`topico-${topico.id}`}
              placeholder="Insira a descrição do tópico"
              className="min-h-[100px] w-full rounded-[30px] border shadow-none border-[#B2D7EC] focus-visible:ring-0 focus-visible:border-[#B2D7EC] bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none"
              required={index === 0}
            />
          </div>
        ))}

        <DialogFooter className="flex-col sm:flex-row sm:justify-center gap-2">
          <Button
            type="button"
            variant="outline"
            className="w-full flex-1 sm:w-auto rounded-[30px] shadow-md border-[#0D4F97] text-[#0D4F97] hover:bg-[#fafafa]"
            onClick={adicionarTopico}
          >
            <Plus className="mr-2 h-4 w-4" />
            Adicionar tópico
          </Button>
          <Button
            type="submit"
            className="w-full flex-1 sm:w-auto rounded-[30px] shadow-md bg-[#0D4F97] hover:bg-[#13447D]"
          >
            <Check className="mr-2 h-4 w-4" /> Criar atendimento
          </Button>
        </DialogFooter>
      </form>
    </>
  );
}
