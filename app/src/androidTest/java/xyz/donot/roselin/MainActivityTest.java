package xyz.donot.roselin;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);
    @Test
    public void check_start_activity() {
        MainActivity activity = activityTestRule.launchActivity(null);
        assertThat(
                "MainActivity is running",
                activity.isFinishing(),
                Matchers.is(false)
        );
    }
}