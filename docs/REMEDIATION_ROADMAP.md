# Remediation Roadmap — Spring PetClinic Angular

> **Generated:** 2026-05-21 | **Based on:** [GAP_ANALYSIS.md](./GAP_ANALYSIS.md)

---

## Phase 1 — Quick Wins (High Severity / Small Effort)

These items can be completed in a single session each and address critical or high-severity issues.

---

### 1.1 Fix Copy-Paste Service Name Bug in Error Handler Calls

**Gap:** 1.4 | **Severity:** High | **Effort:** Small

Fix all five services that incorrectly pass `'OwnerService'` to `createHandleError()`.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, fix the copy-paste bug where `VetService`, `PetService`, `VisitService`, `PetTypeService`, and `SpecialtyService` all pass `'OwnerService'` as the service name to `httpErrorHandler.createHandleError()`. Each service should pass its own class name instead (e.g., `'VetService'`, `'PetService'`, etc.). Files to change: `src/app/vets/vet.service.ts`, `src/app/pets/pet.service.ts`, `src/app/visits/visit.service.ts`, `src/app/pettypes/pettype.service.ts`, `src/app/specialties/specialty.service.ts`. Run `npm run lint` and `npm run test-headless` after.

---

### 1.2 Fix Hardcoded localhost in Production Environment Config

**Gap:** 4.1 | **Severity:** Critical | **Effort:** Small

The production environment file uses `http://localhost:9966/petclinic/api/` which will fail in any real deployment.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, update `src/environments/environment.prod.ts` to use a configurable production API URL. Change `REST_API_URL` to `'/petclinic/api/'` (relative URL) so it works behind a reverse proxy. Also update the URL scheme to support HTTPS. Add a comment explaining the expected deployment configuration. Run `npm run build` to verify the change.

---

### 1.3 Add Global HTTP Interceptor for Error Handling

**Gap:** 2.3 | **Severity:** High | **Effort:** Small

Create a centralized `HttpInterceptor` to handle errors globally instead of per-service `catchError`.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, create a new file `src/app/http-error.interceptor.ts` implementing `HttpInterceptor`. It should catch HTTP errors, log them with the correct format (service name can be derived from URL), and display them via a shared notification mechanism. Register it in `AppModule` providers using `HTTP_INTERCEPTORS` multi-provider. Keep the existing per-service `catchError` for now but update it to not duplicate error logging. Run lint and tests after.

---

### 1.4 Add Retry Logic for GET Requests

**Gap:** 7.1 | **Severity:** High | **Effort:** Small

Add RxJS `retry` operator to idempotent GET requests to handle transient network failures.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, add retry logic to all HTTP GET calls in the services. Use the RxJS `retry(2)` operator (retry up to 2 times with a short delay) in the HTTP interceptor or in each service's GET methods. Only retry GET requests, not POST/PUT/DELETE. Verify with `npm run test-headless` and `npm run lint`.

---

### 1.5 Fix Inconsistent ID Parameter Types

**Gap:** 5.2 | **Severity:** Medium | **Effort:** Small

Standardize all service method signatures to use `number` for entity IDs.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, standardize all service method ID parameters to use `number` type. Currently some methods use `string` (e.g., `deleteOwner(ownerId: string)`, `getVetById(vetId: string)`) while others use `number`. Change all to `number` and convert to string only when building URL strings. Update all callers accordingly. Run `npm run lint` and `npm run test-headless`.

---

### 1.6 Add Request Timeouts

**Gap:** 7.2 | **Severity:** Medium | **Effort:** Small

Add timeouts to HTTP requests so the UI doesn't hang indefinitely on a slow backend.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, add a `timeout(30000)` RxJS operator to all HTTP calls (or add it centrally in an HTTP interceptor). Import `timeout` from `rxjs/operators`. Configure it to throw a `TimeoutError` after 30 seconds. Ensure the error handler catches `TimeoutError` and displays a user-friendly message. Run lint and tests after.

---

### 1.7 Fix HTTPS for API Communication

**Gap:** 4.4 | **Severity:** High | **Effort:** Small

Update the API URL scheme in environment files to support HTTPS.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, update `src/environments/environment.ts` to use `http://localhost:9966/petclinic/api/` (acceptable for local dev) and update `src/environments/environment.prod.ts` to use a relative URL `'/petclinic/api/'` so the browser's current protocol (HTTPS in production) is used. This avoids mixed-content issues. Run build to verify.

---

### 1.8 Fix Deprecated `TestBed.get()` Calls

