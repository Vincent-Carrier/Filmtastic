package com.vincentcarrier.filmtastic

import android.app.Application
import com.vincentcarrier.filmtastic.di.DaggerNetComponent
import com.vincentcarrier.filmtastic.di.NetComponent

class App : Application() {

	companion object {
		lateinit var netComponent: NetComponent
	}

	override fun onCreate() {
		super.onCreate()
		netComponent = DaggerNetComponent.create()
	}
}
