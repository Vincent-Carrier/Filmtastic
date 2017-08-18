package com.vincentcarrier.filmtastic.di

import com.vincentcarrier.filmtastic.moviegridscreen.MovieGridActivity
import com.vincentcarrier.filmtastic.moviegridscreen.MovieGridViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {
	fun inject(viewModel: MovieGridViewModel)
	fun inject(activity: MovieGridActivity)
}
