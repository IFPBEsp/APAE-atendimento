import { NextResponse } from "next/server";
import fs from "fs";
import path from "path";

export async function POST(req: Request) {
  const { codigo } = await req.json();

  const filePath = path.join(process.cwd(), "data", "verificacao.json");

  if (!fs.existsSync(filePath)) {
    return NextResponse.json({ success: false, message: "Arquivo não encontrado." }, { status: 404 });
  }

  const fileData = JSON.parse(fs.readFileSync(filePath, "utf-8"));
  const codigoCorreto = fileData.codigo;

  if (codigo === codigoCorreto) {
    return NextResponse.json({ success: true, message: "Código validado com sucesso." });
  } else {
    return NextResponse.json({ success: false, message: "Código inválido." });
  }
}