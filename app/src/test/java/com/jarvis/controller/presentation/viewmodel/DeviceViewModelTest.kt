package com.jarvis.controller.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.jarvis.controller.domain.usecase.GetDeviceInfoUseCase
import com.jarvis.controller.domain.usecase.ToggleFlashlightUseCase
import com.jarvis.controller.domain.model.DeviceInfo
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DeviceViewModelTest {
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase = mock()
    private val toggleFlashlightUseCase: ToggleFlashlightUseCase = mock()
    private val setVolumeUseCase: com.jarvis.controller.domain.usecase.SetVolumeUseCase = mock()
    private val setBrightnessUseCase: com.jarvis.controller.domain.usecase.SetBrightnessUseCase = mock()
    private val openAppUseCase: com.jarvis.controller.domain.usecase.OpenAppUseCase = mock()

    private lateinit var viewModel: DeviceViewModel

    @Before
    fun setup() {
        val deviceInfo = DeviceInfo(
            batteryLevel = 85,
            batteryHealth = "Good",
            storageUsed = 10000000000L,
            storageFree = 64000000000L,
            ramUsed = 2000000000L,
            ramTotal = 8000000000L,
            wifiConnected = true,
            bluetoothOn = false,
            mobileDataOn = true
        )
        whenever(getDeviceInfoUseCase()).thenReturn(flowOf(deviceInfo))
        viewModel = DeviceViewModel(
            getDeviceInfoUseCase,
            toggleFlashlightUseCase,
            setVolumeUseCase,
            setBrightnessUseCase,
            openAppUseCase
        )
    }

    @Test
    fun testDeviceInfoLoaded() = runTest {
        assert(viewModel.uiState.value.deviceInfo != null)
        assert(viewModel.uiState.value.deviceInfo?.batteryLevel == 85)
    }
}
