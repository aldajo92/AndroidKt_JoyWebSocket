package com.aldajo92.joystickwebsocket.framework

import android.util.Log
import com.aldajo92.joystickwebsocket.presentation.ROBOT_MESSAGE
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.delay

class SocketManager(
    private val socketListener: SocketManagerListener? = null
) {
    private var mSocket: Socket? = null

    private val robotMessage = Emitter.Listener {
//        val robotMessage = objectGSon.fromJson(it[0].toString(), RobotVelocityEncoder::class.java)
//        socketListener.onDataReceived(robotMessage)
    }

    suspend fun connect(socketPath: String) {
        try {
            mSocket = IO.socket(socketPath)
            Log.d("SocketManager", "success ${mSocket?.id().orEmpty()}")
            socketListener?.onConnectionOpened(mSocket?.id().orEmpty())
            delay(1000)
            mSocket?.connect()
            mSocket?.on(Socket.EVENT_CONNECT) {
                Log.i("SocketManager", "connected")
                socketListener?.onConnectionStarted()
            }
            mSocket?.on(ROBOT_MESSAGE, robotMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("SocketManager", "Failed to connect")
            socketListener?.onConnectionError(e.message ?: "Failed to connect")
        }
    }

    fun sendData(channel: String, data: String) {
        mSocket?.emit(channel, data)
    }

    fun disconnect() {
        val socket = mSocket?.disconnect()
        Log.i("SocketManager", "disconnected ${socket?.id()}")
        socketListener?.onConnectionClosed()
    }

}
