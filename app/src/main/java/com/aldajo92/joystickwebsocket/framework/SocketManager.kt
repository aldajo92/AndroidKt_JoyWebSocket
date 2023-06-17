package com.aldajo92.joystickwebsocket.framework

import android.util.Log
import com.aldajo92.joystickwebsocket.models.MoveRobotMessage
import com.squareup.moshi.Moshi
import io.socket.client.IO
import io.socket.client.Socket

class SocketManager(
    private val socketPath: String
) {

    lateinit var mSocket: Socket

    private val jsonAdapter by lazy {
        Moshi.Builder().build().adapter(MoveRobotMessage::class.java)
    }

    fun connect() {
        try {
            mSocket = IO.socket(socketPath)
            Log.d("success", mSocket.id())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT) {
            Log.i(this::class.java.name, "connected")
        }
    }

    fun sendData(channel: String, src: MoveRobotMessage) {
        val jsonData = jsonAdapter.toJson(src)
        mSocket.emit(channel, jsonData)
    }

    fun disconnect() {
        mSocket.disconnect()
    }

}

const val ROBOT_MESSAGE = "robot-message"
const val ROBOT_COMMAND = "robot-command"
