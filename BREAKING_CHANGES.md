# Breaking Changes: Angular 16 to Angular 22 Migration

This document lists every breaking change encountered and addressed during the upgrade from Angular 16.2.1 to Angular 22.0.2.

## 1. Angular Core

### 1.1 Standalone Components Default (`standalone: true`)
- **Change**: Angular 19+ changed the default value of `standalone` in `@Component`, `@Directive`, and `@Pipe` decorators from `false` to `true`.
- **Impact**: All components declared in NgModules failed to compile with `NG6008: Component X is standalone, and cannot be declared in an NgModule`.
- **Fix**: Added `standalone: false` explicitly to all component and directive decorators to preserve NgModule-based architecture.
- **Files affected**: All 22+ component files and 1 directive in `src/app/`.

### 1.2 HttpClientModule Removed
- **Change**: `HttpClientModule` was deprecated in Angular 15 and removed in Angular 22. Replaced by `provideHttpClient()` function.
- **Impact**: `HttpClientModule` import in `app.module.ts` no longer available.
- **Fix**: Replaced `HttpClientModule` in imports with `provideHttpClient()` in providers array.
- **File**: `src/app/app.module.ts`

### 1.3 BrowserAnimationsModule Removed
- **Change**: `BrowserAnimationsModule` was deprecated and replaced by `provideAnimations()` function.
- **Impact**: `BrowserAnimationsModule` import in `app.module.ts` no longer available.
- **Fix**: Replaced `BrowserAnimationsModule` in imports with `provideAnimations()` in providers array.
- **File**: `src/app/app.module.ts`

### 1.4 enableProdMode() Removed
- **Change**: `enableProdMode()` is no longer needed; production mode is now inferred from the build configuration.
- **Impact**: `enableProdMode()` call in `main.ts` is unnecessary.
- **Fix**: Removed `enableProdMode()` import and conditional call from `main.ts`.
- **File**: `src/main.ts`

### 1.5 Component Decorator: `styleUrls` to `styleUrl`
- **Change**: Angular 15+ introduced `styleUrl` (singular string) as a replacement for `styleUrls` (array of strings) when a component has a single stylesheet.
- **Impact**: `styleUrls: ['./component.css']` pattern deprecated.
- **Fix**: Changed all components from `styleUrls: ['./component.css']` to `styleUrl: './component.css'`.
- **Files affected**: All 22 component files.

### 1.6 Constructor Injection to `inject()` Function
- **Change**: Angular 14+ introduced the `inject()` function as the preferred way to inject dependencies, replacing constructor parameter injection.
- **Impact**: Constructor parameter injection deprecated in favor of `inject()`.
- **Fix**: Used Angular's `@angular/core:inject` migration schematic to convert all constructor injections to `inject()` function calls.
- **Files affected**: All services, components, and resolvers.

### 1.7 Template Control Flow: `*ngIf`/`*ngFor` to `@if`/`@for`
- **Change**: Angular 17+ introduced built-in control flow syntax (`@if`, `@for`, `@switch`) replacing structural directives (`*ngIf`, `*ngFor`, `*ngSwitch`).
- **Impact**: Structural directive syntax deprecated.
- **Fix**: Used Angular's `@angular/core:control-flow` migration schematic to convert all templates to built-in control flow.
- **Files affected**: All 19 HTML template files.

### 1.8 polyfills.ts Removed
- **Change**: The `polyfills.ts` file is no longer used. Polyfills are now configured directly in `angular.json` via the `polyfills` array.
- **Impact**: `polyfills.ts` file reference removed from build configuration.
- **Fix**: Deleted `src/polyfills.ts` and configured `zone.js` in `angular.json` polyfills array.
- **Files affected**: `src/polyfills.ts` (deleted), `angular.json`.

### 1.9 test.ts Removed
- **Change**: The `test.ts` bootstrap file for Karma tests is no longer needed with the Angular 22 test runner.
- **Impact**: `test.ts` reference removed.
- **Fix**: Deleted `src/test.ts`.
- **File**: `src/test.ts` (deleted).

## 2. Angular Build System

### 2.1 Application Builder (`@angular-devkit/build-angular:application`)
- **Change**: The `browser` builder was deprecated and replaced by the `application` builder (esbuild-based).
- **Impact**: `angular.json` build configuration needed restructuring. The `main` option was renamed to `browser`.
- **Fix**: Updated `angular.json` to use `@angular-devkit/build-angular:application` builder with `browser` entry point.
- **File**: `angular.json`

### 2.2 Duplicate Asset Resolution (Bootstrap Glyphicons)
- **Change**: The new esbuild-based application builder processes CSS font references differently, causing duplicate output errors when the same font file exists in multiple locations.
- **Impact**: Bootstrap CSS from `node_modules` and `petclinic.css` (in `src/assets/css/`) both referenced `glyphicons-halflings-regular.svg`, causing build failure.
- **Fix**: Removed `node_modules/bootstrap/dist/css/bootstrap.css` from angular.json styles array since `petclinic.css` already includes Bootstrap CSS.
- **File**: `angular.json`

## 3. TypeScript

### 3.1 TypeScript 4.9 to 6.0
- **Change**: TypeScript upgraded from 4.9.5 to 6.0.3. Multiple compiler options deprecated.
- **Impact**: `baseUrl` is deprecated and will stop functioning in TypeScript 7.0.
- **Fix**: Added `"ignoreDeprecations": "6.0"` to `tsconfig.json` to silence deprecation warnings while maintaining backward compatibility.
- **File**: `tsconfig.json`

