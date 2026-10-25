import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
  basePath: "/atendimento",
  output: "standalone",
  turbopack: {
    root: path.join(__dirname),
  },
};

export default nextConfig;
