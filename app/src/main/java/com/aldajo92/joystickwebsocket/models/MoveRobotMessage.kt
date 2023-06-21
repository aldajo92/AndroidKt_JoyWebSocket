package com.aldajo92.joystickwebsocket.models

import com.squareup.moshi.Json

data class MoveRobotMessage(
    @Json(name = "steering") val steering: Float,
    @Json(name = "throttle") val throttle: Float,
    @Json(name = "pan") val pan: Float = 0f,
    @Json(name = "tilt") val tilt: Float = 0f
)
