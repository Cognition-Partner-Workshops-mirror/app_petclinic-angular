// Angular 22: migrated from .eslintrc.json to ESLint flat config (ESLint 9+)
const eslint = require("@eslint/js");
const tseslint = require("typescript-eslint");
const angularEslint = require("angular-eslint");

module.exports = tseslint.config(
  {
    files: ["**/*.ts"],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...angularEslint.configs.tsRecommended,
    ],
    processor: angularEslint.processInlineTemplates,
    rules: {
      "@angular-eslint/component-selector": [
        "error",
        {
          prefix: "app",
          style: "kebab-case",
          type: "element",
        },
      ],
      "@angular-eslint/directive-selector": [
        "error",
        {
          prefix: "app",
          style: "camelCase",
          type: "attribute",
        },
      ],
      // Angular 22: @typescript-eslint/quotes removed in v8, using built-in quotes rule
      "quotes": [
        "error",
        "single",
        {
          allowTemplateLiterals: true,
        },
      ],
      "@angular-eslint/no-empty-lifecycle-method": "off",
      // Angular 22: keeping NgModule architecture, standalone migration deferred
      "@angular-eslint/prefer-standalone": "off",
      // Relaxed rules to allow existing patterns during migration
      "@typescript-eslint/no-explicit-any": "off",
      "@typescript-eslint/no-unused-vars": "warn",
    },
  },
  {
    files: ["**/*.html"],
    extends: [
      ...angularEslint.configs.templateRecommended,
    ],
  }
);
