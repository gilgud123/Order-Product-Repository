# Troubleshooting: Missing Dependencies in IntelliJ IDEA

## Problem
The UserControllerTest shows errors like:
- "Cannot resolve symbol 'WebMvcTest'"
- "Cannot resolve symbol 'MockBean'"
- "Cannot resolve symbol 'web'"
- "Cannot resolve symbol 'mockito'"

## Root Cause
IntelliJ IDEA has not indexed the Maven dependencies yet, even though they are correctly defined in `pom.xml` and have been downloaded.

## Solution Steps

### Method 1: Reload Maven Project (RECOMMENDED)

1. **Open Maven Tool Window**
   - View ? Tool Windows ? Maven
   - Or press `Ctrl + Alt + Shift + M` (Windows/Linux)

2. **Click the Reload Button**
   - Look for the circular arrow icon (?) in the Maven tool window
   - Click it to reload all Maven projects
   
3. **Wait for Indexing**
   - IntelliJ will re-index all dependencies
   - Watch the progress bar at the bottom of the IDE
   - This may take 1-2 minutes

4. **Verify**
   - The red underlines in UserControllerTest.java should disappear
   - Imports should now be recognized

### Method 2: Invalidate Caches

If Method 1 doesn't work:

1. **File ? Invalidate Caches...**
2. Check "Clear file system cache and Local History"
3. Check "Clear downloaded shared indexes"
4. Click "Invalidate and Restart"
5. Wait for IntelliJ to restart and re-index

### Method 3: Reimport Maven Project

1. **Right-click on `pom.xml`** in the Project view
2. Select **Maven ? Reimport**
3. Wait for the process to complete

### Method 4: Use the Helper Script

Run the provided batch script:

```cmd
reload-maven.bat
```

This script will:
- Clean the project
- Download all dependencies
- Compile the project
- Remind you to reload Maven in IntelliJ

### Method 5: Manual Steps

1. **Delete target folder**
   ```cmd
   rmdir /s /q target
   ```

2. **Clean and compile**
   ```cmd
   .\mvnw.cmd clean compile
   ```

3. **Reload Maven in IntelliJ** (see Method 1)

## Verification

After reloading, verify the dependencies are recognized:

1. Open `UserControllerTest.java`
2. All imports should be recognized (no red underlines)
3. Press `Ctrl + Space` on `@WebMvcTest` - you should see autocomplete suggestions
4. Right-click on the test class ? Run 'UserControllerTest' should work

## Check Dependencies are Downloaded

Run this command to verify all dependencies are present:

```cmd
.\mvnw.cmd dependency:tree
```

Look for these key test dependencies:
- `spring-boot-starter-test`
- `mockito-core`
- `mockito-junit-jupiter`

## If Problem Persists

### Check Maven Settings in IntelliJ

1. **File ? Settings ? Build, Execution, Deployment ? Build Tools ? Maven**
2. Verify "Maven home directory" points to the bundled Maven or your local installation
3. Check "User settings file" is correct
4. Click "OK" and reload Maven project

### Check JDK Configuration

1. **File ? Project Structure ? Project**
2. Verify "SDK" is set to Java 17 or higher
3. Verify "Language level" is set to 17 or higher

### Check Module Dependencies

1. **File ? Project Structure ? Modules**
2. Select your module
3. Go to "Dependencies" tab
4. Ensure all Maven dependencies are listed
5. If not, click "+" ? "Library" ? "From Maven" and add them manually

### Enable Maven Auto-Import

1. **File ? Settings ? Build, Execution, Deployment ? Build Tools ? Maven**
2. Check "Reload project after changes in the build scripts"
3. Select "Any changes"

## Common Issues

### Issue: "Cannot resolve symbol" persists after reload

**Solution**: The IDE cache might be corrupted
- Use Method 2 (Invalidate Caches)

### Issue: Maven home directory not found

**Solution**: Configure Maven home
1. File ? Settings ? Maven
2. Set "Maven home directory" to the bundled Maven or download Maven manually

### Issue: Dependencies show in Maven tool window but not in code

**Solution**: Module not recognized as Maven project
1. Right-click on `pom.xml`
2. Select "Add as Maven Project"

## Quick Reference Commands

```cmd
# Clean project
.\mvnw.cmd clean

# Download all dependencies
.\mvnw.cmd dependency:resolve

# Compile project
.\mvnw.cmd compile

# Run tests
.\mvnw.cmd test

# Show dependency tree
.\mvnw.cmd dependency:tree
```

## Expected Result

After completing these steps:

? No red underlines in UserControllerTest.java
? All imports recognized
? Autocomplete works for Spring and Mockito annotations
? Tests can be run from IDE
? No "Cannot resolve symbol" errors

## Still Having Issues?

1. Check your internet connection (Maven needs to download dependencies)
2. Check if a proxy is blocking Maven Central repository
3. Delete `~/.m2/repository` and re-download all dependencies (nuclear option)
4. Restart IntelliJ IDEA
5. Check IntelliJ IDEA logs: Help ? Show Log in Explorer

---

**Note**: The dependencies ARE correctly configured in your `pom.xml` and have been successfully downloaded. This is purely an IDE indexing issue that will be resolved by reloading the Maven project.