### 3.2 Module Resolution
- **Change**: TypeScript 6 recommends `"moduleResolution": "bundler"` for Angular projects.
- **Impact**: Previous `"node"` module resolution is deprecated.
- **Fix**: Changed `moduleResolution` to `"bundler"` and `module` to `"ES2022"`.
- **File**: `tsconfig.json`

### 3.3 Non-relative Imports
- **Change**: With `moduleResolution: "bundler"`, non-relative imports using `baseUrl` (e.g., `from 'app/...'`) may not resolve correctly.
- **Impact**: `vet-add.component.ts` used `import {SpecialtyService} from 'app/specialties/specialty.service'`.
- **Fix**: Changed to relative import `from '../../specialties/specialty.service'`.
- **File**: `src/app/vets/vet-add/vet-add.component.ts`

### 3.4 Empty Object Type `{}`
- **Change**: TypeScript/ESLint now flags `{}` as an overly permissive type.
- **Impact**: Service methods using `Observable<{}>` return types flagged as errors.
- **Fix**: Changed `Observable<{}>` to `Observable<object>` in service files; changed `{}` type annotations to `Record<string, unknown>` in test stubs.
- **Files affected**: `src/app/owners/owner.service.ts`, `src/app/testing/router-stubs.ts`

## 4. RxJS

### 4.1 RxJS 6 to RxJS 7
- **Change**: RxJS upgraded from 6.3.1 to 7.8.2.
- **Impact**: `throwError()` no longer accepts a direct value; it requires a factory function.
- **Fix**: Changed `throwError(message)` to `throwError(() => message)`.
- **File**: `src/app/error.service.ts`

## 5. Moment.js Import

### 5.1 Namespace Import to Default Import
- **Change**: With `esModuleInterop: true` in TypeScript config, `import * as moment from 'moment'` is incorrect.
- **Impact**: Namespace import pattern no longer works correctly.
- **Fix**: Changed to `import moment from 'moment'` (default import).
- **Files affected**: `src/app/pets/pet-edit/pet-edit.component.ts`, `src/app/pets/pet-add/pet-add.component.ts`, `src/app/visits/visit-add/visit-add.component.ts`, `src/app/visits/visit-edit/visit-edit.component.ts`

## 6. ESLint

### 6.1 ESLint 8 to ESLint 9 (Flat Config)
- **Change**: ESLint 9 requires flat config format (`eslint.config.js`) instead of legacy `.eslintrc.json`.
- **Impact**: `.eslintrc.json` no longer supported by ESLint 9.
- **Fix**: Created new `eslint.config.js` in flat config format using `angular-eslint` and `typescript-eslint` packages. Deleted `.eslintrc.json`.
- **Files affected**: `.eslintrc.json` (deleted), `eslint.config.js` (created)

### 6.2 `@typescript-eslint/quotes` Rule Removed
- **Change**: The `@typescript-eslint/quotes` rule was removed in `typescript-eslint` v8. Stylistic rules moved to `@stylistic/eslint-plugin`.
- **Impact**: ESLint config referencing this rule fails.
- **Fix**: Replaced with built-in `quotes` rule.
- **File**: `eslint.config.js`

## 7. Testing

### 7.1 Karma Coverage Reporter
- **Change**: `karma-coverage-istanbul-reporter` replaced by `karma-coverage` in Angular 22.
- **Impact**: Karma config references non-existent plugin.
- **Fix**: Updated `karma.conf.js` to use `karma-coverage` instead of `karma-coverage-istanbul-reporter`.
- **File**: `karma.conf.js`

### 7.2 Protractor Removed
- **Change**: Protractor was deprecated and removed. The `e2e` directory and protractor config are no longer supported.
- **Impact**: E2E test infrastructure removed.
- **Fix**: Deleted `e2e/` directory and removed `e2e` script from `package.json`.
- **Files affected**: `e2e/` (deleted), `package.json`

## 8. Dependencies Updated

| Package | Old Version | New Version |
|---------|------------|-------------|
| `@angular/*` | 16.2.1 | 22.0.2 |
| `@angular/cli` | 16.2.1 | 22.0.3 |
| `@angular-devkit/build-angular` | 16.2.1 | 22.0.3 |
| `typescript` | 4.9.5 | 6.0.3 |
| `rxjs` | 6.3.1 | 7.8.2 |
| `zone.js` | 0.13.1 | 0.16.2 |
| `eslint` | 8.x | 9.18.0 |
| `@angular-eslint/*` | 16.x | 22.0.0 |
| `@typescript-eslint/*` | 5.x | 8.20.0 |
| `jasmine-core` | 4.x | 5.5.0 |
| `karma-jasmine` | 4.x | 5.1.0 |
| `karma-jasmine-html-reporter` | 1.x | 2.1.0 |
| `bootstrap` | 3.3.7 | 3.4.1 |

### Packages Removed
- `core-js` - No longer needed as polyfills
- `protractor` - Deprecated E2E framework
- `codelyzer` - Replaced by `@angular-eslint`
- `@types/jasminewd2` - Protractor types
- `karma-coverage-istanbul-reporter` - Replaced by `karma-coverage`

## 9. Node.js Requirement
- **Change**: Angular 22 requires Node.js `^22.22.3 || ^24.15.0 || >=26.0.0`.
- **Impact**: Previous Node.js 18.x or earlier versions are no longer supported.
- **Fix**: Updated development environment to Node.js v22.22.3.
