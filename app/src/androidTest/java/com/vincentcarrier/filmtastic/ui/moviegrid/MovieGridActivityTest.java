package com.vincentcarrier.filmtastic.ui.moviegrid;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.jakewharton.espresso.OkHttp3IdlingResource;
import com.vincentcarrier.filmtastic.App;
import com.vincentcarrier.filmtastic.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MovieGridActivityTest {

	@Rule
	public ActivityTestRule<MovieGridActivity> mActivityTestRule = new ActivityTestRule<>(MovieGridActivity.class);

	@Test
	public void movieGridActivityTest() {

		IdlingResource resource = OkHttp3IdlingResource.create("OkHttp", App.netComponent.getClient());
		Espresso.registerIdlingResources(resource);

		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.movieGrid),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										0),
								1),
						isDisplayed()));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup && parentMatcher.matches(parent)
						&& view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}
