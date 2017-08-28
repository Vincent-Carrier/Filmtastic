package com.vincentcarrier.filmtastic.pojos

import android.support.annotation.StringRes
import com.vincentcarrier.filmtastic.R


enum class SortingMethod(@StringRes val stringResource: Int) {
	popular(R.string.popular), top_rated(R.string.top_rated);
}
