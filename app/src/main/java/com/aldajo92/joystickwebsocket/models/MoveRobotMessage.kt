package com.aldajo92.joystickwebsocket.models

import com.squareup.moshi.Json

data class MoveRobotMessage(
    @Json(name = "steering") val steering: Float,
    @Json(name = "throttle") val throttle: Float,
)
