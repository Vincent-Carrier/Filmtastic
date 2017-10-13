package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import com.airbnb.epoxy.Typed2EpoxyController
import com.vincentcarrier.filmtastic.data.TrailersRepository
import com.vincentcarrier.filmtastic.data.WatchlistRepository
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.models.Trailer
import io.reactivex.Completable
import io.reactivex.Single

class DetailsViewModel : ViewModel() {

	lateinit internal var movie: Movie
	private var trailers: List<Trailer> = emptyList()

	private val controller = DetailsController()
	internal fun adapter() = controller.adapter

	internal fun requestMovieTrailers(): Single<List<Trailer>> {
		return TrailersRepository.requestMovieTrailers(movie.id)
				.doOnSuccess {
					trailers = it
					controller.setData(movie, trailers)
				}
	}

	internal fun addMovieToWatchList(): Completable {
		return WatchlistRepository.postMovieToWatchList(movie.id)
	}

	class DetailsController : Typed2EpoxyController<Movie, List<Trailer>>() {
		override fun buildModels(movie: Movie, trailers: List<Trailer>) {
			headerView {
				id(movie.id)
				title(movie.title)
				posterPath(movie.posterPath)
				releaseDate(movie.releaseDate.substring(0, 4))
				voteAverage("${movie.voteAverage}/10")
				overview(movie.overview)
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