package com.aldajo92.joystickwebsocket.di

import com.aldajo92.joystickwebsocket.framework.validation.FieldValidator
import com.aldajo92.joystickwebsocket.framework.validation.IPValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ValidationModule {

    @Provides
    @Singleton
    @Named("ip")
    fun providesEmailValidation(): FieldValidator = IPValidator()

}
