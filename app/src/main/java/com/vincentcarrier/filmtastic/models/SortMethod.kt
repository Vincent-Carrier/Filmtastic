package com.vincentcarrier.filmtastic.models

import android.support.annotation.StringRes
import com.vincentcarrier.filmtastic.R

enum class SortMethod(@StringRes val stringResource: Int) {
	popular(R.string.popular), top_rated(R.string.top_rated);
}
