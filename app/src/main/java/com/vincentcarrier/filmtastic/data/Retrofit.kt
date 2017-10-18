package com.vincentcarrier.filmtastic.data

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.vincentcarrier.filmtastic.BuildConfig
import io.reactivex.schedulers.Schedulers.io
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object Retrofit {

	internal val api = theMovieDbApi(retrofit(okHttpClient()))

	private fun theMovieDbApi(retrofit: Retrofit): TheMovieDbApi {
		return retrofit.create<TheMovieDbApi>(TheMovieDbApi::class.java)
	}

	private fun retrofit(client: OkHttpClient): Retrofit {
		return Retrofit.Builder()
				.client(client)
				.baseUrl("http://api.themoviedb.org/3/")
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(io()))
				.build()
	}

	private fun okHttpClient(): OkHttpClient {
		/** Append the API key to every Retrofit request. If you downloaded this project
		 * from GitHub, you must replace the API_KEY constant with your own API key,
		 * which you can obtain from themoviedb.org */
		return OkHttpClient.Builder()
				.addInterceptor { chain ->
					val original = chain.request()
					val url = original.url().newBuilder().addQueryParameter("api_key", API_KEY).build()
					val request = original.newBuilder().url(url).build()
					chain.proceed(request)
				}
				.addInterceptor(HttpLoggingInterceptor().apply {
					level = if (BuildConfig.DEBUG) BASIC else NONE
				})
				.build()
	}
}
