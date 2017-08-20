package com.vincentcarrier.filmtastic.di

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.vincentcarrier.filmtastic.TheMovieDbApi
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetModule {

	@Provides
	@Singleton
	fun provideTheMovieDbApi(retrofit: Retrofit): TheMovieDbApi {
		return retrofit.create<TheMovieDbApi>(TheMovieDbApi::class.java)
	}

	@Provides
	@Singleton
	fun provideRetrofit(client: OkHttpClient): Retrofit {
		return Retrofit.Builder()
				.client(client)
				.baseUrl("http://api.themoviedb.org/3/movie/")
				.addConverterFactory(MoshiConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
				.build()
	}

	@Provides
	@Singleton
	fun provideOkHttpClient(): OkHttpClient {
		/** Append the API key to every Retrofit request. If you downloaded this project
		 * from GitHub, you must replace the API_KEY constant with your own API key,
		 * which you can obtain from themoviedb.org */
		return OkHttpClient.Builder()
				.addInterceptor { chain ->
					val original = chain.request()
					val url = original.url().newBuilder().addQueryParameter("api_key", API_KEY).build()
					val requestBuilder = original.newBuilder().url(url)
					val request = requestBuilder.build()
					chain.proceed(request)
				}
				.build()
	}
}
