# Testing Guide

## Dependencies Added

The following Mockito dependencies have been added to `pom.xml`:

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

These dependencies are automatically managed by Spring Boot's parent POM and are already resolved.

## Reload Maven Project in IDE

If you see compilation errors in the test files:

### IntelliJ IDEA
1. Right-click on `pom.xml`
2. Select "Maven" ? "Reload project"
3. Or use the Maven tool window and click the refresh icon

### Eclipse
1. Right-click on the project
2. Select "Maven" ? "Update Project"
3. Check "Force Update of Snapshots/Releases"
4. Click OK

### VS Code
1. Open Command Palette (Ctrl+Shift+P)
2. Type "Java: Clean Java Language Server Workspace"
3. Restart VS Code

## Running Tests

### Run All Tests
```cmd
.\mvnw.cmd test
```

### Run Specific Test Class
```cmd
.\mvnw.cmd test -Dtest=UserControllerTest
```

### Run from IDE
- Right-click on the test class or method
- Select "Run Test" or "Debug Test"

## Test Structure

The project includes:
- **UserControllerTest.java** - Unit tests for UserController
  - Tests for GET all users
  - Tests for GET user by ID
  - Tests for POST create user
  - Tests for DELETE user

## Writing More Tests

### Example: Testing Product Controller

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(new BigDecimal("99.99"));
        
        when(productService.createProduct(any())).thenReturn(productDTO);
        
        String productJson = """
            {
                "name": "Test Product",
                "price": 99.99,
                "stockQuantity": 10
            }
            """;
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test Product"));
    }
}
```

## Test Coverage Tools

### JaCoCo (Java Code Coverage)

Add to `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run tests with coverage:
```cmd
.\mvnw.cmd clean test
```

View report at: `target/site/jacoco/index.html`

## Integration Tests

For integration tests with actual database:

```java
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    void createUser_ShouldPersistToDatabase() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        
        UserDTO saved = userService.createUser(userDTO);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
    }
}
```

## Next Steps

1. **Reload Maven project** in your IDE to resolve dependencies
2. **Run existing tests** to verify setup: `.\mvnw.cmd test`
3. **Write tests** for ProductController and OrderController
4. **Add integration tests** for service layer
5. **Set up test coverage** reporting with JaCoCo

Happy Testing! ?

