package com.aldajo92.joystickwebsocket.repository.robot_message

import com.aldajo92.joystickwebsocket.models.MoveRobotMessage
import kotlinx.coroutines.flow.Flow

interface RobotMessageRepository {

    suspend fun startConnection(urlPath: String): Boolean

    fun sendMessage(channel: String, messageObject: MoveRobotMessage)

    fun endConnection()

    fun getRobotConnectionState(): Flow<ConnectionState>

}
