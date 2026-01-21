"use client";

import { useRef, useState, useEffect } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";

const PACIENTE_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

export default function AvatarUploadTeste() {
  const inputRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);

  function handleAvatarClick() {
    inputRef.current?.click();
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    setFile(selectedFile);
    setPreview(URL.createObjectURL(selectedFile));
  }

  async function handleUpload() {
    if (!file) return;

    const formData = new FormData();
    formData.append("foto", file); // mesmo nome do backend

    setLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/pacientes/${PACIENTE_ID}`,
        {
          method: "POST",
          body: formData,
        }
      );

      const text = await response.text();
      alert(text);
    } catch (err) {
      alert("Erro ao enviar foto");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    return () => {
      if (preview) URL.revokeObjectURL(preview);
    };
  }, [preview]);

  return (
    <div className="flex flex-col gap-4 items-start">
      {/* INPUT ESCONDIDO */}
      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        hidden
        onChange={handleChange}
      />

      {/* AVATAR */}
      <Avatar
        onClick={handleAvatarClick}
        className="cursor-pointer w-40 h-40 rounded-xl bg-[#F2F4F7]"
      >
        <AvatarImage
          src={preview ?? "/avatar-placeholder.png"}
          alt="Avatar"
        />
      </Avatar>

      {/* BOTÃO ENVIAR */}
      {file && (
        <button
          onClick={handleUpload}
          disabled={loading}
          className="px-4 py-2 bg-black text-white rounded"
        >
          {loading ? "Enviando..." : "Enviar foto"}
        </button>
      )}
    </div>
  );
}