**Gap:** 3.5 | **Severity:** Medium | **Effort:** Small

Replace deprecated `TestBed.get()` with `TestBed.inject()` across all spec files.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, find and replace all occurrences of `TestBed.get(` with `TestBed.inject(` across all `.spec.ts` files. Ensure the type parameter is preserved. Run `npm run test-headless` to verify all tests still pass.

---

### 1.9 Add Loading State Indicators

**Gap:** 5.3 | **Severity:** Medium | **Effort:** Small

Add loading state to all list and detail components.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, add `isLoading: boolean` property to all list and detail components (following the pattern in `OwnerListComponent.isOwnersDataReceived`). Set it to `true` before data fetch and `false` in `finalize()`. Add a loading spinner or "Loading..." text to each component's template, shown when `isLoading` is `true`. Run lint and tests after.

---

### 1.10 Enforce Test Coverage Threshold

**Gap:** 3.3 | **Severity:** Medium | **Effort:** Small

Configure Karma to fail if code coverage drops below a minimum threshold.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, update `karma.conf.js` to add coverage thresholds. In the `coverageIstanbulReporter` config, add `thresholds: { emitWarning: true, global: { statements: 50, branches: 30, functions: 50, lines: 50 } }`. Also add `'coverage-istanbul'` to the reporters array. Run `npm run test-headless` to verify.

---

## Phase 2 — Important (High Severity / Medium Effort)

These items require more significant changes but are important for production readiness.

---

### 2.1 Fix Silent Error Swallowing and Add Notification Service

**Gap:** 2.1, 2.2 | **Severity:** Critical + High | **Effort:** Medium

Create a shared notification service and ensure all errors are visible to users.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, create a `NotificationService` in `src/app/shared/notification.service.ts` that uses Angular Material `MatSnackBar` to display success/error/warning messages. Update `HttpErrorHandler` to call this service instead of only logging to console. Update all component error handlers to use the notification service. Create a `SharedModule` to export common functionality. Register `MatSnackBarModule` in `AppModule`. Run lint and tests after.

---

### 2.2 Replace Nested Observable Anti-Pattern with RxJS Operators

**Gap:** 5.5 | **Severity:** Medium | **Effort:** Small

Refactor nested `subscribe()` calls using `switchMap` and `forkJoin`.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, refactor `VisitAddComponent.ngOnInit()` and `VisitEditComponent.ngOnInit()` to use RxJS `switchMap` instead of nested `subscribe()` calls. For `VisitEditComponent`, chain `getVisitById → getPetById → getOwnerById` using `switchMap`. For `VisitAddComponent`, chain `getPetById → getOwnerById` using `switchMap`. Handle errors in a single `.subscribe(error)` callback. Run lint and tests after.

---

### 2.3 Implement Lazy Loading for Feature Modules

**Gap:** 1.3 | **Severity:** Medium | **Effort:** Medium

Convert eagerly loaded feature modules to lazy-loaded routes.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, convert all feature modules (OwnersModule, PetsModule, VisitsModule, VetsModule, PetTypesModule, SpecialtiesModule) to lazy-loaded modules. Update `app-routing.module.ts` to use `loadChildren: () => import('./owners/owners.module').then(m => m.OwnersModule)` for each feature. Remove the direct module imports from `AppModule`. Each feature's routing module should change from `forChild` to `forRoot` equivalent or keep `forChild` with proper child route configuration. Run build and tests after.

---

### 2.4 Add Pagination Support to List Components

**Gap:** 5.1 | **Severity:** High | **Effort:** Medium

Add client-side pagination (and optionally server-side) to all list views.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, add pagination to all list components (OwnerListComponent, PetListComponent, VetListComponent, VisitListComponent, PettypeListComponent, SpecialtyListComponent). Use Angular Material `MatPaginator` component. Add `pageSize`, `pageIndex`, and `totalItems` properties to each list component. Implement client-side pagination first by slicing the array. Add the paginator to each list template. Run lint and tests after.

---

### 2.5 Replace Broken Protractor E2E Suite with Cypress or Playwright

**Gap:** 3.2 | **Severity:** High | **Effort:** Medium

Replace the deprecated Protractor with a modern E2E testing framework.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, replace the deprecated Protractor E2E test setup with Cypress. Remove `protractor` from `devDependencies` and `protractor.conf.js`. Install `cypress` and `@cypress/schematic`. Create basic E2E tests in `cypress/e2e/` that verify: (1) home page loads, (2) navigate to owners list, (3) navigate to vets list, (4) navigate to pet types. Update `package.json` scripts to add `"e2e": "cypress run"`. Remove the `e2e/` directory.

