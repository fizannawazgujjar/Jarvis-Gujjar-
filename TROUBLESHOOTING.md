# Troubleshooting Guide

## Common Issues and Solutions

### Build & Compilation

#### Issue: Gradle sync fails
**Solution:**
1. Check internet connection
2. Clear cache: `./gradlew clean`
3. Invalidate Android Studio cache: File → Invalidate Caches
4. Restart Android Studio
5. Check Gradle version compatibility

#### Issue: Cannot resolve symbol errors
**Solution:**
1. Run Gradle sync: File → Sync Now
2. Check import statements
3. Rebuild project: Build → Clean Project → Build Project
4. Check minSdk and compileSdk settings

#### Issue: Kotlin compilation errors
**Solution:**
1. Check Kotlin version in build.gradle.kts
2. Update IntelliJ Kotlin plugin
3. Check KSP/KAPT configuration
4. Verify @Suppress annotations if needed

### Runtime Issues

#### Issue: App crashes on startup
**Solution:**
1. Check logcat for error messages
2. Verify AndroidManifest.xml
3. Check Hilt DI configuration
4. Ensure all Hilt modules are properly configured
5. Clear app data: Settings → Apps → JARVIS → Clear Data
6. Reinstall app

#### Issue: Permission denied errors
**Solution:**
1. Grant permissions in system settings
2. Check if permission is declared in AndroidManifest.xml
3. Request runtime permissions for API 23+
4. Verify permission name spelling
5. Check permission compatibility

#### Issue: Database errors
**Solution:**
1. Check Room entity definitions
2. Verify database version
3. Check DAO queries
4. Use database inspector in Android Studio
5. Clear app database and reinstall

### Voice & Audio

#### Issue: Speech recognition not working
**Solution:**
1. Grant RECORD_AUDIO permission
2. Check microphone hardware
3. Test microphone in system settings
4. Check language settings
5. Verify SpeechRecognizer availability
6. Check internet connectivity

#### Issue: Text-to-speech not working
**Solution:**
1. Install TTS engine: Settings → Language & input → Text-to-speech
2. Select preferred TTS engine
3. Check language availability
4. Verify volume settings
5. Test with system TTS

### Camera & Vision

#### Issue: Camera not opening
**Solution:**
1. Grant CAMERA permission
2. Check device has camera
3. Verify CameraX setup
4. Check camera availability
5. Restart app after granting permission
6. Check for camera conflicts with other apps

#### Issue: OCR not recognizing text
**Solution:**
1. Ensure image is clear
2. Check lighting
3. Hold camera steady
4. Verify ML Kit download
5. Check language support
6. Use close-up of text

### Network & APIs

#### Issue: API key not working
**Solution:**
1. Verify API key is valid
2. Check with provider's dashboard
3. Ensure API is enabled
4. Check API rate limits
5. Verify HTTPS connectivity
6. Check firewall/VPN issues

#### Issue: Network timeout errors
**Solution:**
1. Check internet connection
2. Verify API endpoint is reachable
3. Increase timeout duration
4. Check data usage
5. Verify API status page
6. Check proxy settings

#### Issue: SSL/Certificate errors
**Solution:**
1. Check internet time/date
2. Verify HTTPS certificate
3. Check network security config
4. Try different network
5. Clear app cache

### UI & Display

#### Issue: UI not updating
**Solution:**
1. Check StateFlow emission
2. Verify collectAsState() usage
3. Check Composable recomposition
4. Verify ViewModel scope
5. Use Android Profiler to debug

#### Issue: Navigation not working
**Solution:**
1. Verify routes in NavHost
2. Check route arguments
3. Verify navController initialization
4. Check NavBackStackEntry
5. Use Navigation testing APIs

#### Issue: Theme not applying
**Solution:**
1. Check Theme function parameters
2. Verify Material 3 setup
3. Check color definitions
4. Rebuild after theme changes
5. Clear app cache

### Storage & Database

#### Issue: DataStore not persisting data
**Solution:**
1. Check DataStore initialization
2. Verify file permissions
3. Check disk space
4. Verify key definitions
5. Use Database Inspector

