package com.aldajo92.joystickwebsocket.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldajo92.joystickwebsocket.framework.validation.FieldValidator
import com.aldajo92.joystickwebsocket.models.JoystickValues
import com.aldajo92.joystickwebsocket.models.MoveRobotMessage
import com.aldajo92.joystickwebsocket.repository.robot_message.ConnectionState
import com.aldajo92.joystickwebsocket.repository.robot_message.RobotMessageRepository
import com.aldajo92.joystickwebsocket.repository.url.UrlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val robotMessageRepository: RobotMessageRepository,
    private val urlRepository: UrlRepository,
    @Named("ip") ipValidator: FieldValidator
) : ViewModel() {

    private var clockJob: Job? = null
    private var joystickValues = JoystickValues()

    val connectionState = robotMessageRepository
        .getRobotConnectionState()
        .map {
            if (it == ConnectionState.Connected) urlRepository.saveUrl(_ipFieldState.value)
            it
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ConnectionState.Disconnected
        )

    private val _ipFieldState = MutableStateFlow("http://192.168.1.32:5170")
    val ipFieldState = _ipFieldState.asStateFlow()

    init {
        viewModelScope.launch {
            urlRepository.getStoredUrlFlow().collect {
                if (it.isNotEmpty()) _ipFieldState.value = it
            }
        }
    }

    val isIpValid = _ipFieldState.map {
        ipValidator.isValid(it)
    }

    fun setIP(value: String) {
        _ipFieldState.value = value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun connect() {
        viewModelScope.launch {
            if (connectionState.value != ConnectionState.Connected) {
                robotMessageRepository.startConnection(_ipFieldState.value)
                startClock()
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            robotMessageRepository.endConnection()
            stopClock()
        }
    }

    fun setCurrentJoystickState(xValue: Float, yValue: Float) {
        this.joystickValues = JoystickValues(xValue, yValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startClock() {
        clockJob = if (clockJob == null) {
            tickerFlow(0.3.seconds)
                .map { LocalDateTime.now() }
                .onEach {
                    val robotMessage = MoveRobotMessage(
                        steering = joystickValues.valueY,
                        throttle = -joystickValues.valueX,
                    )
                    robotMessageRepository.sendMessage(ROBOT_COMMAND, robotMessage)
                }
                .launchIn(viewModelScope)
        } else {
            clockJob?.cancel()
            null
        }
    }

    private fun stopClock() {
        clockJob?.cancel()
        clockJob = null
    }

    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

}

const val ROBOT_COMMAND = "robot-command"
const val ROBOT_MESSAGE = "robot-message"
