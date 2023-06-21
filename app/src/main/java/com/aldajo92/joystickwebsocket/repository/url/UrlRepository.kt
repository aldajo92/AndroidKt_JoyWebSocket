package com.aldajo92.joystickwebsocket.repository.url

import kotlinx.coroutines.flow.Flow

interface UrlRepository {

    suspend fun getStoredUrl(): String

    fun saveUrl(url: String)

    fun getStoredUrlFlow(): Flow<String>

}
