package com.aldajo92.joystickwebsocket.di

import android.content.Context
import com.aldajo92.joystickwebsocket.repository.robot_message.RobotMessageRepository
import com.aldajo92.joystickwebsocket.repository.robot_message.RobotMessageRepositoryImpl
import com.aldajo92.joystickwebsocket.repository.url.UrlRepository
import com.aldajo92.joystickwebsocket.repository.url.UrlRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun providesRobotMessageRepository(): RobotMessageRepository = RobotMessageRepositoryImpl()

    @Provides
    fun providesUrlRepository(
        @ApplicationContext appContext: Context
    ): UrlRepository = UrlRepositoryImpl(appContext)

}
