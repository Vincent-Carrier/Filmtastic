package com.vincentcarrier.filmtastic

import android.app.Application
import com.vincentcarrier.filmtastic.di.DaggerNetComponent
import com.vincentcarrier.filmtastic.di.NetComponent

class Filmtastic : Application() {

	private val PREFS_NAME = "prefs"

	companion object {
		lateinit var netComponent: NetComponent
	}

	override fun onCreate() {
		super.onCreate()
		netComponent = DaggerNetComponent.create()
	}

	fun storeSessionId(sessionId: String) {
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
				.edit().putString(PREFS_NAME, sessionId).apply()
	}

	fun retrieveSessionId(): String? {
		return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
				.getString(PREFS_NAME, null)
	}

	fun isLoggedIn() = (retrieveSessionId() != null)
}
