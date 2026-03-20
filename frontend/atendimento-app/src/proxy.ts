import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function proxy(request: NextRequest) {
  const firebaseEnabled = process.env.NEXT_PUBLIC_FIREBASE_ENABLED === "true";

  if (!firebaseEnabled) {
    return NextResponse.next();
  }

  const token = request.cookies.get("token")?.value;

  const publicRoutes = ["/login"];
  const pathname = request.nextUrl.pathname;

  const isPublicRoute = pathname.startsWith("/login");

  if (!isPublicRoute && !token) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  if (isPublicRoute && token) {
    return NextResponse.redirect(new URL("/home", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/home/:path*"],
};