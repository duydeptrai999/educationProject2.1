package com.duyth10.dellhieukieugiservice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.duyth10.dellhieukieugiservice.ui.MainActivity;
import com.duyth10.dellhieukieugiservice.ui.QRFragment;
import com.duyth10.dellhieukieugiservice.ui.TransactionResultFragment;
import com.duyth10.dellhieukieugiservice.viewmodel.MainViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


/*
*
*
* jacoco
test coverage
*
* */
public class QRFragmentTest {

    private FragmentScenario<QRFragment> fragmentScenario;
    private FragmentScenario<TransactionResultFragment> transactionResultFragmentFragmentScenario;
    private TestMainViewModel testViewModel;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    // Cấp quyền CAMERA cho test
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Before
    public void setUp() {

        testViewModel = new TestMainViewModel(ApplicationProvider.getApplicationContext());

        fragmentScenario = FragmentScenario.launchInContainer(QRFragment.class, null, androidx.appcompat.R.style.Theme_AppCompat);

        fragmentScenario.onFragment(fragment -> {
            ViewModelProvider viewModelProvider = new ViewModelProvider(fragment, new TestViewModelFactory(testViewModel));
            MainViewModel viewModel = viewModelProvider.get(MainViewModel.class);

            testViewModel.setTestScanResult("Sample QR Code Data");
            TestMainViewModel.setTextFromMain("Initial Text");

            testViewModel.getTextFromMain().observe(fragment.getViewLifecycleOwner(), text -> {
                fragment.binding.textFromMainApp.setText(text);
            });

            testViewModel.getScanResult().observe(fragment.getViewLifecycleOwner(), result -> {
                fragment.binding.textFromMainApp.setText(result);
            });

        });

        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        if (fragmentScenario != null) {
            fragmentScenario.close();
        }
        Intents.release();
    }


    public static class TestMainViewModel extends MainViewModel {
        private final MutableLiveData<String> scanResult = new MutableLiveData<>();
        private static final MutableLiveData<String> textFromMain = new MutableLiveData<>();

        public TestMainViewModel(Application application) {
            super(application);
        }

        @Override
        public MutableLiveData<String> getScanResult() {
            return scanResult;
        }

        public MutableLiveData<String> getTextFromMain() {
            return textFromMain;
        }

        public void setTestScanResult(String result) {
            scanResult.postValue(result);
        }

        public static void setTextFromMain(String result) {
            textFromMain.postValue(result);
        }
    }

    public static class TestViewModelFactory implements ViewModelProvider.Factory {
        private final ViewModel viewModel;

        public TestViewModelFactory(ViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) viewModel;
        }
    }

    @Test
    public void testDefaultUI() {
        onView(withId(R.id.desciption))
                .check(matches(withText("scan QR pls")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testTextFromMainViewModelUpdate() {
        TestMainViewModel.setTextFromMain("111111");

        onView(withId(R.id.textFromMainApp))
                .check(matches(isDisplayed()))
                .check(matches(withText("111111")));

    }

    @Test
    public void testBackButtonClick() throws InterruptedException {

        TestMainViewModel.setTextFromMain("111111");


        onView(withId(R.id.textFromMainApp))
                .check(matches(isDisplayed()))
                .check(matches(withText("111111")));

        onView(withId(R.id.iconBack))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));


        fragmentScenario.onFragment(
                qrFragment -> {

                    ImageView img = qrFragment.getView().findViewById(R.id.iconBack);
                    img.performClick();
                }
        );
        intended(hasComponent(new ComponentName("com.duyth10.dellhieukieugi", "com.duyth10.dellhieukieugi.MainActivity")));
//         intended(hasFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


    }

    @Test
    public void testQRCodeScanningResult() {
        Bundle args = new Bundle();
        args.putString("qrData", "Sample QR Code Data");
        args.putString("textFromMain", "100");

        FragmentScenario<TransactionResultFragment> fragmentScenario1 =
                FragmentScenario.launchInContainer(TransactionResultFragment.class, args);

        fragmentScenario1.onFragment(fragment -> {
            TextView qrDataTextView = fragment.getView().findViewById(R.id.qrDataTextView);
            TextView textFromMainMonney = fragment.getView().findViewById(R.id.textMonney);

            assertEquals("Sample QR Code Data", qrDataTextView.getText().toString());
            assertEquals("100", textFromMainMonney.getText().toString());
        });
    }
}
