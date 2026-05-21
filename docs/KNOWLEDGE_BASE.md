# Architecture Knowledge Base — Spring PetClinic Angular

> **Generated:** 2026-05-21 | **Angular:** 16.2.1 | **License:** Apache 2.0

---

## 1. Architecture Overview

### 1.1 Application Type

Spring PetClinic Angular is a **client-only single-page application (SPA)** built with Angular 16. It serves as the frontend for the [Spring PetClinic REST API](https://github.com/spring-petclinic/spring-petclinic-rest) backend (`http://localhost:9966/petclinic/api/`). It does not contain any server-side logic or data persistence of its own.

### 1.2 High-Level Component Diagram

```
┌─────────────────────────────────────────────────────┐
│                  Browser (SPA)                      │
│                                                     │
│  ┌────────────┐  ┌───────────┐  ┌───────────────┐  │
│  │  Owners    │  │   Pets    │  │   Visits      │  │
│  │  Module    │  │   Module  │  │   Module       │  │
│  ├────────────┤  ├───────────┤  ├───────────────┤  │
│  │  Vets      │  │ PetTypes  │  │ Specialties   │  │
│  │  Module    │  │  Module   │  │   Module       │  │
│  └─────┬──────┘  └─────┬─────┘  └──────┬────────┘  │
│        │               │               │            │
│        └───────────┬───┴───────────────┘            │
│                    │                                │
│           HttpErrorHandler (error.service.ts)       │
│                    │                                │
│              HttpClient                             │
└────────────────────┼────────────────────────────────┘
                     │ REST (JSON over HTTP)
                     ▼
          ┌──────────────────────┐
          │  Spring PetClinic    │
          │  REST Backend        │
          │  :9966/petclinic/api │
          └──────────────────────┘
```

### 1.3 Module Structure

The application follows Angular's **feature-module** pattern. Each domain entity has its own NgModule with dedicated components, a service, and a routing module:

| Module | Path | Responsibility |
|---|---|---|
| `AppModule` | `src/app/app.module.ts` | Root module; imports all feature modules |
| `OwnersModule` | `src/app/owners/` | Owner CRUD and search |
| `PetsModule` | `src/app/pets/` | Pet CRUD, nested under owners |
| `VisitsModule` | `src/app/visits/` | Visit (appointment) CRUD |
| `VetsModule` | `src/app/vets/` | Veterinarian CRUD with specialty assignment |
| `PetTypesModule` | `src/app/pettypes/` | Pet type reference data management |
| `SpecialtiesModule` | `src/app/specialties/` | Vet specialty reference data management |
| `PartsModule` | `src/app/parts/` | Shared layout: Welcome page, 404 page |
| `AppRoutingModule` | `src/app/app-routing.module.ts` | Root-level routes (welcome, wildcard) |
| `TestingModule` | `src/app/testing/` | Router stubs & dummy component for tests |

### 1.4 Communication Pattern

All backend communication is via **Angular `HttpClient`** making REST calls to the Spring PetClinic REST API. Every service uses the centralized `HttpErrorHandler` (`src/app/error.service.ts`) for error interception.

### 1.5 State Management

There is **no dedicated state management library** (e.g., NgRx, Akita). Component state is managed locally via component properties, and data is fetched fresh from the backend on each navigation.

---

## 2. Data Models

All models are defined as TypeScript `interface` types (no classes).

| Entity | File | Fields | Relationships |
|---|---|---|---|
| **Owner** | `src/app/owners/owner.ts` | `id`, `firstName`, `lastName`, `address`, `city`, `telephone`, `pets` | Has-many `Pet[]` |
| **Pet** | `src/app/pets/pet.ts` | `id`, `ownerId`, `name`, `birthDate`, `type`, `owner`, `visits` | Belongs-to `Owner`, has-one `PetType`, has-many `Visit[]` |
| **Visit** | `src/app/visits/visit.ts` | `id`, `date`, `description`, `pet`, `petId?` | Belongs-to `Pet` |
| **Vet** | `src/app/vets/vet.ts` | `id`, `firstName`, `lastName`, `specialties` | Has-many `Specialty[]` |
| **PetType** | `src/app/pettypes/pettype.ts` | `id`, `name` | Referenced by `Pet.type` |
| **Specialty** | `src/app/specialties/specialty.ts` | `id`, `name` | Referenced by `Vet.specialties[]` |

### Entity Relationship Diagram

```
Owner 1──* Pet *──1 PetType
                 │
                 1──* Visit

Vet *──* Specialty
```

---

## 3. API Surface Map

All services construct URLs from `environment.REST_API_URL` (`http://localhost:9966/petclinic/api/`).

### 3.1 OwnerService (`src/app/owners/owner.service.ts`)

| Method | HTTP | URL Pattern | Request Body | Response |
|---|---|---|---|---|
| `getOwners()` | GET | `/api/owners` | — | `Owner[]` |
| `getOwnerById(id)` | GET | `/api/owners/{id}` | — | `Owner` |
| `addOwner(owner)` | POST | `/api/owners` | `Owner` | `Owner` |
| `updateOwner(id, owner)` | PUT | `/api/owners/{id}` | `Owner` | `Owner` |
| `deleteOwner(id)` | DELETE | `/api/owners/{id}` | — | `Owner` |
| `searchOwners(lastName)` | GET | `/api/owners?lastName={name}` | — | `Owner[]` |

### 3.2 PetService (`src/app/pets/pet.service.ts`)

| Method | HTTP | URL Pattern | Request Body | Response |
|---|---|---|---|---|
| `getPets()` | GET | `/api/pets` | — | `Pet[]` |
| `getPetById(id)` | GET | `/api/pets/{id}` | — | `Pet` |
| `addPet(pet)` | POST | `/api/owners/{ownerId}/pets` | `Pet` | `Pet` |
| `updatePet(id, pet)` | PUT | `/api/pets/{id}` | `Pet` | `Pet` |
| `deletePet(id)` | DELETE | `/api/pets/{id}` | — | `number` |

### 3.3 VisitService (`src/app/visits/visit.service.ts`)

| Method | HTTP | URL Pattern | Request Body | Response |
|---|---|---|---|---|
| `getVisits()` | GET | `/api/visits` | — | `Visit[]` |
| `getVisitById(id)` | GET | `/api/visits/{id}` | — | `Visit` |
| `addVisit(visit)` | POST | `/api/owners/{ownerId}/pets/{petId}/visits` | `Visit` | `Visit` |
| `updateVisit(id, visit)` | PUT | `/api/visits/{id}` | `Visit` | `Visit` |
| `deleteVisit(id)` | DELETE | `/api/visits/{id}` | — | `number` |

### 3.4 VetService (`src/app/vets/vet.service.ts`)

| Method | HTTP | URL Pattern | Request Body | Response |
|---|---|---|---|---|
| `getVets()` | GET | `/api/vets` | — | `Vet[]` |
| `getVetById(id)` | GET | `/api/vets/{id}` | — | `Vet` |
| `addVet(vet)` | POST | `/api/vets` | `Vet` | `Vet` |
| `updateVet(id, vet)` | PUT | `/api/vets/{id}` | `Vet` | `Vet` |
| `deleteVet(id)` | DELETE | `/api/vets/{id}` | — | `number` |

### 3.5 PetTypeService (`src/app/pettypes/pettype.service.ts`)

| Method | HTTP | URL Pattern | Request Body | Response |
|---|---|---|---|---|
| `getPetTypes()` | GET | `/api/pettypes` | — | `PetType[]` |
| `getPetTypeById(id)` | GET | `/api/pettypes/{id}` | — | `PetType` |
| `addPetType(petType)` | POST | `/api/pettypes` | `PetType` | `PetType` |
| `updatePetType(id, petType)` | PUT | `/api/pettypes/{id}` | `PetType` | `PetType` |
| `deletePetType(id)` | DELETE | `/api/pettypes/{id}` | — | `number` |

### 3.6 SpecialtyService (`src/app/specialties/specialty.service.ts`)

| Method | HTTP | URL Pattern | Request Body | Response |
|---|---|---|---|---|
| `getSpecialties()` | GET | `/api/specialties` | — | `Specialty[]` |
| `getSpecialtyById(id)` | GET | `/api/specialties/{id}` | — | `Specialty` |
| `addSpecialty(specialty)` | POST | `/api/specialties` | `Specialty` | `Specialty` |
| `updateSpecialty(id, specialty)` | PUT | `/api/specialties/{id}` | `Specialty` | `Specialty` |
| `deleteSpecialty(id)` | DELETE | `/api/specialties/{id}` | — | `number` |

---

## 4. Route Map

### 4.1 Root Routes (`app-routing.module.ts`)

| Path | Component | Notes |
|---|---|---|
| `/welcome` | `WelcomeComponent` | Landing page |
| `/` (empty) | `WelcomeComponent` | Default redirect |
| `/**` (wildcard) | `PageNotFoundComponent` | 404 fallback |

### 4.2 Owner Routes (`owners-routing.module.ts`)

| Path | Component |
|---|---|
| `/owners` | `OwnerListComponent` |
| `/owners/add` | `OwnerAddComponent` |
| `/owners/:id` | `OwnerDetailComponent` |
| `/owners/:id/edit` | `OwnerEditComponent` |
| `/owners/:id/pets/add` | `PetAddComponent` |

### 4.3 Pet Routes (`pets-routing.module.ts`)

| Path | Component |
|---|---|
| `/pets` | `PetListComponent` |
| `/pets/add` | `PetAddComponent` |
| `/pets/:id/edit` | `PetEditComponent` |
| `/pets/:id/visits/add` | `VisitAddComponent` |

### 4.4 Visit Routes (`visits-routing.module.ts`)

| Path | Component |
|---|---|
| `/visits` | `VisitListComponent` |
| `/visits/add` | `VisitAddComponent` |
| `/visits/:id/edit` | `VisitEditComponent` |

### 4.5 Vet Routes (`vets-routing.module.ts`)

| Path | Component | Resolvers |
|---|---|---|
| `/vets` | `VetListComponent` | — |
| `/vets/add` | `VetAddComponent` | — |
| `/vets/:id/edit` | `VetEditComponent` | `VetResolver`, `SpecResolver` |

### 4.6 PetType Routes (`pettypes-routing.module.ts`)

| Path | Component |
|---|---|
| `/pettypes` | `PettypeListComponent` |
| `/pettypes/add` | `PettypeAddComponent` |
| `/pettypes/:id/edit` | `PettypeEditComponent` |

### 4.7 Specialty Routes (`specialties-routing.module.ts`)

| Path | Component |
|---|---|
| `/specialties` | `SpecialtyListComponent` |
| `/specialties/:id/edit` | `SpecialtyEditComponent` |

> **Note:** Specialty `add` route is commented out in the routing module.

---

## 5. Business Logic Inventory

### 5.1 Owner Search
- `OwnerListComponent.searchByLastName()` — performs client-side branching: if search term is empty, fetches all owners; otherwise calls `searchOwners(lastName)` for server-side filtering.

### 5.2 Visit Date Formatting
- `VisitAddComponent.onSubmit()` and `VisitEditComponent.onSubmit()` — use `moment.js` to format the visit date to `YYYY-MM-DD` (RFC 3339) before sending to the backend.

### 5.3 Vet Specialty Assignment
- `VetEditComponent` — uses Angular Reactive Forms with `FormBuilder` and `Validators`. Loads specialties via `SpecResolver` route resolver before component initialization. Uses `compareSpecFn` for mat-select comparison.

### 5.4 Pet Creation Under Owner Context
- `PetService.addPet()` — constructs the URL as `/owners/{ownerId}/pets` (nested REST resource).
- `VisitService.addVisit()` — constructs the URL as `/owners/{ownerId}/pets/{petId}/visits`.

### 5.5 Route Resolvers
- `VetResolver` — pre-fetches a vet by ID before navigating to the edit component.
- `SpecResolver` — pre-fetches all specialties before navigating to the vet edit component.

---

## 6. Error Handling

### 6.1 Centralized Error Service (`src/app/error.service.ts`)

- `HttpErrorHandler` is an `@Injectable` service provided at the root module level.
- `createHandleError(serviceName)` returns a curried function for service-specific error handling.
- The handler distinguishes between client-side `ErrorEvent` and server error responses.
- Parses the `errors` HTTP header for Spring MVC validation error messages.
- Logs the full error to `console.error`.
- Re-throws the error as an RxJS `throwError` observable.

### 6.2 Component-Level Error Handling
- All components store errors in a local `errorMessage: string` property.
- Error messages are displayed in templates (no toast/snackbar notifications).

### 6.3 Bug: Incorrect Service Names in Error Handler
- `VetService`, `PetService`, `VisitService`, `PetTypeService`, and `SpecialtyService` all pass `'OwnerService'` as the service name to `httpErrorHandler.createHandleError()`. This is a copy-paste bug that results in misleading error logs.

---

## 7. Integration Points

| Integration | Technology | Details |
|---|---|---|
| **REST Backend** | HTTP/JSON | `http://localhost:9966/petclinic/api/` (dev & prod) |
| **Date Handling** | Moment.js | Date formatting for visit date fields |
| **UI Framework** | Bootstrap 3 + jQuery + Tether | Global CSS and JS loaded via `angular.json` scripts/styles |
| **Angular Material** | `@angular/material` 16.2.1 | Used for `mat-datepicker` in visit forms |
| **Angular CDK** | `@angular/cdk` 16.2.1 | Material dependency |

---

## 8. Build & Deployment Summary

### 8.1 Build Commands

| Command | Description |
|---|---|
| `npm start` / `ng serve` | Development server on `http://localhost:4200` |
| `npm run build` / `ng build` | Development build → `dist/` |
| `ng build --prod` | Production build with AOT, optimization, output hashing |
| `npm run lint` | ESLint with `@angular-eslint` rules |
| `npm test` | Karma + Jasmine unit tests (Chrome) |
| `npm run test-headless` | Headless Chrome CI test run |
| `npm run e2e` | Protractor E2E tests |

### 8.2 Docker

- **Dockerfile:** Multi-stage build — Node 16 Alpine for `npm install && npm run build`, Nginx 1.17.6 for serving static files.
- Exposes port **8080** (Nginx).
- Includes a `HEALTHCHECK` command.
- Runs as `nginx` user (non-root).

### 8.3 Key Configuration Files

| File | Purpose |
|---|---|
| `angular.json` | Angular CLI workspace config (build, serve, test, lint targets) |
| `tsconfig.json` | Root TypeScript config (ES2022 target, strict templates) |
| `src/tsconfig.app.json` | App-specific TS config |
| `src/tsconfig.spec.json` | Test-specific TS config |
| `karma.conf.js` | Karma test runner (Jasmine, Chrome, ChromeHeadlessCI) |
| `protractor.conf.js` | Protractor E2E config |
| `.eslintrc.json` | ESLint + `@angular-eslint` + `@typescript-eslint` rules |
| `src/environments/environment.ts` | Dev environment (REST API URL) |
| `src/environments/environment.prod.ts` | Prod environment (REST API URL — currently same as dev) |

### 8.4 Testing Infrastructure

- **Unit Tests:** 28 spec files using Jasmine + Karma
  - Service tests use `HttpClientTestingModule` and `HttpTestingController`
  - Component tests use `RouterTestingModule`, `NO_ERRORS_SCHEMA`, and custom stubs
- **E2E Tests:** 1 Protractor spec with Page Object Model pattern
  - **Note:** The single E2E test is commented out / broken (expects `'app works!'` text)
- **Test helpers:** `src/app/testing/` provides `DummyComponent`, `RouterLinkStubDirective`, `RouterOutletStubComponent`, `ActivatedRouteStub`

### 8.5 Dependencies Summary

**Runtime (14 packages):**
- Angular 16.2.1 (core, forms, router, material, animations, CDK)
- Bootstrap 3, jQuery, Tether (legacy UI stack)
- Moment.js, RxJS 6, zone.js, tslib, core-js

**Dev (21 packages):**
- Angular CLI 16.2.0, TypeScript 4.9.5
- ESLint ecosystem (`@angular-eslint`, `@typescript-eslint`, Prettier)
- Testing: Karma 6.3.16, Jasmine 3.6, Protractor 7
- Build: `@angular-devkit/build-angular`, `source-map-explorer`
