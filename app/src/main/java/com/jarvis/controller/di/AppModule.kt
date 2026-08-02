package com.jarvis.controller.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.jarvis.controller.data.db.JARVISDatabase
import com.jarvis.controller.data.db.dao.MessageDao
import com.jarvis.controller.data.local.PreferencesManager
import com.jarvis.controller.data.remote.OpenAIService
import com.jarvis.controller.data.remote.GeminiService
import com.jarvis.controller.data.repository.ChatRepositoryImpl
import com.jarvis.controller.data.repository.DeviceRepositoryImpl
import com.jarvis.controller.domain.repository.ChatRepository
import com.jarvis.controller.domain.repository.DeviceRepository
import com.jarvis.controller.domain.usecase.ClearConversationUseCase
import com.jarvis.controller.domain.usecase.GetConversationHistoryUseCase
import com.jarvis.controller.domain.usecase.GetDeviceInfoUseCase
import com.jarvis.controller.domain.usecase.OpenAppUseCase
import com.jarvis.controller.domain.usecase.SendMessageUseCase
import com.jarvis.controller.domain.usecase.SetBrightnessUseCase
import com.jarvis.controller.domain.usecase.SetVolumeUseCase
import com.jarvis.controller.domain.usecase.ToggleFlashlightUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jarvis_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): JARVISDatabase {
        return Room.databaseBuilder(
            context,
            JARVISDatabase::class.java,
            "jarvis_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideMessageDao(database: JARVISDatabase): MessageDao {
        return database.messageDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context.dataStore)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideOpenAIService(okHttpClient: OkHttpClient): OpenAIService {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(OpenAIService::class.java)
    }

    @Singleton
    @Provides
    fun provideGeminiService(okHttpClient: OkHttpClient): GeminiService {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GeminiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideChatRepository(
        messageDao: MessageDao,
        openAIService: OpenAIService,
        preferencesManager: PreferencesManager
    ): ChatRepository {
        return ChatRepositoryImpl(messageDao, openAIService, preferencesManager)
    }

    @Singleton
    @Provides
    fun provideDeviceRepository(
        @ApplicationContext context: Context
    ): DeviceRepository {
        return DeviceRepositoryImpl(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideSendMessageUseCase(chatRepository: ChatRepository): SendMessageUseCase {
        return SendMessageUseCase(chatRepository)
    }

    @Provides
    fun provideGetConversationHistoryUseCase(chatRepository: ChatRepository): GetConversationHistoryUseCase {
        return GetConversationHistoryUseCase(chatRepository)
    }

    @Provides
    fun provideClearConversationUseCase(chatRepository: ChatRepository): ClearConversationUseCase {
        return ClearConversationUseCase(chatRepository)
    }

    @Provides
    fun provideGetDeviceInfoUseCase(deviceRepository: DeviceRepository): GetDeviceInfoUseCase {
        return GetDeviceInfoUseCase(deviceRepository)
    }

    @Provides
    fun provideToggleFlashlightUseCase(deviceRepository: DeviceRepository): ToggleFlashlightUseCase {
        return ToggleFlashlightUseCase(deviceRepository)
    }

    @Provides
    fun provideSetVolumeUseCase(deviceRepository: DeviceRepository): SetVolumeUseCase {
        return SetVolumeUseCase(deviceRepository)
    }

    @Provides
    fun provideSetBrightnessUseCase(deviceRepository: DeviceRepository): SetBrightnessUseCase {
        return SetBrightnessUseCase(deviceRepository)
    }

    @Provides
    fun provideOpenAppUseCase(deviceRepository: DeviceRepository): OpenAppUseCase {
        return OpenAppUseCase(deviceRepository)
    }
}
