export function getTokenFromCookie(): string | null {
  if (typeof document === "undefined") return null;

  return document.cookie
    .split("; ")
    .find(row => row.startsWith("token="))
    ?.split("=")[1] || null;
}