#### Issue: Room database migration issues
**Solution:**
1. Increment database version
2. Implement migration if schema changed
3. Use fallbackToDestructiveMigration() for development
4. Verify entity annotations
5. Check DAO implementations

### Performance

#### Issue: App is slow/laggy
**Solutions:**
1. Profile with Android Profiler
2. Check for ANR errors in logcat
3. Monitor memory usage
4. Check for excessive recomposition
5. Optimize database queries
6. Use LazyColumn/LazyRow for lists
7. Implement pagination

#### Issue: High memory usage
**Solutions:**
1. Check for memory leaks
2. Use WeakReference where appropriate
3. Clear caches properly
4. Close resources in finally blocks
5. Use Android Profiler
6. Check for circular references

### Testing

#### Issue: Tests failing locally
**Solution:**
1. Run tests from clean build
2. Check test dependencies
3. Verify mocking setup
4. Check test resource files
5. Use logcat in tests
6. Run tests with verbose output

#### Issue: UI tests failing
**Solution:**
1. Verify Compose test setup
2. Check screen synchronization
3. Use proper waits
4. Verify test device state
5. Check for timing issues
6. Enable verbose logging

## Debugging Techniques

### Logcat Filtering
```bash
# Filter by app
adb logcat | grep com.jarvis.controller

# Filter by priority
adb logcat *:E  # Only errors
adb logcat *:W  # Warnings and errors

# Clear logcat
adb logcat -c

# Save to file
adb logcat > logcat.txt
```

### Using Timber
```kotlin
Timber.d("Debug message")
Timber.i("Info message")
Timber.w("Warning message")
Timber.e(exception, "Error message")
```

### Android Studio Debugging
1. Set breakpoints by clicking line number
2. Run → Debug 'app'
3. Step through code
4. Inspect variables
5. Evaluate expressions
6. View call stack

### Android Profiler
1. View → Tool Windows → Profiler
2. Run app and profiler starts
3. Monitor CPU, Memory, Network
4. Check frame rate (Jank)
5. Record and analyze

### Database Inspector
1. View → Tool Windows → Database Inspector
2. Connect to running app
3. Browse Room database
4. Execute queries
5. Inspect data

### Network Monitoring
1. Android Profiler → Network tab
2. Monitor API calls
3. Check request/response times
4. Verify request headers
5. Check payload sizes

## Getting Help

1. **Check Documentation**
   - README.md
   - DEVELOPMENT.md
   - API_REFERENCE.md

2. **Search Issues**
   - GitHub Issues
   - Stack Overflow
   - GitHub Discussions

3. **Provide Details**
   - Device model and Android version
   - Complete error logs
   - Steps to reproduce
   - Code snippets

4. **Report Issues**
   - Open GitHub issue
   - Follow issue template
   - Attach relevant files
   - Be specific and detailed

## Advanced Debugging

### Memory Leaks
```kotlin
import androidx.compose.runtime.DisposableEffect

@Composable
fun MyScreen() {
    DisposableEffect(Unit) {
        onDispose {
            // Cleanup code
        }
    }
}
```

### Coroutine Debugging
```kotlin
viewModelScope.launch {
    try {
        // Code
    } catch (e: CancellationException) {
        Timber.d("Coroutine cancelled")
    } catch (e: Exception) {
        Timber.e(e, "Coroutine error")
    }
}
```

### Compose Debugging
```kotlin
// Enable debug mode
val debugMode = true

if (debugMode) {
    Text("Debug: ${state.value}")
}
```

## FAQ

**Q: How do I clear all app data?**
A: Settings → Apps → JARVIS → Clear Storage → Clear All Data

**Q: How do I check if permission is granted?**
A: Use `ContextCompat.checkSelfPermission()`

**Q: How do I debug database queries?**
A: Use Database Inspector or add logging to DAO

**Q: How do I profile memory?**
A: Use Android Profiler Memory tab

**Q: How do I test without internet?**
A: Mock API responses in tests or use OkHttp MockWebServer

## Still Having Issues?

If you've tried all solutions:
1. Check latest version
2. Update dependencies
3. Contact support: fizannawazgujjar@gmail.com
4. Open GitHub issue with detailed logs
5. Post in GitHub Discussions

We're here to help! 🙌
