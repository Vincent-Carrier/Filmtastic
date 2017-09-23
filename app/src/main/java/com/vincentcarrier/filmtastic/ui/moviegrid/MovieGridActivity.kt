package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View.DRAWING_CACHE_QUALITY_HIGH
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.id.change_sort_method
import com.vincentcarrier.filmtastic.R.id.sign_in
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.data.MoviesManager
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import com.vincentcarrier.filmtastic.ui.loadPoster
import com.vincentcarrier.filmtastic.ui.subscribeWithLifecycle
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MovieGridActivity : AppCompatActivity(), AnkoLogger {

	private lateinit var vm: MovieGridViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		vm = ViewModelProviders.of(this, MovieGridVmFactory(MoviesManager())).get(MovieGridViewModel::class.java)
		setUpMovieGrid()
		fetchMovies()
	}

	private fun fetchMovies() {
		vm.fetchMovies()
				.subscribeWithLifecycle(this) {
					info("Fetched ${it.size} movies")
				}
	}

	override fun onStart() {
		super.onStart()
//		if (!app().isLoggedIn()) {
//			// Warm up the in-app browser to reduce loading time
//			CustomTabsClient.connectAndInitialize(this, "com.android.chrome")
//			vm.requestSessionId()?.subscribeWithLifecycle(this) {
//				app().storeSessionId(it)
//				invalidateOptionsMenu()
//			}
//		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		onPrepareOptionsMenu(menu)
		return true
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean {
		menu.findItem(change_sort_method).title = "${getString(string.sorted_by)} : ${getString(vm.sortMethod())}"
		menu.findItem(sign_in).isVisible = true
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			change_sort_method -> {
				vm.changeSortMethod()
			}
			sign_in -> {
				signIn()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun signIn() {
//		vm.requestRequestToken().subscribeWithLifecycle(this) {
//			vm.requestToken = it
//			val browser = CustomTabsIntent.Builder()
//					.setToolbarColor(ContextCompat.getColor(this, R.color.chromeToolbar))
//					.build()
//			val LOGIN_URL = "https://www.themoviedb.org/authenticate/"
//			browser.launchUrl(this, Uri.parse(LOGIN_URL + it))
//		}
	}

	private fun setUpMovieGrid() {
		with(movieGrid) {
			val isPortrait = resources.configuration.orientation == ORIENTATION_PORTRAIT
			layoutManager = GridLayoutManager(context, if (isPortrait) 2 else 4)
			adapter = vm.adapter()

			addOnScrollListener(
					InfiniteScrollListener({ fetchMovies() }, layoutManager as GridLayoutManager))

			setHasFixedSize(true)
			setItemViewCacheSize(40)
			isDrawingCacheEnabled = true
			drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH
		}
	}
}

@EpoxyModelClass(layout = R.layout.movie_grid_item)
abstract class MovieModel(movie: Movie) : EpoxyModel<ConstraintLayout>() {

	@EpoxyAttribute var posterPath: String? = movie.posterPath
	@EpoxyAttribute var title: String = movie.title

	override fun bind(view: ConstraintLayout) {
		with(view) {
			poster.loadPoster(posterPath)
			contentDescription = title
			setOnClickListener {
				context.startActivity(Intent(this.context, DetailsActivity::class.java))
			}
		}
	}
}