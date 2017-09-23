package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.airbnb.epoxy.Typed2EpoxyController
import com.vincentcarrier.filmtastic.data.TrailersManager
import com.vincentcarrier.filmtastic.data.WatchlistManager
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.models.Trailer
import com.vincentcarrier.filmtastic.ui.subscribeWithDefaults
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread

class DetailsViewModel(private val watchlistManager: WatchlistManager,
                       private val trailersManager: TrailersManager) : ViewModel() {

	lateinit internal var movie: Movie
	private var trailers: List<Trailer> = emptyList()

	private val controller = DetailsController(movie)
	internal fun adapter() = controller.adapter

	internal fun addMovieToWatchList(): Completable {
		return watchlistManager.postMovieToWatchList(movie.id)
				.observeOn(mainThread())
	}

	internal fun requestMovieTrailers(movieId: Int) {
		trailersManager.requestMovieTrailers(movie.id)
				.subscribeWithDefaults {
					trailers = it
					controller.setData(movie, trailers)
				}
	}

	/* Airbnb's RecyclerView.Adapter replacement */
	class DetailsController(movie: Movie) : Typed2EpoxyController<Movie, List<Trailer>>() {
		private val detailsModel = DetailsModel_(movie)
		internal var trailers = emptyList<Trailer>()
			set(value) {
				field = value
				requestModelBuild()
			}

		override fun buildModels(movie: Movie, trailers: List<Trailer>) {
			detailsModel.id("details").addTo(this)
			for (trailer in trailers) TrailerModel_(trailer).id(trailer.key).addTo(this)
		}
	}
}

@Suppress("UNCHECKED_CAST")
class DetailsVmFactory(private val watchlistManager: WatchlistManager,
                       private val trailersManager: TrailersManager) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return DetailsViewModel(watchlistManager, trailersManager) as T
	}
}