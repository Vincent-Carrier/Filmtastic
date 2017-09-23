package com.vincentcarrier.filmtastic.ui

import android.arch.lifecycle.LifecycleOwner
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.GlideApp
import com.vincentcarrier.filmtastic.R
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxkotlin.subscribeBy

private val posterWidths = mapOf<String, String>(
		"xsmall" to "92",
		"small" to "154",
		"medium" to "185",
		"large" to "342",
		"xlarge" to "500",
		"xxlarge" to "780",
		"original" to "original")

internal fun ImageView.loadPoster(posterPath: String?) {
	val baseUrl = "https://image.tmdb.org/t/p/w"
	GlideApp.with(this.context)
			.load(baseUrl + posterWidths["xxlarge"] + posterPath)
			.placeholder(R.drawable.poster_placeholder)
			.transition(withCrossFade())
			.into(this)
}

internal fun <T : Any> Single<T>.subscribeWithLifecycle(
		lifecycle: LifecycleOwner,
		onError: (Throwable) -> Unit = { it.printStackTrace() },
		onSuccess: (T) -> Unit) {
	observeOn(mainThread())
			.bindToLifecycle(lifecycle)
			.subscribeBy(
					onSuccess = onSuccess,
					onError = onError
			)
}

internal fun Completable.subscribeWithLifecycle(
		lifecycle: LifecycleOwner,
		onError: (Throwable) -> Unit = { it.printStackTrace() },
		onComplete: () -> Unit) {
	bindToLifecycle<LifecycleOwner>(lifecycle) // Not sure why it wants the type but w/e
			.subscribeBy(
					onComplete = onComplete,
					onError = onError
			)
}

internal fun <T : Any> Observable<T>.subscribeWithLifecycle(
		lifecycle: LifecycleOwner,
		onError: (Throwable) -> Unit = { it.printStackTrace() },
		onComplete: () -> Unit = {},
		onNext: (T) -> Unit) {
	bindToLifecycle(lifecycle)
			.subscribeBy(
					onNext = onNext,
					onComplete = onComplete,
					onError = onError
			)
}

internal fun <T : Any> Single<T>.subscribeWithDefaults(
		onError: (Throwable) -> Unit = { it.printStackTrace() },
		onSuccess: (T) -> Unit) {
	observeOn(mainThread())
			.subscribeBy(
					onSuccess = onSuccess,
					onError = onError
			)
}