---

### 2.6 Add Structured Logging

**Gap:** 6.1 | **Severity:** Medium | **Effort:** Medium

Replace ad-hoc `console.log` with a structured logging service.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, create a `LoggerService` in `src/app/shared/logger.service.ts` with methods `debug()`, `info()`, `warn()`, `error()` that output structured JSON logs with timestamp, level, component name, and message. Replace all `console.log` and `console.error` calls throughout the codebase with the appropriate `LoggerService` method. Make `LoggerService` configurable via environment (e.g., `logLevel: 'debug'` in dev, `'warn'` in prod). Run lint and tests after.

---

### 2.7 Add Graceful Backend Unavailability Handling

**Gap:** 7.3 | **Severity:** Medium | **Effort:** Medium

Show user-friendly messages when the backend API is unreachable.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, update the `HttpErrorHandler` to detect when the backend is completely unreachable (status code 0, or network error). Create a `ServiceUnavailableComponent` that displays a user-friendly "Service is temporarily unavailable" message with a retry button. In the HTTP interceptor, check for `status === 0` or `status >= 500` and either show a global banner or redirect to the service unavailable page. Run lint and tests.

---

## Phase 3 — Polish (Lower Severity / Longer-Term)

These items improve overall quality but are lower priority.

---

### 3.1 Create SharedModule and CoreModule

**Gap:** 1.2 | **Severity:** Medium | **Effort:** Small

Consolidate shared and singleton services into proper modules.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, create `src/app/core/core.module.ts` for singleton services (`HttpErrorHandler`, future `NotificationService`, `LoggerService`) and `src/app/shared/shared.module.ts` for shared components, directives, and pipes. Move `HttpErrorHandler` provider from `AppModule` to `CoreModule`. Import `CoreModule` in `AppModule` only. Import `SharedModule` in each feature module that needs shared components. Run lint and tests after.

---

### 3.2 Standardize Form Approach (Template-Driven vs Reactive)

**Gap:** 1.5 | **Severity:** Low | **Effort:** Small

Pick one form approach and use it consistently across the app.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, standardize all forms to use Reactive Forms (currently only `VetEditComponent` uses them). Convert `OwnerAddComponent`, `OwnerEditComponent`, `PetAddComponent`, `PetEditComponent`, `VisitAddComponent`, `VisitEditComponent`, `PettypeAddComponent`, `PettypeEditComponent`, `SpecialtyEditComponent` to use `FormBuilder` with `FormGroup` and `Validators`. Add proper validation (required fields, minLength, pattern for telephone). Run lint and tests after.

---

### 3.3 Add Authentication and Route Guards

**Gap:** 4.2 | **Severity:** High | **Effort:** Large

Implement authentication flow and protect routes.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, implement JWT-based authentication. Create `src/app/auth/auth.module.ts` with `LoginComponent`, `AuthService`, `AuthGuard`, and `AuthInterceptor`. The `AuthService` should handle login/logout and store JWT in `localStorage`. The `AuthGuard` should implement `CanActivate` and redirect to login if not authenticated. The `AuthInterceptor` should add the `Authorization: Bearer <token>` header to all API requests. Protect all routes except `/welcome` and `/login` with `AuthGuard`. Run lint and tests after.

---

### 3.4 Replace Bootstrap 3 + jQuery with Angular Material / Modern CSS

**Gap:** 4.5 | **Severity:** Medium | **Effort:** Large

Migrate from legacy Bootstrap 3 + jQuery to Angular Material or Bootstrap 5.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, migrate the UI from Bootstrap 3 + jQuery + Tether to Angular Material components. Remove `bootstrap`, `jquery`, and `tether` from `package.json`. Remove their script/style entries from `angular.json`. Replace Bootstrap grid with CSS Grid or Flexbox. Replace Bootstrap navbar with `mat-toolbar` and `mat-menu`. Replace Bootstrap tables with `mat-table`. Replace Bootstrap forms with `mat-form-field`. Update all component templates. Run build, lint, and tests after.

---

### 3.5 Add Integration Tests with HttpClientTestingModule

**Gap:** 3.1 | **Severity:** High | **Effort:** Large

Add integration-level tests that verify component-service interaction.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, add integration tests for key user flows. Create `src/app/owners/owners-integration.spec.ts` that tests: (1) OwnerListComponent loads and displays owners from service, (2) OwnerAddComponent submits and navigates, (3) OwnerDetailComponent loads pet data. Use `HttpClientTestingModule` to mock backend responses. Create similar integration tests for Vets and Visits modules. Ensure all tests pass with `npm run test-headless`.

