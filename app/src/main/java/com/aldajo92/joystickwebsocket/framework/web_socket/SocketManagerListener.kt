package com.aldajo92.joystickwebsocket.framework.web_socket

interface SocketManagerListener {

    fun onConnectionOpened(id: String)

    fun onConnectionStarted()

    fun onConnectionClosed()

    fun onConnectionError(error: String)

}