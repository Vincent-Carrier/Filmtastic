package com.vincentcarrier.filmtastic.di

import com.vincentcarrier.filmtastic.ui.details.DetailsViewModel
import com.vincentcarrier.filmtastic.ui.moviegrid.MovieGridViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {
	fun inject(viewModel: MovieGridViewModel)
	fun inject(detailsViewModel: DetailsViewModel)
}
