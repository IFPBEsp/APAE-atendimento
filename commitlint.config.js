export default {
  extends: ["@commitlint/config-conventional"],

  rules: {
    /*
    TIPOS PERMITIDOS
    */
    "type-enum": [
      2,
      "always",
      [
        "feat",
        "fix",
        "chore",
        "refactor",
        "style",
        "docs",
        "test",
        "perf",
        "ci",
        "build",
        "revert",
        "cleanup",
        "remove",
      ],
    ],

    /*
    ESCOPO OBRIGATÓRIO
    */
    "scope-empty": [2, "never"],

    /*
    DESCRIÇÃO OBRIGATÓRIA
    */
    "subject-empty": [2, "never"],

    /*
    DESCRIÇÃO MÍNIMA
    */
    "subject-min-length": [2, "always", 10],

    /*
    LOWERCASE
    */
    "subject-case": [2, "always", ["lower-case"]],

    /*
    BLOQUEAR PONTO FINAL
    */
    "subject-full-stop": [2, "never", "."],
  },
};
