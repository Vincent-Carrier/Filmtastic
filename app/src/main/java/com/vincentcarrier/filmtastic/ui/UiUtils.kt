package com.vincentcarrier.filmtastic.ui

import android.arch.lifecycle.LifecycleActivity
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.PosterWidth.XLARGE
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.anko.toast

internal fun ImageView.loadPoster(movie: Movie) {
	Picasso.with(this.context)
			.load("https://image.tmdb.org/t/p/w${XLARGE.width + movie.posterPath}")
			.placeholder(R.drawable.poster_placeholder)
			.into(this)
}

internal fun <T : Any> Single<T>.execute(
		activity: LifecycleActivity,
		onError: (Throwable) -> Unit = { activity.toast(it.localizedMessage) },
		onSuccess: (T) -> Unit) {
	bindToLifecycle(activity)
			.subscribeBy(
					onSuccess = onSuccess,
					onError = onError
			)
}

internal fun Completable.execute(
		activity: LifecycleActivity,
		onError: (Throwable) -> Unit = { activity.toast(it.localizedMessage) },
		onComplete: () -> Unit) {
	bindToLifecycle<LifecycleActivity>(activity) // Not sure if correct but w/e
			.subscribeBy(
					onComplete = onComplete,
					onError = onError
			)
}