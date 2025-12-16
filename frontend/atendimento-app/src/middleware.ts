import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  const token = request.cookies.get("token")?.value;

  const publicRoutes = ["/login"];
  const pathname = request.nextUrl.pathname;

  const isPublic = publicRoutes.some(route => pathname.startsWith(route));

  if (!isPublic && !token) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/home", "/agenda", "/atendimento/:path*", "/relatorio/:path*"],
};