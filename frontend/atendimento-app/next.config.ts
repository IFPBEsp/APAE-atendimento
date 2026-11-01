import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
  basePath: "/atendimento",
  output: "standalone",
  turbopack: {
    root: path.join(__dirname),
  },
  async rewrites() {
    const backendUrl = (
      process.env.BACKEND_URL ||
      `http://localhost:${process.env.SERVER_PORT || "8080"}`
    ).replace(/\/+$/, "");

    return [
      {
        source: "/api/:path*",
        destination: `${backendUrl}/atendimento/:path*`,
      },
    ];
  },
};

export default nextConfig;
