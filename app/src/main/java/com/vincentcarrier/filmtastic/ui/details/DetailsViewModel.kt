package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.airbnb.epoxy.Typed2EpoxyController
import com.vincentcarrier.filmtastic.data.TheMovieDbService
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.models.Trailer
import io.reactivex.Completable
import io.reactivex.Single

class DetailsViewModel(private val service: TheMovieDbService) : ViewModel() {

	lateinit internal var movie: Movie
	private var trailers: List<Trailer> = emptyList()

	private val controller = DetailsController()
	internal fun adapter() = controller.adapter

	internal fun requestMovieTrailers(): Single<List<Trailer>> {
		return service.requestMovieTrailers(movie.id)
				.doOnSuccess {
					trailers = it
					controller.setData(movie, trailers)
				}
	}

	internal fun addMovieToWatchList(): Completable {
		return service.postMovieToWatchList(movie.id)
	}

	inner class DetailsController : Typed2EpoxyController<Movie, List<Trailer>>() {
		override fun buildModels(movie: Movie, trailers: List<Trailer>) {
			headerView {
				id(movie.id)
				title(movie.title)
				posterPath(movie.posterPath)
				releaseDate(movie.releaseDate.substring(0, 4))
				voteAverage("${movie.voteAverage}/10")
				overview(movie.overview)
				clickListener { addMovieToWatchList() }
			}
			trailers.forEach { trailer ->
				trailerView {
					id(trailer.key)
					name(trailer.name)
					key(trailer.key)
				}
			}
		}
	}
}

class DetailsVmFactory(private val service: TheMovieDbService) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		@Suppress("UNCHECKED_CAST") return DetailsViewModel(service) as T
	}
}