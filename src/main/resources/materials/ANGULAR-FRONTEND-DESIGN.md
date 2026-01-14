# Angular Front-End Design for Product REST Assignment

This document outlines a practical Angular front-end design tailored to the Product REST backend, covering app structure, routing, services, state, auth, UI, and integration.

## Checklist
- Establish app modules, routing, and core layout
- Define data models matching backend DTOs
- Build services for Users, Products, Orders with pagination/filter/search
- Implement auth (Keycloak + JWT) and HTTP interceptors
- Create feature pages: lists, details, forms
- Add reusable UI primitives: table, pagination, search/filter, form controls
- Handle errors, loading states, and validation
- Wire OpenAPI/Swagger for type safety (optional)
- Plan testing, configuration, and environments

## 1) App Structure
- Workspace: Single Angular CLI app (`src/app`).
- Modules:
  - `CoreModule`: layout, navbar/sidebar, auth, interceptors.
  - `SharedModule`: reusable components (table, pagination, search, filter chips), pipes, directives.
  - Feature modules:
    - `UsersModule`: list, detail, create/edit.
    - `ProductsModule`: list, detail, create/edit, filter.
    - `OrdersModule`: list, detail, create, status update.
    - `AnalyticsModule`: customer revenue view/graph (consumes `CustomerRevenueDTO`).
  - `AuthModule`: login/logout, guard (or wrap Keycloak adapter initialization here).

## 2) Routing
- Top-level routes:
  - `/login` (if not fully external with Keycloak)
  - `/users`, `/users/:id`
  - `/products`, `/products/:id`
  - `/orders`, `/orders/:id`
  - `/analytics/revenue`
- Guards:
  - `AuthGuard` ensures valid JWT before protected routes.
- Preloading strategy: `PreloadAllModules` for snappy UX.

## 3) Data Models (align with backend DTOs)
- `User`: id, username, email, firstName, lastName, createdAt, updatedAt.
- `Product`: id, name, description, price, stockQuantity, category, createdAt, updatedAt.
- `Order`: id, userId, productIds, totalAmount, status, createdAt, updatedAt.
- `Page<T>`: content, page, size, totalElements, totalPages.
- `ProductFilter`: { category?: string, minPrice?: number, maxPrice?: number }.

## 4) Services and API Integration
- Base API URL: `http://localhost:8080/api`
- `UsersService`
  - `list(page, size)`
  - `getById(id)`
  - `search(query, page, size)`
  - `create(user)`
  - `update(id, user)`
  - `delete(id)`
- `ProductsService`
  - `list(page, size)`
  - `getById(id)`
  - `search(query, page, size)`
  - `filter({category, minPrice, maxPrice}, page, size)`
  - `create(product)`
  - `update(id, product)`
  - `delete(id)`
- `OrdersService`
  - `list(page, size)`
  - `getById(id)`
  - `listByUser(userId, page, size)`
  - `filter({userId, status}, page, size)` if available
  - `create({userId, productIds})`
  - `updateStatus(id, status)` via PATCH
  - `delete(id)`
- `HttpClient` usage:
  - Centralize error handling via interceptor.
  - Include JWT via `Authorization: Bearer <token>` from Keycloak.

## 5) Auth and Security (Keycloak)
- Option A: Keycloak JS adapter (recommended for SPA)
  - Initialize in `main.ts` before bootstrapping Angular (Keycloak init, silent SSO).
  - On success, store token in `TokenService`; refresh token on schedule.
- Option B: OAuth2/OIDC with `angular-oauth2-oidc`
  - Configure discovery URL to Keycloak realm; set `responseType=code`, use PKCE.
- Interceptor:
  - Attach `Authorization` header.
  - Handle 401/403 by redirecting to login or refreshing token.
- Guards:
  - `AuthGuard` ensures token presence and validity.
  - `RoleGuard` if backend roles are used (e.g., admin to delete).

## 6) UI Components and Pages
- Core layout:
  - Top navbar: navigation between Users, Products, Orders, Analytics; user profile/logout.
  - Side panel (optional): quick filters for products.
- Users
  - `UsersList`: paginated table, search bar for query (username/email/name), columns (username, email, names, actions).
  - `UserDetail`: read-only view; edit button to navigate to form.
  - `UserForm`: reactive form with validation (email format, required fields).
- Products
  - `ProductsList`: paginated table; search by query; filter by category and price range; columns (name, price, stock, category, actions).
  - `ProductDetail`: description, price, stock, created/updated dates.
  - `ProductForm`: reactive form with constraints (name length, price > 0).
