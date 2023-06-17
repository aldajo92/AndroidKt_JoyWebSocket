package com.aldajo92.joystickwebsocket

import androidx.lifecycle.ViewModel
import com.aldajo92.joystickwebsocket.repository.robot_message.RobotMessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val robotMessageRepository: RobotMessageRepository
) : ViewModel() {

}
