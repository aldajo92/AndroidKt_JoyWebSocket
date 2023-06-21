package com.aldajo92.joystickwebsocket.presentation

import androidx.lifecycle.ViewModel
import com.aldajo92.joystickwebsocket.models.MoveRobotMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InfoDialogViewModel @Inject constructor() : ViewModel() {

    private val jsonAdapter by lazy {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory()).build()
            .adapter(MoveRobotMessage::class.java)
            .indent("\t")
    }

    fun getJsonExample(): String {
        val messageObject = MoveRobotMessage(
            0f, 0f
        )
        return jsonAdapter.toJson(messageObject)
    }

}
