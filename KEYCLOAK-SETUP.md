# Keycloak Security Setup Guide

## Overview
This guide explains how to set up and configure Keycloak for securing the Product REST API with OAuth2/OpenID Connect authentication.

## Architecture

The application uses:
- **Keycloak** - Identity and Access Management (IAM) server
- **OAuth2/OpenID Connect** - Authentication protocol
- **JWT tokens** - Token-based authentication
- **Role-based access control (RBAC)** - Authorization

## Prerequisites

- Docker and Docker Compose installed
- Application dependencies added to `pom.xml`

## Dependencies Added

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Starting Keycloak

### 1. Start Services with Docker Compose

```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432 (application database)
- Keycloak on port 8180 (IAM server)

### 2. Access Keycloak Admin Console

Open browser to: `http://localhost:8180`

**Admin Credentials:**
- Username: `admin`
- Password: `admin`

## Keycloak Configuration

### Step 1: Create Realm

1. Click on **"Create Realm"** (top left dropdown)
2. Enter realm name: `product-rest-api`
3. Click **"Create"**

### Step 2: Create Roles

1. Navigate to **Realm roles** (left menu)
2. Click **"Create role"**
3. Create the following roles:
   - Role name: `USER`
   - Role name: `ADMIN`

### Step 3: Create Client

1. Navigate to **Clients** (left menu)
2. Click **"Create client"**
3. Configure:
   - **Client ID**: `product-rest-client`
   - **Client Protocol**: `openid-connect`
   - Click **"Next"**
4. Client authentication:
   - **Client authentication**: `OFF` (for public client) or `ON` (for confidential)
   - **Authorization**: `OFF`
   - **Standard flow**: `Enabled`
   - **Direct access grants**: `Enabled`
   - Click **"Next"**
5. Valid redirect URIs:
   - `http://localhost:8080/*`
   - `http://localhost:8080/swagger-ui/*`
   - `http://localhost:8080/swagger-ui.html`
6. Valid post logout redirect URIs:
   - `http://localhost:8080/*`
7. Web origins:
   - `http://localhost:8080`
   - `*` (for development only)
8. Click **"Save"**

### Step 4: Create Users

#### Create Admin User

1. Navigate to **Users** (left menu)
2. Click **"Add user"**
3. Configure:
   - **Username**: `admin-user`
   - **Email**: `admin@example.com`
   - **First name**: `Admin`
   - **Last name**: `User`
   - **Email verified**: `ON`
4. Click **"Create"**
5. Go to **Credentials** tab:
   - Click **"Set password"**
   - Password: `admin123`
   - Temporary: `OFF`
   - Click **"Save"**
6. Go to **Role mappings** tab:
   - Click **"Assign role"**
   - Select: `ADMIN` and `USER`
   - Click **"Assign"**

#### Create Regular User

1. Navigate to **Users** (left menu)
2. Click **"Add user"**
3. Configure:
   - **Username**: `regular-user`
   - **Email**: `user@example.com`
   - **First name**: `Regular`
   - **Last name**: `User`
   - **Email verified**: `ON`
4. Click **"Create"**
5. Go to **Credentials** tab:
   - Click **"Set password"**
   - Password: `user123`
   - Temporary: `OFF`
   - Click **"Save"**
6. Go to **Role mappings** tab:
   - Click **"Assign role"**
   - Select: `USER`
   - Click **"Assign"**

## Security Configuration

### Application Properties

```properties
# Keycloak OAuth2 Resource Server Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/product-rest-api
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/product-rest-api/protocol/openid-connect/certs

# Keycloak Client Configuration (for reference)
keycloak.auth-server-url=http://localhost:8180
keycloak.realm=product-rest-api
keycloak.resource=product-rest-client
```

### Security Rules

**Public Endpoints (No Authentication Required):**
- `/swagger-ui/**`
- `/api-docs/**`
- `/actuator/**`

**Product Endpoints:**
- `GET /api/products/**` - Requires `USER` or `ADMIN` role
- `POST /api/products/**` - Requires `ADMIN` role
- `PUT /api/products/**` - Requires `ADMIN` role
- `DELETE /api/products/**` - Requires `ADMIN` role

**User Endpoints:**
- `GET /api/users/**` - Requires `USER` or `ADMIN` role
- `POST /api/users/**` - Requires `ADMIN` role
- `PUT /api/users/**` - Requires `ADMIN` role
- `DELETE /api/users/**` - Requires `ADMIN` role

**Order Endpoints:**
- `GET /api/orders/**` - Requires `USER` or `ADMIN` role
- `POST /api/orders/**` - Requires `USER` or `ADMIN` role
- `PATCH /api/orders/**` - Requires `ADMIN` role
- `DELETE /api/orders/**` - Requires `ADMIN` role

