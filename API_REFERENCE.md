# JARVIS Mobile Controller - API Reference

## Overview

Complete API reference for JARVIS Mobile Controller development.

## Domain Layer

### Models

#### Message
```kotlin
data class Message(
    val id: String,
    val content: String,
    val role: String,
    val timestamp: Long
)
```

#### Conversation
```kotlin
data class Conversation(
    val id: String,
    val title: String,
    val messages: List<Message>,
    val createdAt: Long,
    val updatedAt: Long
)
```

#### AIResponse
```kotlin
data class AIResponse(
    val content: String,
    val model: String,
    val timestamp: Long
)
```

#### DeviceInfo
```kotlin
data class DeviceInfo(
    val batteryLevel: Int,
    val batteryHealth: String,
    val storageUsed: Long,
    val storageFree: Long,
    val ramUsed: Long,
    val ramTotal: Long,
    val wifiConnected: Boolean,
    val bluetoothOn: Boolean,
    val mobileDataOn: Boolean
)
```

### Repositories

#### ChatRepository
```kotlin
interface ChatRepository {
    suspend fun sendMessage(message: Message): Result<AIResponse>
    fun getConversationHistory(): Flow<List<Message>>
    suspend fun saveMessage(message: Message)
    suspend fun clearHistory()
}
```

#### DeviceRepository
```kotlin
interface DeviceRepository {
    fun getDeviceInfo(): Flow<DeviceInfo>
    suspend fun toggleFlashlight(enable: Boolean): Result<Unit>
    suspend fun setVolume(level: Int): Result<Unit>
    suspend fun setBrightness(level: Int): Result<Unit>
    suspend fun openApp(packageName: String): Result<Unit>
}
```

### Use Cases

#### SendMessageUseCase
```kotlin
class SendMessageUseCase(chatRepository: ChatRepository)
suspend operator fun invoke(content: String): Result<AIResponse>
```

#### GetConversationHistoryUseCase
```kotlin
class GetConversationHistoryUseCase(chatRepository: ChatRepository)
operator fun invoke(): Flow<List<Message>>
```

#### ToggleFlashlightUseCase
```kotlin
class ToggleFlashlightUseCase(deviceRepository: DeviceRepository)
suspend operator fun invoke(enable: Boolean): Result<Unit>
```

#### SetVolumeUseCase
```kotlin
class SetVolumeUseCase(deviceRepository: DeviceRepository)
suspend operator fun invoke(level: Int): Result<Unit>
```

#### SetBrightnessUseCase
```kotlin
class SetBrightnessUseCase(deviceRepository: DeviceRepository)
suspend operator fun invoke(level: Int): Result<Unit>
```

#### OpenAppUseCase
```kotlin
class OpenAppUseCase(deviceRepository: DeviceRepository)
suspend operator fun invoke(packageName: String): Result<Unit>
```

## Data Layer

### Database

#### JARVISDatabase
```kotlin
@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JARVISDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
```

#### MessageDao
```kotlin
@Dao
interface MessageDao {
    suspend fun insert(message: MessageEntity)
    suspend fun update(message: MessageEntity)
    suspend fun delete(message: MessageEntity)
    fun getAllMessages(): Flow<List<MessageEntity>>
    suspend fun getMessageById(id: String): MessageEntity?
    suspend fun deleteAll()
}
```

### Local Storage

#### PreferencesManager
```kotlin
class PreferencesManager(dataStore: DataStore<Preferences>)

suspend fun setOpenaiApiKey(key: String)
suspend fun setGeminiApiKey(key: String)
suspend fun setSelectedAiProvider(provider: String)
suspend fun setThemeMode(mode: String)
suspend fun setVoiceLanguage(language: String)

val openaiApiKey: Flow<String?>
val geminiApiKey: Flow<String?>
val selectedAiProvider: Flow<String?>
val themeMode: Flow<String?>
val voiceLanguage: Flow<String?>
```

### Remote APIs

#### OpenAIService
```kotlin
interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}
```

#### GeminiService
```kotlin
interface GeminiService {
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
```

## Presentation Layer

### ViewModels

#### ChatViewModel
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    sendMessageUseCase: SendMessageUseCase,
    getConversationHistoryUseCase: GetConversationHistoryUseCase,
    clearConversationUseCase: ClearConversationUseCase
) : ViewModel()

