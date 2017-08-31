package com.vincentcarrier.filmtastic.di

import com.vincentcarrier.filmtastic.ui.details.DetailsViewModel
import com.vincentcarrier.filmtastic.ui.moviegrid.MovieGridViewModel
import dagger.Component
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {
	fun inject(viewModel: MovieGridViewModel)
	fun inject(viewModel: DetailsViewModel)

	val client: OkHttpClient
}
