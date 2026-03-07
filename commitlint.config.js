module.exports = {
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
    DESCRIÇÃO OBRIGATÓRIA
    */
    "subject-empty": [2, "never"],
  },
};