val uiState: StateFlow<ChatUiState>
fun sendMessage(content: String)
fun updateInputText(text: String)
fun clearHistory()
```

#### DeviceViewModel
```kotlin
@HiltViewModel
class DeviceViewModel @Inject constructor(
    getDeviceInfoUseCase: GetDeviceInfoUseCase,
    toggleFlashlightUseCase: ToggleFlashlightUseCase,
    setVolumeUseCase: SetVolumeUseCase,
    setBrightnessUseCase: SetBrightnessUseCase,
    openAppUseCase: OpenAppUseCase
) : ViewModel()

val uiState: StateFlow<DeviceUiState>
fun toggleFlashlight()
fun setVolume(level: Int)
fun setBrightness(level: Int)
fun openApp(packageName: String)
```

#### SettingsViewModel
```kotlin
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel()

val uiState: StateFlow<SettingsUiState>
fun setAiProvider(provider: String)
fun setTheme(theme: String)
fun setLanguage(language: String)
fun setVoiceEnabled(enabled: Boolean)
```

### Screens

#### HomeScreen
```kotlin
@Composable
fun HomeScreen(navController: NavHostController)
```

#### ChatScreen
```kotlin
@Composable
fun ChatScreen(navController: NavHostController)
```

#### SettingsScreen
```kotlin
@Composable
fun SettingsScreen(navController: NavHostController)
```

#### DeviceControlScreen
```kotlin
@Composable
fun DeviceControlScreen(navController: NavHostController)
```

### Components

#### ExpandableCard
```kotlin
@Composable
fun ExpandableCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
)
```

#### ActionButton
```kotlin
@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

### Utilities

#### AppPackages
```kotlin
object AppPackages {
    const val GOOGLE_CAMERA: String
    const val PIXEL_CAMERA: String
    const val GALLERY: String
    const val CHROME: String
    const val YOUTUBE: String
    const val GMAIL: String
    const val CALCULATOR: String
    const val CONTACTS: String
    const val CALENDAR: String
    const val MAPS: String
    const val WHATSAPP: String
    const val TELEGRAM: String
    const val FILE_MANAGER: String
}
```

#### IntentHelper
```kotlin
object IntentHelper {
    fun makeCall(context: Context, phoneNumber: String)
    fun sendSMS(context: Context, phoneNumber: String, message: String)
    fun sendEmail(context: Context, email: String, subject: String, body: String)
    fun shareText(context: Context, text: String)
    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String)
    fun openTelegramChat(context: Context, username: String)
}
```

#### TimeFormatter
```kotlin
object TimeFormatter {
    fun formatTimestamp(timestamp: Long): String
}
```

## Constants

```kotlin
object Constants {
    const val DATABASE_NAME = "jarvis_database"
    const val PREFERENCES_NAME = "jarvis_prefs"

    object AI {
        const val DEFAULT_MODEL = "gpt-3.5-turbo"
        const val GEMINI_MODEL = "gemini-pro"
        const val DEFAULT_TEMPERATURE = 0.7
        const val DEFAULT_MAX_TOKENS = 2048
    }
}
```

## Services

### VoiceAssistantService
```kotlin
class VoiceAssistantService : Service() {
    companion object {
        const val ACTION_START_LISTENING = "com.jarvis.controller.START_LISTENING"
        const val ACTION_STOP_LISTENING = "com.jarvis.controller.STOP_LISTENING"
    }
}
```

### FloatingWidgetService
```kotlin
class FloatingWidgetService : Service()
```

## Broadcast Receivers

### BootReceiver
```kotlin
class BootReceiver : BroadcastReceiver()
```

### NotificationReceiver
```kotlin
class NotificationReceiver : BroadcastReceiver()
```

## Dependency Injection

### Hilt Modules

- `DatabaseModule` - Room database provision
- `DataStoreModule` - DataStore preferences
- `NetworkModule` - Retrofit services
- `RepositoryModule` - Repository implementations
- `UseCaseModule` - Use case provision

## Navigation

```kotlin
@Composable
fun JARVISNavHost(navController: NavHostController)

// Routes
composable("home")
composable("chat")
composable("settings")
composable("devices")
```

## State Management

### ChatUiState
```kotlin
data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)
```

### DeviceUiState
```kotlin
data class DeviceUiState(
    val deviceInfo: DeviceInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val flashlightOn: Boolean = false
)
```

### SettingsUiState
```kotlin
data class SettingsUiState(
    val aiProvider: String = "OpenAI",
    val theme: String = "Dark",
    val language: String = "English",
    val voiceEnabled: Boolean = true,
    val error: String? = null
)
```
