# Android App README

This is the Android client for JARVIS (Kotlin + Jetpack Compose).

How to run in Android Studio
1. Open the `apps/android` directory as a project in Android Studio.
2. Ensure the Android SDK and Kotlin plugin are installed.
3. Run the app on an emulator or device.

Networking notes
- The app targets the backend running on your development machine. For the Android emulator, use `http://10.0.2.2:8000` to reach `localhost:8000` on the host.
- Ensure the backend is running: `uvicorn app.main:app --reload --port 8000` and docker-compose services (Postgres/Redis) if needed.

Pairing and Chat
- The app includes a simple pairing screen and a basic chat UI implemented in Compose.
- The ViewModel uses a simple ApiClient (OkHttp) to call pairing and commander endpoints.
