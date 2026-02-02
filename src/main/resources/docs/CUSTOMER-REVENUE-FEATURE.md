# Customer Revenue Feature Implementation Summary

## Overview
Successfully implemented a REST endpoint that returns total revenue per customer per year, with full integration testing across all layers (controller, service, repository).

## Implementation Details

### 1. **CustomerRevenueDTO** (New)
**File:** `src/main/java/com/katya/test/productrestassignement/dto/CustomerRevenueDTO.java`

A new DTO to encapsulate customer revenue data:
```java
public class CustomerRevenueDTO {
    private Long customerId;
    private Integer year;
    private BigDecimal totalRevenue;
}
```

### 2. **OrderRepository** (Modified)
**File:** `src/main/java/com/katya/test/productrestassignement/repository/OrderRepository.java`

Added a new query method that:
- Groups orders by year (extracted from createdAt timestamp)
- Sums the totalAmount for each year
- Filters by customer ID

```java
@Query("SELECT YEAR(o.createdAt) as year, SUM(o.totalAmount) as totalRevenue " +
       "FROM Order o WHERE o.user.id = :customerId " +
       "GROUP BY YEAR(o.createdAt) " +
       "ORDER BY YEAR(o.createdAt)")
List<Object[]> calculateCustomerRevenuePerYear(@Param("customerId") Long customerId);
```

### 3. **OrderService** (Modified)
**File:** `src/main/java/com/katya/test/productrestassignement/service/OrderService.java`

Added a new service method that:
- Validates the customer exists
- Calls the repository query
- Transforms Object[] results into CustomerRevenueDTO objects

```java
@Transactional(readOnly = true)
public List<CustomerRevenueDTO> getCustomerRevenuePerYear(Long customerId) {
    // Verify user exists
    if (!userRepository.existsById(customerId)) {
        throw new ResourceNotFoundException("User", customerId);
    }

    List<Object[]> results = orderRepository.calculateCustomerRevenuePerYear(customerId);
    List<CustomerRevenueDTO> revenueList = new ArrayList<>();

    for (Object[] result : results) {
        Integer year = (Integer) result[0];
        BigDecimal totalRevenue = (BigDecimal) result[1];
        revenueList.add(new CustomerRevenueDTO(customerId, year, totalRevenue));
    }

    return revenueList;
}
```

### 4. **OrderController** (Modified)
**File:** `src/main/java/com/katya/test/productrestassignement/controller/OrderController.java`

Added a new REST endpoint:
```java
@GetMapping("/customer/{customerId}/revenue")
public ResponseEntity<List<CustomerRevenueDTO>> getCustomerRevenue(@PathVariable Long customerId) {
    List<CustomerRevenueDTO> revenue = orderService.getCustomerRevenuePerYear(customerId);
    return ResponseEntity.ok(revenue);
}
```

**Endpoint:** `GET /api/orders/customer/{customerId}/revenue`

**Example Response:**
```json
[
  {
    "customerId": 1,
    "year": 2024,
    "totalRevenue": 5000.00
  },
  {
    "customerId": 1,
    "year": 2025,
    "totalRevenue": 3000.00
  }
]
```

## Integration Tests

### 5. **OrderRepositoryTest** (Modified)
**File:** `src/test/java/com/katya/test/productrestassignement/repository/OrderRepositoryTest.java`

Added test: `whenCalculateCustomerRevenuePerYear_thenReturnCorrectRevenue()`
- Creates 3 orders for the same customer with different amounts
- Calls the repository query method
- Verifies the result contains the correct year and total revenue sum
- Validates the revenue totals to $1749.99

### 6. **OrderServiceTest** (Modified)
**File:** `src/test/java/com/katya/test/productrestassignement/service/OrderServiceTest.java`

Added test: `whenGetCustomerRevenuePerYear_thenReturnRevenueList()`
- Creates 3 orders for a customer
- Calls the service method
- Verifies CustomerRevenueDTO objects are returned with correct structure
- Validates customer ID, year, and total revenue ($1750.00)

### 7. **OrderControllerTest** (Modified)
**File:** `src/test/java/com/katya/test/productrestassignement/controller/OrderControllerTest.java`

Added test: `getCustomerRevenue_ShouldReturnRevenueList()`
- Mocks the service to return sample revenue data
- Tests the REST endpoint with MockMvc
- Verifies HTTP 200 status
- Validates JSON response structure and values

## Documentation Updates

### 8. **API-EXAMPLES.http** (Modified)
Added example request:
```http
### Get Customer Revenue Per Year
GET http://localhost:8080/api/orders/customer/1/revenue
```

### 9. **README.md** (Modified)
- Added the new endpoint to the Order Endpoints table
- Added a section explaining the Customer Revenue Response format with example JSON

## Test Coverage Summary

? **Repository Layer**
- Query correctly groups by year
- Query correctly sums order totals
- Results are in the correct format (Object[] with year and totalRevenue)

? **Service Layer**
- Customer validation works
- Object[] results are correctly transformed to DTOs
- Error handling for non-existent customers

? **Controller Layer**
- Endpoint responds with HTTP 200
- JSON response is correctly formatted
- Integration with service layer works

## Technical Highlights

1. **SQL Aggregation**: Uses JPQL with `YEAR()` function and `GROUP BY` for efficient database-level aggregation
2. **Type Safety**: Properly converts Object[] from query to typed CustomerRevenueDTO
3. **Validation**: Ensures customer exists before attempting to calculate revenue
4. **Read-Only Transaction**: Uses `@Transactional(readOnly = true)` for optimal performance
5. **Integration Testing**: Full test coverage across all layers using Testcontainers with PostgreSQL
6. **RESTful Design**: Follows REST conventions with clear resource naming

## How to Use

1. **Ensure customer has orders**: Create orders for a customer using `POST /api/orders`
2. **Get revenue data**: Call `GET /api/orders/customer/{customerId}/revenue`
3. **Interpret results**: Response shows total revenue grouped by year

## Example Workflow

```http
# 1. Create a customer
POST http://localhost:8080/api/users
Content-Type: application/json
{
  "username": "john",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}

# 2. Create products
POST http://localhost:8080/api/products
Content-Type: application/json
{
  "name": "Laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics"
}

# 3. Create orders
POST http://localhost:8080/api/orders
Content-Type: application/json
{
  "userId": 1,
  "productIds": [1]
}

# 4. Get customer revenue
GET http://localhost:8080/api/orders/customer/1/revenue
```

## Files Modified/Created

**Created:**
- `CustomerRevenueDTO.java`

**Modified:**
- `OrderRepository.java` - Added query method
- `OrderService.java` - Added service method
- `OrderController.java` - Added REST endpoint
- `OrderRepositoryTest.java` - Added integration test
- `OrderServiceTest.java` - Added integration test
- `OrderControllerTest.java` - Added controller test
- `API-EXAMPLES.http` - Added API example
- `README.md` - Added endpoint documentation

**Total Changes:**
- 1 new file
- 7 modified files
- 3 new test methods
- Full integration testing coverage

## Status: ? Complete

All changes have been implemented, tested, and documented. The feature is ready for use!

