package com.vincentcarrier.filmtastic.ui.signin

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.AuthRequestToken
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class SignInViewModel : ViewModel() {

	@Inject lateinit var api: TheMovieDbApi

	init {
		App.netComponent.inject(this)
		api.fetchRequestToken()
				.observeOn(AndroidSchedulers.mainThread())
				.map(AuthRequestToken::request_token)
	}
}