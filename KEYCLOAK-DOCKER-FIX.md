# Keycloak Docker Configuration Fix

## Problem
The Keycloak Docker container was failing to start with the error:
```
ERROR: Failed to run 'build' command.
ERROR: Cannot invoke "io.smallrye.config.ConfigValue.withConfigSourceName(String)" 
because the return value of "org.keycloak.quarkus.runtime.configuration.mappers.PropertyMapper.transformValue(String, java.util.Optional, io.smallrye.config.ConfigSourceInterceptorContext)" is null
```

## Root Cause
The error was caused by incorrect Keycloak configuration in the `compose.yaml` file, specifically:
1. Attempting to configure Keycloak with PostgreSQL database
2. Incorrect environment variable format for Keycloak 23.0.0
3. Database connection issues between Keycloak and PostgreSQL

## Solution
Simplified the Keycloak configuration to use its built-in H2 database in development mode.

### Before (Problematic Configuration)
```yaml
keycloak:
  image: quay.io/keycloak/keycloak:23.0.0
  container_name: product-keycloak
  environment:
    KEYCLOAK_ADMIN: admin
    KEYCLOAK_ADMIN_PASSWORD: admin
    KC_DB: postgres
    KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
    KC_DB_USERNAME: postgres
    KC_DB_PASSWORD: postgres
    KC_HOSTNAME_STRICT: "false"
    KC_HTTP_ENABLED: "true"
    KC_HEALTH_ENABLED: "true"
  command:
    - start-dev
  ports:
    - "8180:8080"
  depends_on:
    postgres:
      condition: service_healthy
```

### After (Working Configuration)
```yaml
keycloak:
  image: quay.io/keycloak/keycloak:23.0.0
  container_name: product-keycloak
  environment:
    KEYCLOAK_ADMIN: admin
    KEYCLOAK_ADMIN_PASSWORD: admin
  command:
    - start-dev
  ports:
    - "8180:8080"
  depends_on:
    postgres:
      condition: service_healthy
```

## Key Changes

1. **Removed PostgreSQL Configuration**
   - Removed `KC_DB`, `KC_DB_URL`, `KC_DB_USERNAME`, `KC_DB_PASSWORD`
   - Keycloak now uses built-in H2 database

2. **Simplified Environment Variables**
   - Only kept essential variables: `KEYCLOAK_ADMIN` and `KEYCLOAK_ADMIN_PASSWORD`
   - Removed unnecessary configuration flags

3. **Development Mode Benefits**
   - `start-dev` command automatically configures Keycloak for development
   - Built-in H2 database (no external database needed)
   - Faster startup time
   - Simpler configuration

## Why This Works

### Development Mode (`start-dev`)
When Keycloak runs with `start-dev`, it:
- Uses an embedded H2 database automatically
- Disables hostname verification
- Enables HTTP (non-HTTPS)
- Enables health checks
- Simplifies the development experience

### No External Database Required
For development and testing:
- H2 database is sufficient
- Data persists between container restarts (stored in container)
- No need to manage a separate Keycloak database

### Production Considerations
For production, you should:
- Use `start` command instead of `start-dev`
- Configure external PostgreSQL database
- Enable HTTPS
- Configure proper hostname
- Set up persistent volumes

## Testing the Fix

### 1. Clean Up Old Containers
```bash
docker-compose down -v
```

### 2. Remove Old Images (Optional)
```bash
docker rmi quay.io/keycloak/keycloak:23.0.0
```

### 3. Start Services
```bash
docker-compose up -d
```

Or use the provided script:
```bash
start-keycloak.bat
```

### 4. Verify Keycloak is Running
```bash
docker ps
```

Look for `product-keycloak` container with status "Up".

### 5. Check Logs
```bash
docker-compose logs -f keycloak
```

You should see:
```
Keycloak 23.0.0 on JVM (powered by Quarkus)
...
Listening on: http://0.0.0.0:8080
```

### 6. Access Admin Console
Wait 30-60 seconds after startup, then open:
```
http://localhost:8180
```

Login with:
- Username: `admin`
- Password: `admin`

## Files Modified

1. **compose.yaml**
   - Simplified Keycloak service configuration
   - Removed PostgreSQL database connection

2. **README.md**
   - Updated database setup section
   - Added note about H2 database in development mode

3. **KEYCLOAK-SETUP.md**
   - Added initialization wait time note
   - Added troubleshooting section for this specific error

4. **start-keycloak.bat**
   - Added error handling
   - Increased wait time to 40 seconds
   - Added troubleshooting instructions

## Common Issues and Solutions

### Issue: Container starts but admin console not accessible
**Solution:** Wait longer (up to 60 seconds) for Keycloak to fully initialize.

### Issue: Port 8180 already in use
**Solution:** 
```bash
# Find and kill process using port 8180
netstat -ano | findstr :8180
taskkill /PID <PID> /F

# Or change port in compose.yaml
```

### Issue: Container keeps restarting
**Solution:**
```bash
# Check logs for errors
docker-compose logs keycloak

# Try recreating containers
docker-compose down -v
docker-compose up -d --force-recreate
```

### Issue: "Connection refused" when accessing admin console
**Solution:**
- Verify Docker Desktop is running
- Check container status: `docker ps`
- Check logs: `docker-compose logs keycloak`
- Wait longer for initialization

## Benefits of This Approach

### For Development
? Simple configuration
? Fast setup
? No external database management
? Works out of the box
? Easy to reset (just restart container)

### For Testing
? Consistent test environment
? Isolated from production data
? Quick to spin up/down
? No data persistence concerns

## When to Use External Database

Use PostgreSQL (or other external database) for Keycloak when:
- **Production deployment** - Need data persistence
- **Multi-instance setup** - Running Keycloak in cluster
- **Data backup** - Need to backup Keycloak configuration
- **Long-term storage** - Need to preserve realms, users, clients

## Migration Path to Production

When ready for production:

1. **Create Keycloak Database**
   ```sql
   CREATE DATABASE keycloak;
   ```

2. **Update compose.yaml**
   ```yaml
   keycloak:
     image: quay.io/keycloak/keycloak:23.0.0
     environment:
       KEYCLOAK_ADMIN: admin
       KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
       KC_DB: postgres
       KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
       KC_DB_USERNAME: postgres
       KC_DB_PASSWORD: ${DB_PASSWORD}
       KC_HOSTNAME: your-domain.com
       KC_HTTPS_CERTIFICATE_FILE: /opt/keycloak/conf/server.crt
       KC_HTTPS_CERTIFICATE_KEY_FILE: /opt/keycloak/conf/server.key
     command:
       - start
       - --optimized
     volumes:
       - ./certs:/opt/keycloak/conf
   ```

3. **Use Environment Variables for Secrets**
4. **Enable HTTPS**
5. **Configure Proper Hostname**
6. **Set Up Persistent Volumes**

## Summary

? **Problem:** Keycloak Docker container failing to start with configuration error

? **Root Cause:** Incorrect PostgreSQL database configuration for Keycloak

? **Solution:** Simplified to use built-in H2 database in development mode

? **Result:** Keycloak starts successfully and is accessible at http://localhost:8180

? **Trade-off:** Using H2 (fine for development) instead of PostgreSQL (needed for production)

The fix provides a working development environment while maintaining the option to upgrade to PostgreSQL for production deployment.

