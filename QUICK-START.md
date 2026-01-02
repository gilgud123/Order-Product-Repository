# Quick Start Guide

## Step 1: Start PostgreSQL Database

Using Docker Compose (recommended):
```cmd
docker-compose up -d
```

Or manually install and configure PostgreSQL on localhost:5432 with database name `productdb`.

## Step 2: Build the Project

```cmd
mvnw.cmd clean install
```

## Step 3: Run the Application

```cmd
mvnw.cmd spring-boot:run
```

Or run from your IDE by executing the main class:
`ProductRestAssignementApplication.java`

## Step 4: Test the API

The application runs on `http://localhost:8080`

### Option 1: Using cURL

Create a user:
```cmd
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"johndoe\",\"email\":\"john@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"
```

Get all users:
```cmd
curl http://localhost:8080/api/users
```

### Option 2: Using the HTTP file

Open `API-EXAMPLES.http` in IntelliJ IDEA and click the "Run" button next to each request.

### Option 3: Using Postman

Import the endpoints from the README.md into Postman.

## Step 5: (Optional) Load Sample Data

To load sample data on startup:

1. Add this to `application.properties`:
   ```properties
   spring.profiles.active=dev
   ```

2. Restart the application

This will create:
- 2 sample users
- 3 sample products  
- 1 sample order

## Verify Installation

Check if the API is running:
```cmd
curl http://localhost:8080/api/users
```

You should receive a JSON response with paginated user data.

## Common Issues

### Port 8080 already in use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Cannot connect to PostgreSQL
- Ensure PostgreSQL is running: `docker ps` (if using Docker)
- Check credentials in `application.properties`
- Verify port 5432 is not blocked by firewall

### Dependencies not downloading
- Check internet connection
- Clear Maven cache: `mvnw.cmd clean`
- Try manual dependency download: `mvnw.cmd dependency:resolve`

## API Endpoints Summary

- **Users**: `/api/users`
- **Products**: `/api/products`  
- **Orders**: `/api/orders`

See `README.md` for detailed endpoint documentation.

## Next Steps

1. Explore the API using the examples in `API-EXAMPLES.http`
2. Review the code structure in `src/main/java`
3. Modify the entities to add custom fields
4. Add custom business logic in the service layer
5. Write unit tests for your services and controllers

Happy coding! ?

