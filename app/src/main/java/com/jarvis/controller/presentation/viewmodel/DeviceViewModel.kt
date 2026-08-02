package com.jarvis.controller.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.controller.domain.model.DeviceInfo
import com.jarvis.controller.domain.usecase.GetDeviceInfoUseCase
import com.jarvis.controller.domain.usecase.ToggleFlashlightUseCase
import com.jarvis.controller.domain.usecase.SetVolumeUseCase
import com.jarvis.controller.domain.usecase.SetBrightnessUseCase
import com.jarvis.controller.domain.usecase.OpenAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class DeviceUiState(
    val deviceInfo: DeviceInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val flashlightOn: Boolean = false
)

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase,
    private val toggleFlashlightUseCase: ToggleFlashlightUseCase,
    private val setVolumeUseCase: SetVolumeUseCase,
    private val setBrightnessUseCase: SetBrightnessUseCase,
    private val openAppUseCase: OpenAppUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    init {
        loadDeviceInfo()
    }

    private fun loadDeviceInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getDeviceInfoUseCase().collect { deviceInfo ->
                    _uiState.value = _uiState.value.copy(
                        deviceInfo = deviceInfo,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
                Timber.e(e, "Error loading device info")
            }
        }
    }

    fun toggleFlashlight() {
        val newState = !_uiState.value.flashlightOn
        viewModelScope.launch {
            try {
                toggleFlashlightUseCase(newState).onSuccess {
                    _uiState.value = _uiState.value.copy(flashlightOn = newState)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error toggling flashlight"
                    )
                    Timber.e(exception, "Error toggling flashlight")
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception toggling flashlight")
            }
        }
    }

    fun setVolume(level: Int) {
        viewModelScope.launch {
            try {
                setVolumeUseCase(level).onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error setting volume"
                    )
                    Timber.e(exception, "Error setting volume")
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception setting volume")
            }
        }
    }

    fun setBrightness(level: Int) {
        viewModelScope.launch {
            try {
                setBrightnessUseCase(level).onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error setting brightness"
                    )
                    Timber.e(exception, "Error setting brightness")
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception setting brightness")
            }
        }
    }

    fun openApp(packageName: String) {
        viewModelScope.launch {
            try {
                openAppUseCase(packageName).onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error opening app"
                    )
                    Timber.e(exception, "Error opening app")
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception opening app")
            }
        }
    }
}
