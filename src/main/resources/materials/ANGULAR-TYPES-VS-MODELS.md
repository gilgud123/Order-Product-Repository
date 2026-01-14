# Types vs Models in Angular (and TypeScript)

A practical guide to when to use interfaces/types versus classes/models in Angular apps.

## Quick Definitions
- **Types/Interfaces**: Compile-time only. Describe the shape of data (properties and their types). They disappear at runtime.
- **Models (Classes)**: Runtime constructs. Represent domain entities with behavior (methods), defaults, computed fields, and validation.

## Key Differences
- **Purpose**
  - Types/interfaces: Structure and contracts for data. Great for API responses, function inputs/outputs, component props.
  - Models (classes): Domain behavior and lifecycle. Useful for rich objects with methods, transformations, and invariants.
- **Runtime presence**
  - Types: No runtime—cannot instantiate, check `instanceof`, or attach methods.
  - Models: Have constructors, methods, can be instantiated, and used with `instanceof`.
- **Data mapping**
  - Types: Typically used directly with HttpClient’s generics to type server responses.
  - Models: Wrap raw data (DTOs) into richer objects via constructors or factory methods.
- **Validation and defaults**
  - Types: Can’t enforce at runtime.
  - Models: Can enforce invariants, set defaults, normalize inputs.
- **Serialization**
  - Types: Rely on raw JSON and utilities.
  - Models: Implement `toJSON()`, `fromJSON()`, and mapping to/from DTOs.

## When to Use Each
- **Use types/interfaces when**:
  - Defining the shape of API payloads (DTOs).
  - You need lightweight typing for components, services, and utilities.
  - Data is treated as plain objects and you don’t need methods.
- **Use models (classes) when**:
  - You have domain behavior (e.g., calculate totals, format prices, check status transitions).
  - You need constructors to normalize/validate data, set defaults.
  - You want encapsulated methods (e.g., `order.updateStatus()`, `product.isInStock()`).

## Examples
- **Type/interface (API DTO)**
  - `ProductDTO`: `id`, `name`, `description`, `price`, `stockQuantity`, `category`, `createdAt`, `updatedAt`.
  - Suited for HttpClient: `http.get<ProductDTO[]>(...)`.
- **Model (class wrapping DTO)**
  ```ts
  export interface ProductDTO {
    id: number;
    name: string;
    description?: string;
    price: number;
    stockQuantity: number;
    category?: string;
    createdAt?: string;
    updatedAt?: string;
  }

  export class Product {
    constructor(private dto: ProductDTO) {}

    get formattedPrice(): string {
      return `$${this.dto.price.toFixed(2)}`;
    }

    isInStock(): boolean {
      return this.dto.stockQuantity > 0;
    }

    updateStock(delta: number): void {
      this.dto.stockQuantity = Math.max(0, this.dto.stockQuantity + delta);
    }

    toDTO(): ProductDTO {
      return { ...this.dto };
    }

    static fromDTO(dto: ProductDTO): Product {
      return new Product(dto);
    }
  }
  ```

## Pros and Cons
- **Types/Interfaces**
  - Pros: Simple, zero runtime overhead, easy with HttpClient, great developer ergonomics.
  - Cons: No runtime behavior, no validation or invariants.
- **Models (Classes)**
  - Pros: Encapsulate behavior, validation, defaults, computed properties; more expressive domain.
  - Cons: More boilerplate; need mapping between DTOs and models; risk of over-engineering for simple CRUD.

## Practical Guidance in Angular Apps
- Keep API contracts as interfaces (DTO types). Works perfectly with HttpClient generics.
- Introduce models selectively for complex domains:
  - Orders: methods to compute total, change status with rules.
  - Products: stock management, formatting helpers.
- Centralize mapping:
  - In services: map DTOs to models on read; map models to DTOs on write.
  - Or use factory/static methods: `Order.fromDTO(dto)`, `order.toDTO()`.

## Testing Impact
- **Types**: Unit tests focus on functions manipulating plain objects.
- **Models**: Unit tests target methods directly (e.g., `updateStatus()`, `computeTotal()`).

## Bottom Line
- Use interfaces for lightweight typing of data shapes.
- Use classes as models when you need runtime behavior and domain logic.
- Don’t convert everything to models—apply them where they add clear value.

