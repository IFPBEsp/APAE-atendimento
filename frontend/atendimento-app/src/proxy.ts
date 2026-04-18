import { NextRequest, NextResponse } from "next/server";

const PRIVATE_PREFIXES = [
  "/home",
  "/agenda",
  "/atendimento",
  "/relatorio",
  "/anexo",
];

function matchesPrefix(pathname: string, prefix: string) {
  return pathname === prefix || pathname.startsWith(`${prefix}/`);
}

export function proxy(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const token = request.cookies.get("token")?.value;

  const isPrivateRoute = PRIVATE_PREFIXES.some((prefix) =>
    matchesPrefix(pathname, prefix),
  );

  if (isPrivateRoute && !token) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/home/:path*",
    "/agenda/:path*",
    "/atendimento/:path*",
    "/relatorio/:path*",
    "/anexo/:path*",
  ],
};
