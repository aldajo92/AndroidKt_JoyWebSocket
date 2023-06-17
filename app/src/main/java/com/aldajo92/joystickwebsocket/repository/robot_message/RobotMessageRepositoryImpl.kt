package com.aldajo92.joystickwebsocket.repository.robot_message

import com.aldajo92.joystickwebsocket.framework.SocketManager
import com.aldajo92.joystickwebsocket.models.MoveRobotMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RobotMessageRepositoryImpl : RobotMessageRepository {

    private var socketManager: SocketManager? = null

    override fun startConnection(urlPath: String): Boolean {
        socketManager = SocketManager(urlPath)
        socketManager?.connect()
        return true
    }

    override fun sendMessage(channel: String, messageObject: MoveRobotMessage) {
        socketManager?.sendData(channel, messageObject)
    }

    override fun endConnection(): Boolean = socketManager?.let {
        it.disconnect()
        true
    } ?: false

}
