import {
  getAuth,
  GoogleAuthProvider,
  signInWithPopup,
  sendSignInLinkToEmail,
  signInWithEmailLink,
  isSignInWithEmailLink
} from "firebase/auth";

import { auth } from "@/lib/firebase";

const actionCodeSettings = {
  url: "http://localhost:3000/login/verificacao",
  handleCodeInApp: true,
};

/*export async function loginWithGoogle() {
  const provider = new GoogleAuthProvider();
  const cred = await signInWithPopup(auth, provider);
  return await cred.user.getIdToken();
}*/

export async function loginWithGoogle() {
  const provider = new GoogleAuthProvider();

  const cred = await signInWithPopup(auth, provider);

  console.log("Usuário Google:", cred.user);

  const token = await cred.user.getIdToken();

  console.log("ID TOKEN:", token);

  return token;
}

export async function sendMagicLink(email: string) {
  await sendSignInLinkToEmail(auth, email, actionCodeSettings);
  window.localStorage.setItem("emailForSignIn", email);
}

export async function confirmMagicLink() {
  const auth = getAuth();
  const url = window.location.href;

  if (!isSignInWithEmailLink(auth, url)) {
    return null;
  }

  const email = localStorage.getItem("emailForSignIn");

  if (!email) {
    throw new Error("Email não encontrado");
  }

  const result = await signInWithEmailLink(auth, email, url);

  localStorage.removeItem("emailForSignIn");

  return await result.user.getIdToken();
}
