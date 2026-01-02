# Product REST API Assignment

A comprehensive Spring Boot REST API for managing users, products, and orders with full CRUD operations, pagination, filtering, and search capabilities.

## Features

### User Management
- Create, Read, Update, Delete users
- Search users by username, email, first name, or last name
- Pagination support
- Unique username and email validation
- Email format validation

### Product Management
- Create, Read, Update, Delete products
- Search products by name, description, or category
- Filter products by category, price range
- Pagination support
- Stock quantity tracking
- Price validation

### Order Management
- Create, Read, Update, Delete orders
- Automatic total amount calculation
- Order status tracking (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- Filter orders by user and status
- View orders by specific user
- Pagination support

## Technology Stack

- **Spring Boot 3.2.1**
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Primary database
- **Hibernate** - ORM
- **Lombok** - Reduce boilerplate code
- **MapStruct** - Entity-DTO mapping
- **Jakarta Validation** - Input validation
- **Testcontainers** - Integration testing with PostgreSQL
- **Maven** - Dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use Docker Compose)

## Database Setup

### Option 1: Using Docker Compose (Recommended)

The project includes a `compose.yaml` file for PostgreSQL. Simply run:

```cmd
docker-compose up -d
```

This will start a PostgreSQL 15 container with the following configuration:
- Database: `productdb`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

### Option 2: Manual PostgreSQL Setup

1. Install PostgreSQL
2. Create a database named `productdb`
3. Update credentials in `application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/productdb
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

### Database Initialization

The project includes SQL scripts for automatic database setup:

- **`schema.sql`** - Creates all database tables (users, products, orders, order_products)
- **`data.sql`** - Populates the database with sample data

These scripts run automatically when the application starts. To disable them, set:
```properties
spring.sql.init.mode=never
```

You can also manually run these scripts using your preferred PostgreSQL client.

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run using Maven:
   ```cmd
   mvnw.cmd spring-boot:run
   ```
   Or using your IDE's run configuration

The application will start on `http://localhost:8080`

## API Endpoints

### User Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users (paginated) |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/search?query={term}` | Search users |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

#### User Request Body Example
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Product Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products (paginated) |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/search?query={term}` | Search products |
| GET | `/api/products/filter?category={cat}&minPrice={min}&maxPrice={max}` | Filter products |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

#### Product Request Body Example
```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics"
}
```

### Order Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders (paginated) |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/user/{userId}` | Get orders by user |
| GET | `/api/orders/filter?userId={id}&status={status}` | Filter orders |
| POST | `/api/orders` | Create new order |
| PATCH | `/api/orders/{id}/status?status={status}` | Update order status |
| DELETE | `/api/orders/{id}` | Delete order |

#### Order Request Body Example
```json
{
  "userId": 1,
  "productIds": [1, 2, 3]
}
```

#### Order Status Values
- `PENDING`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

## Pagination Parameters

All list endpoints support pagination with the following query parameters:

- `page` - Page number (0-based, default: 0)
- `size` - Page size (default: 10, max: 100)
- `sort` - Sort field and direction (e.g., `name,asc` or `createdAt,desc`)

Example: `/api/products?page=0&size=20&sort=name,asc`

## Error Handling

The API provides consistent error responses:

```json
{
  "timestamp": "2026-01-02T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 123",
  "path": "/api/products/123"
}
```

### Validation Errors

Validation errors include field-specific messages:

```json
{
  "timestamp": "2026-01-02T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users",
  "validationErrors": {
    "email": "Email should be valid",
    "username": "Username must be between 3 and 50 characters"
  }
}
```

## Testing with cURL

### Create a User
```cmd
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"johndoe\",\"email\":\"john@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"
```

### Get All Users
```cmd
curl http://localhost:8080/api/users?page=0^&size=10
```

### Create a Product
```cmd
curl -X POST http://localhost:8080/api/products ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Laptop\",\"description\":\"High-performance laptop\",\"price\":999.99,\"stockQuantity\":50,\"category\":\"Electronics\"}"
```

### Create an Order
```cmd
curl -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"productIds\":[1,2]}"
```

### Update Order Status
```cmd
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=PROCESSING"
```

## Testing with Postman

1. Import the endpoints into Postman
2. Set base URL: `http://localhost:8080`
3. Use JSON format for request bodies
4. Follow the examples above for each endpoint

## Project Structure

```
src/
??? main/
?   ??? java/com/katya/test/productrestassignement/
?   ?   ??? controller/          # REST controllers
?   ?   ??? dto/                 # Data Transfer Objects
?   ?   ??? entity/              # JPA entities
?   ?   ??? exception/           # Exception handling
?   ?   ??? mapper/              # Entity-DTO mappers (MapStruct)
?   ?   ??? repository/          # Data repositories
?   ?   ??? service/             # Business logic
?   ?   ??? config/              # Configuration classes
?   ??? resources/
?       ??? application.properties
?       ??? schema.sql           # Database schema definition
?       ??? data.sql             # Sample data initialization
??? test/
    ??? java/                    # Unit and integration tests
        ??? controller/          # Controller tests
        ??? repository/          # Repository integration tests
        ??? service/             # Service integration tests
```

## Data Model

### User
- id (Long)
- username (String, unique, 3-50 chars)
- email (String, unique, valid email)
- firstName (String, max 50 chars)
- lastName (String, max 50 chars)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

### Product
- id (Long)
- name (String, 2-100 chars)
- description (String, max 500 chars)
- price (BigDecimal, > 0)
- stockQuantity (Integer)
- category (String, max 50 chars)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

### Order
- id (Long)
- user (User reference)
- products (List of Products, many-to-many)
- totalAmount (BigDecimal, calculated)
- status (OrderStatus enum)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

## Development

### Building the Project
```cmd
mvnw.cmd clean install
```

### Running Tests

The project includes comprehensive unit and integration tests.

#### Run All Tests
```cmd
mvnw.cmd test
```

#### Run Specific Test Class
```cmd
mvnw.cmd test -Dtest=ProductRepositoryTest
```

#### Integration Tests

Integration tests use **Testcontainers** to spin up a real PostgreSQL database in a Docker container. This ensures tests run against an actual database rather than mocks or in-memory databases.

Key integration test classes:
- `ProductRepositoryTest` - Repository layer integration tests
- `ProductServiceTest` - Service layer integration tests

**Note:** Docker must be running for integration tests to execute successfully.

### Packaging
```cmd
mvnw.cmd package
```

The JAR file will be created in the `target/` directory.

## Configuration

Key configuration in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/productdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Pagination
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=100
```

**Note:** `spring.jpa.hibernate.ddl-auto=none` means the database schema is managed by the `schema.sql` script rather than Hibernate auto-generation. This provides better control over database structure.

## License

This is an educational project for REST API development practice.

## Author

Katya de Vries

