package com.vincentcarrier.filmtastic

import android.app.Application
import android.content.SharedPreferences
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import io.reactivex.plugins.RxJavaPlugins
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

class App : Application(), AnkoLogger {

	companion object {
		private lateinit var app: App
		fun sharedPrefs(): SharedPreferences = app.getSharedPreferences("prefs", MODE_PRIVATE)
	}

	init {
		app = this
	}

	override fun onCreate() {
		super.onCreate()
		RxJavaPlugins.setErrorHandler { warn(it.localizedMessage, it) }
	}
}

@GlideModule
class MyAppGlideModule : AppGlideModule()