- Orders
  - `OrdersList`: paginated; filter by user and status; show totalAmount and status; action to update status.
  - `OrderDetail`: list of products, total, status timeline.
  - `OrderCreate`: select user and multi-select products; show computed total (optional client-side preview).
- Analytics
  - `CustomerRevenue`: input `customerId`; fetch revenue per year; render chart using `ngx-charts` or `Chart.js`.
- Shared components
  - `PaginationControls`: page/size, show total items.
  - `SearchBox`: emits debounced query changes.
  - `FiltersPanel`: for product filters; two-way binding to filter state.
  - `ConfirmDialog`: for deletes and status changes.
  - `LoadingSpinner` and `ErrorAlert` components.

## 7) Forms and Validation
- Use Reactive Forms.
- Client-side constraints match backend:
  - Product: name 2–100 chars; description max 500; price > 0; stock required; category max 50.
  - User: username 3–50; valid email; first/last name max 50.
- Display backend validation errors (400) in form controls.

## 8) Pagination, Search, Filter UX
- Keep page state in URL query params (`page`, `size`, `q`, `category`, `minPrice`, `maxPrice`).
- Debounce search input; reset page to 0 on query change.
- Persist filters across navigation; provide “Clear filters”.

## 9) Error Handling and Resiliency
- Interceptor maps HTTP errors:
  - 400: show field validation errors.
  - 401: trigger login or token refresh.
  - 403: show “Not authorized”.
  - 404: show “Not found”.
  - 5xx: show “Server error, try again”.
- Global error service for toast/snackbar notifications.
- Loading indicators per page and per action.

## 10) State Management
- Start simple: services + local component state.
- Share auth token and user profile via a lightweight `AuthStore` (BehaviorSubject).
- Scale-up: introduce NGXS or NGRX for entities and cached lists.

## 11) Environment Configuration
- `environment.ts`: `apiBaseUrl = http://localhost:8080/api`; Keycloak settings.
- `environment.prod.ts`: production API and Keycloak realm/client.
- Keep OpenAPI doc URL (`/api-docs`) in env if generating types.

## 12) Type Safety via OpenAPI (Optional)
- Use `openapi-typescript-codegen` or `ng-openapi-gen` against Springdoc.
- Generate DTOs and client methods for strong typing and fewer manual services.

## 13) Testing
- Unit tests:
  - Services: mock `HttpClient`; test query param building and error paths.
  - Components: test form validation, search/filter interactions.
- E2E (Cypress or Playwright):
  - Auth flow (login via Keycloak).
  - CRUD happy path for Users/Products/Orders.
  - Pagination and search behaviors.

## 14) Performance and UX Notes
- Prefer `OnPush` change detection on heavy list components.
- Virtual scrolling if item counts are large.
- Cache last page’s results per query.
- Use `trackBy` in `ngFor` and lazy-load feature modules.

## 15) Security Considerations
- Prefer in-memory/session token storage via Keycloak adapter; avoid `localStorage` when possible.
- Ensure HTTPS in production.
- Validate inputs server-side; sanitize any HTML client-side.

## Backend Endpoint Mapping Quick Reference
- Users:
  - `GET /api/users?page&size`
  - `GET /api/users/{id}`
  - `GET /api/users/search?query`
  - `POST /api/users`
  - `PUT /api/users/{id}`
  - `DELETE /api/users/{id}`
- Products:
  - `GET /api/products?page&size`
  - `GET /api/products/{id}`
  - `GET /api/products/search?query`
  - `GET /api/products/filter?category&minPrice&maxPrice`
  - `POST /api/products`
  - `PUT /api/products/{id}`
  - `DELETE /api/products/{id}`
- Orders:
  - `GET /api/orders?page&size`
  - `GET /api/orders/{id}`
  - `GET /api/orders/user/{userId}`
  - `POST /api/orders`
  - `PATCH /api/orders/{id}/status`
  - `DELETE /api/orders/{id}`
- Analytics:
  - `GET /api/orders/customer/{customerId}/revenue` (as documented) ? consume in Analytics module.

## Next Steps
- Scaffold Angular project structure and stub services/components.
- Add an Auth bootstrap using Keycloak adapter and an HTTP interceptor.
- Optionally generate TypeScript clients from your existing Springdoc OpenAPI.
- Provide a minimal UI skeleton with pagination, search, and forms wired to your APIs.

