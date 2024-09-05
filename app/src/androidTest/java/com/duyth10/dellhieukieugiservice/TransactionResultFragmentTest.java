package com.duyth10.dellhieukieugiservice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static org.hamcrest.Matchers.allOf;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.duyth10.dellhieukieugiservice.ui.MainActivity;
import com.duyth10.dellhieukieugiservice.ui.TransactionResultFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TransactionResultFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private FragmentScenario<TransactionResultFragment> fragmentScenario;

    @Before
    public void setup() {

        Bundle bundle = new Bundle();
        bundle.putString("qrData", "Sample QR Code Data");
        bundle.putString("textFromMain", "Sample Main Text");

        fragmentScenario = FragmentScenario.launchInContainer(TransactionResultFragment.class, bundle);
    }

    @Test
    public void testUIElements() {

        onView(withId(R.id.qrDataTextView))
                .check(matches(withText("Sample QR Code Data")));

        onView(withId(R.id.textMonney))
                .check(matches(withText("Sample Main Text")));
    }

    @Test
    public void testClickCheckV() {

        onView(withId(R.id.imageView)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        Intent expectedIntent = new Intent();
        expectedIntent.setComponent(new ComponentName("com.duyth10.dellhieukieugi", "com.duyth10.dellhieukieugi.MainActivity"));
        expectedIntent.putExtra("qrData", "Sample QR Code Data");
        expectedIntent.putExtra("textFromMain", "Sample Main Text");


        //  intended(allOf(hasComponent(expectedIntent.getComponent()), hasExtra("qrData", "Sample QR Code Data")));
    }


    @Test
    public void testStopDataProcessingService() {
        fragmentScenario.onFragment(fragment -> {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DataProcessingService.class);
            fragment.getActivity().startService(intent);
            fragment.stopDataProcessingService();
        });
    }


}
