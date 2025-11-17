import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  const verified = request.cookies.get("verified")?.value;

  const publicRoutes = ["/login"];
  const pathname = request.nextUrl.pathname;

  const isPublic = publicRoutes.includes(pathname);

  if (!isPublic && !verified) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/home", "/login/verificacao"],
};
