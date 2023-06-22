package com.aldajo92.joystickwebsocket.models

import java.sql.Timestamp

data class JoystickValues(
    val valueX: Float = 0F,
    val valueY: Float = 0F,
    val velocity: Float = 0F,
    val timestamp: Long = Timestamp(System.currentTimeMillis()).time
)
