# Android client skeleton

This folder will contain the Android app built with Kotlin and Jetpack Compose.
For now it contains a brief roadmap and initial file layout guidance.

Structure (to be created):
- apps/android/
  - app/ (Gradle module)
  - build.gradle
  - settings.gradle

Android decisions made:
- Native Kotlin + Jetpack Compose for best UX and performance.
- Use WorkManager for background tasks and proper permissions handling.
- Use ML Kit/Coqui/VOSK for offline speech where available.

Next steps:
- Create a Compose-based chat UI, device pairing flow, and permissions manager.
- Implement voice wake integration using a local wake-word engine.
