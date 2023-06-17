package com.aldajo92.joystickwebsocket.di

import com.aldajo92.joystickwebsocket.repository.robot_message.RobotMessageRepository
import com.aldajo92.joystickwebsocket.repository.robot_message.RobotMessageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun providesRobotMessageRepository(): RobotMessageRepository = RobotMessageRepositoryImpl()

}
