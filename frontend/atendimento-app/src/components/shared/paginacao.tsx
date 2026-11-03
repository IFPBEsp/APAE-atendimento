"use client";

import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";

interface PaginacaoProps {
  page: number;
  totalPages: number;
  totalItems: number;
  itemLabel: string; 
  first: boolean;
  last: boolean;
  disabled?: boolean;
  onPrevious: () => void;
  onNext: () => void;
}

export default function Paginacao({
  page,
  totalPages,
  totalItems,
  itemLabel,
  first,
  last,
  disabled,
  onPrevious,
  onNext,
}: PaginacaoProps) {
  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-6 border-t border-gray-100 pt-6 px-2 w-full">
      <span className="text-sm text-gray-500 font-medium">
        Página {page} de {totalPages} ({totalItems} {itemLabel})
      </span>

      <div className="flex gap-3">
        <Button
          variant="outline"
          onClick={onPrevious}
          disabled={first || disabled}
          className="rounded-full shadow-sm hover:bg-gray-50 flex items-center"
        >
          <ChevronLeft className="h-4 w-4 mr-1" />
          Anterior
        </Button>

        <Button
          variant="outline"
          onClick={onNext}
          disabled={last || disabled}
          className="rounded-full shadow-sm hover:bg-gray-50 flex items-center"
        >
          Próxima
          <ChevronRight className="h-4 w-4 ml-1" />
        </Button>
      </div>
    </div>
  );
}