---

### 3.6 Add Error Tracking Service Integration (Sentry)

**Gap:** 6.4 | **Severity:** Medium | **Effort:** Medium

Integrate a production error tracking service.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, integrate Sentry for error tracking. Install `@sentry/angular-ivy`. Create `src/app/sentry.config.ts` that initializes Sentry with DSN from environment config. Add `Sentry.createErrorHandler()` as Angular's `ErrorHandler` provider in `AppModule`. Add `Sentry.TraceService` for performance monitoring. Configure source map uploading in the build process. Add `SENTRY_DSN` to both environment files (empty in dev, populated in prod). Run lint and tests after.

---

### 3.7 Add Performance Monitoring (Web Vitals)

**Gap:** 6.3 | **Severity:** Medium | **Effort:** Medium

Track Core Web Vitals and Angular-specific performance metrics.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, add performance monitoring. Install `web-vitals` library. Create `src/app/shared/performance.service.ts` that measures and reports LCP, FID, CLS, and TTFB. Use Angular's `NavigationEnd` router events to track page-level load times. Add HTTP interceptor timing to track API response times. Log metrics to console in dev and send to an analytics endpoint in prod. Run lint and tests after.

---

### 3.8 Move Test Utilities Out of Source Tree

**Gap:** 3.4 | **Severity:** Low | **Effort:** Small

Relocate test helpers so they cannot accidentally be bundled in production.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, move `src/app/testing/` to a top-level `testing/` directory. Update all import paths in `.spec.ts` files that reference `../../testing/` or `../testing/`. Update `tsconfig.spec.json` to include the new `testing/` path. Ensure `tsconfig.app.json` excludes `testing/`. Run `npm run test-headless` and `npm run build` to verify.

---

### 3.9 Add Circuit Breaker Pattern

**Gap:** 7.4 | **Severity:** Low | **Effort:** Medium

Prevent cascading failures when the backend is down.

**Devin Prompt:**
> In the repo `app-petclinic-angular`, implement a simple circuit breaker in the HTTP interceptor. Track consecutive failures per service endpoint. After 3 consecutive failures, "open" the circuit for 30 seconds and immediately return a cached response or error without making the HTTP call. After the timeout, allow one "half-open" request through. If it succeeds, close the circuit. Store circuit state in a `CircuitBreakerService`. Run lint and tests after.

---

## Priority Matrix

| Phase | Item | Severity | Effort | Gap Ref |
|---|---|---|---|---|
| **1** | Fix service name bug | High | Small | 1.4 |
| **1** | Fix prod environment URL | Critical | Small | 4.1 |
| **1** | Add HTTP interceptor | High | Small | 2.3 |
| **1** | Add retry logic | High | Small | 7.1 |
| **1** | Fix inconsistent ID types | Medium | Small | 5.2 |
| **1** | Add request timeouts | Medium | Small | 7.2 |
| **1** | Fix HTTPS for API | High | Small | 4.4 |
| **1** | Fix deprecated TestBed.get() | Medium | Small | 3.5 |
| **1** | Add loading indicators | Medium | Small | 5.3 |
| **1** | Enforce coverage threshold | Medium | Small | 3.3 |
| **2** | Notification service + error fix | Critical/High | Medium | 2.1, 2.2 |
| **2** | Refactor nested subscribes | Medium | Small | 5.5 |
| **2** | Lazy loading | Medium | Medium | 1.3 |
| **2** | Pagination | High | Medium | 5.1 |
| **2** | Replace Protractor with Cypress | High | Medium | 3.2 |
| **2** | Structured logging | Medium | Medium | 6.1 |
| **2** | Graceful degradation | Medium | Medium | 7.3 |
| **3** | SharedModule / CoreModule | Medium | Small | 1.2 |
| **3** | Standardize forms | Low | Small | 1.5 |
| **3** | Authentication & guards | High | Large | 4.2 |
| **3** | Replace Bootstrap 3 / jQuery | Medium | Large | 4.5 |
| **3** | Integration tests | High | Large | 3.1 |
| **3** | Sentry integration | Medium | Medium | 6.4 |
| **3** | Web Vitals monitoring | Medium | Medium | 6.3 |
| **3** | Move test utilities | Low | Small | 3.4 |
| **3** | Circuit breaker | Low | Medium | 7.4 |
