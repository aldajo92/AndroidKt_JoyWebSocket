package com.aldajo92.joystickwebsocket.repository.robot_message

import com.aldajo92.joystickwebsocket.models.MoveRobotMessage

interface RobotMessageRepository {

    fun startConnection(urlPath: String): Boolean

    fun sendMessage(channel: String, messageObject: MoveRobotMessage)

    fun endConnection(): Boolean

}