## Testing with Swagger UI

### 1. Start the Application

```bash
mvnw.cmd spring-boot:run
```

### 2. Access Swagger UI

Open: `http://localhost:8080/swagger-ui.html`

### 3. Authenticate

1. Click **"Authorize"** button (top right)
2. In the OAuth2 modal:
   - **client_id**: `product-rest-client`
   - **client_secret**: (leave empty for public client)
   - Select scopes if needed
3. Click **"Authorize"**
4. You'll be redirected to Keycloak login
5. Login with:
   - Username: `admin-user` or `regular-user`
   - Password: `admin123` or `user123`
6. After successful login, you'll be redirected back to Swagger UI

### 4. Test Endpoints

Now you can test endpoints with proper authentication:
- Click "Try it out" on any endpoint
- Execute the request
- The JWT token will be automatically included in the Authorization header

## Testing with cURL

### Get Access Token

```bash
curl -X POST "http://localhost:8180/realms/product-rest-api/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=product-rest-client" \
  -d "username=admin-user" \
  -d "password=admin123" \
  -d "grant_type=password"
```

Response includes:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

### Use Access Token

```bash
curl -X GET "http://localhost:8080/api/products" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Testing with Postman

### 1. Create New Request

1. Method: `GET`
2. URL: `http://localhost:8080/api/products`

### 2. Configure Authorization

1. Go to **Authorization** tab
2. Type: **OAuth 2.0**
3. Configure:
   - **Grant Type**: `Password Credentials`
   - **Access Token URL**: `http://localhost:8180/realms/product-rest-api/protocol/openid-connect/token`
   - **Client ID**: `product-rest-client`
   - **Username**: `admin-user`
   - **Password**: `admin123`
   - **Scope**: (optional)
4. Click **"Get New Access Token"**
5. Click **"Use Token"**

### 3. Send Request

Click **"Send"** - the request will include the JWT token automatically.

## Token Structure

JWT tokens contain:

```json
{
  "exp": 1704441234,
  "iat": 1704440934,
  "jti": "abc-123-def",
  "iss": "http://localhost:8180/realms/product-rest-api",
  "sub": "user-uuid",
  "typ": "Bearer",
  "azp": "product-rest-client",
  "realm_access": {
    "roles": ["USER", "ADMIN"]
  },
  "scope": "openid profile email",
  "email_verified": true,
  "name": "Admin User",
  "preferred_username": "admin-user",
  "given_name": "Admin",
  "family_name": "User",
  "email": "admin@example.com"
}
```

## Troubleshooting

### Issue: 401 Unauthorized

**Cause**: Token is missing or invalid

**Solution**:
- Get a new access token
- Check token expiration (default 5 minutes)
- Verify token includes required roles

### Issue: 403 Forbidden

**Cause**: User doesn't have required role

**Solution**:
- Verify user has correct role in Keycloak
- Check SecurityConfig role requirements
- Use admin-user for admin operations

### Issue: Cannot connect to Keycloak

**Cause**: Keycloak not running

**Solution**:
```bash
docker-compose up -d keycloak
docker-compose logs keycloak
```

### Issue: Token validation failed

**Cause**: Issuer URI mismatch

**Solution**:
- Verify `spring.security.oauth2.resourceserver.jwt.issuer-uri` matches Keycloak realm
- Check Keycloak is accessible from application

## Production Considerations

### Security Best Practices

1. **Use HTTPS** - Always use TLS in production
2. **Strong passwords** - Enforce strong password policies
3. **Token expiration** - Configure appropriate token lifetimes
4. **Refresh tokens** - Implement refresh token rotation
5. **Client secrets** - Use confidential clients with secrets
6. **CORS** - Configure proper CORS policies
7. **Rate limiting** - Implement rate limiting on authentication endpoints

### Keycloak Production Setup

1. Use external database (not H2)
2. Enable HTTPS
3. Configure proper realm settings
4. Set up user federation (LDAP/AD)
5. Enable logging and monitoring
6. Configure backup strategies
7. Use cluster mode for high availability

### Environment-Specific Configuration

Use Spring profiles for different environments:

**application-dev.properties**
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/product-rest-api
```

**application-prod.properties**
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://keycloak.yourcompany.com/realms/product-rest-api
```

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [OAuth 2.0 RFC](https://tools.ietf.org/html/rfc6749)
- [JWT RFC](https://tools.ietf.org/html/rfc7519)

## Summary

? Keycloak integrated as OAuth2 provider
? JWT token-based authentication
? Role-based access control (USER, ADMIN)
? Swagger UI OAuth2 integration
? Stateless session management
? Production-ready security configuration

Your application is now secured with Keycloak! ?

