package com.aldajo92.joystickwebsocket.repository.robot_message

import com.aldajo92.joystickwebsocket.framework.web_socket.SocketManager
import com.aldajo92.joystickwebsocket.framework.web_socket.SocketManagerListener
import com.aldajo92.joystickwebsocket.models.MoveRobotMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RobotMessageRepositoryImpl : RobotMessageRepository, SocketManagerListener {

    // TODO: Inject this
    private val socketManager by lazy {
        SocketManager(this)
    }

    private val jsonAdapter by lazy {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory()).build()
            .adapter(MoveRobotMessage::class.java)
    }

    private val connectionStateFlow = MutableStateFlow<ConnectionState>(
        ConnectionState.Disconnected
    )

    override suspend fun startConnection(urlPath: String): Boolean {
        socketManager.connect(urlPath)
        return true
    }

    override fun sendMessage(channel: String, messageObject: MoveRobotMessage) {
        val jsonData = jsonAdapter.toJson(messageObject)
        socketManager.sendData(channel, jsonData)
    }

    override fun endConnection() {
        socketManager.disconnect()
    }

    override fun getRobotConnectionState(): Flow<ConnectionState> = connectionStateFlow

    override fun onConnectionOpened(id: String) {
        connectionStateFlow.value = ConnectionState.Connecting(id)
    }

    override fun onConnectionStarted() {
        connectionStateFlow.value = ConnectionState.Connected
    }

    override fun onConnectionClosed() {
        connectionStateFlow.value = ConnectionState.Disconnected
    }

    override fun onConnectionError(error: String) {
        connectionStateFlow.value = ConnectionState.Error
    }

}

sealed class ConnectionState {
    class Connecting(val id: String) : ConnectionState()
    object Disconnected : ConnectionState()
    object Connected : ConnectionState()
    object Error : ConnectionState()
}
