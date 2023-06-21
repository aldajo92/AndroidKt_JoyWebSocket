package com.aldajo92.joystickwebsocket.framework.validation;

class IPValidator : FieldValidator {

//	private val IP_REGEX = """^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$"""
	private val IP_REGEX = """^(?:\d{1,3}\.){3}\d{1,3}:\d{1,5}$"""

	override fun isValid(value: String): Boolean {
//		return IP_REGEX.toRegex().matches(value)
		return true
	}
}
