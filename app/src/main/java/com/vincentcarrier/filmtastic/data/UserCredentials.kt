package com.vincentcarrier.filmtastic.data

import com.vincentcarrier.filmtastic.App

object UserCredentials {
	internal var requestToken = ""
	internal var sessionId = App.sharedPrefs().getString("sessionid", "")
		internal set(value) {
			field = value
			App.sharedPrefs().edit().putString("sessionid", value).apply()
		}
	internal var accountId = 0

	fun isLoggedIn() = sessionId.isNotEmpty()
}