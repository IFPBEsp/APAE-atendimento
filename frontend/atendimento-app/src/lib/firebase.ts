import { initializeApp, getApps, getApp } from "firebase/app";
import { getAuth } from "firebase/auth";

let appInitialized = false;

export function getFirebaseAuth() {
  if (typeof window === "undefined") {
    throw new Error("Firebase Auth só pode ser usado no client");
  }

  const app = !getApps().length
    ? initializeApp({
        apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY!,
        authDomain: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN!,
        projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID!,
        storageBucket: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET!,
        messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID!,
        appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID!,
      })
    : getApp();

  return getAuth(app);
}