import {
  GoogleAuthProvider,
  signInWithPopup,
  sendSignInLinkToEmail,
  signInWithEmailLink,
  isSignInWithEmailLink,
} from "firebase/auth";

import { getFirebaseAuth } from "@/lib/firebase";

const actionCodeSettings = {
  url: `${process.env.NEXT_PUBLIC_APP_URL}/login/verificacao`,
  handleCodeInApp: true,
};

export async function loginWithGoogle() {
  const auth = getFirebaseAuth();
  const provider = new GoogleAuthProvider();

  const cred = await signInWithPopup(auth, provider);
  return await cred.user.getIdToken();
}

export async function sendMagicLink(email: string) {
  const auth = getFirebaseAuth();

  await sendSignInLinkToEmail(auth, email, actionCodeSettings);
  window.localStorage.setItem("emailForSignIn", email);
}

export async function confirmMagicLink() {
  if (typeof window === "undefined") return null;

  const auth = getFirebaseAuth();
  const url = window.location.href;

  if (!isSignInWithEmailLink(auth, url)) {
    return null;
  }

  const email = localStorage.getItem("emailForSignIn");
  if (!email) return null;

  const result = await signInWithEmailLink(auth, email, url);

  localStorage.removeItem("emailForSignIn");

  return await result.user.getIdToken();
}