/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ngengs.android.app.filkomnewsreader.ui.splashscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ngengs.android.app.filkomnewsreader.BuildConfig;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.enumeration.Preferences;
import com.ngengs.android.app.filkomnewsreader.ui.main.MainActivity;

import timber.log.Timber;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenContract.View {

    private SplashScreenContract.Presenter mPresenter;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mPresenter = new SplashScreenPresenter(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = mSharedPreferences.getBoolean(Preferences.PREF_KEY_FIRST_RUN, true);
        mPresenter.start();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("first_run", firstRun);
            bundle.putString("screen", getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        }
        mPresenter.runFirstStart(firstRun);
        mPresenter.runInDebug(BuildConfig.DEBUG);
    }

    @Override
    public void setPresenter(@NonNull SplashScreenContract.Presenter presenter) {
        Timber.d("setPresenter() called with: presenter = [" + presenter + "]");
        mPresenter = presenter;
    }

    @Override
    public void startMainActivity() {
        Timber.d("startMainActivity() called");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void changePreferences(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public void subscribeTopic(String key) {
        Timber.d("subscribeTopic() called with: key = [" + key + "]");
        FirebaseMessaging.getInstance().subscribeToTopic(key);
    }

    @Override
    public void unsubscribeTopic(String key) {
        Timber.d("unsubscribeTopic() called with: key = [" + key + "]");
        FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
    }
}
