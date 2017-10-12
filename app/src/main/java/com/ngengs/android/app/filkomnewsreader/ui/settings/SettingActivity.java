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

package com.ngengs.android.app.filkomnewsreader.ui.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ngengs.android.app.filkomnewsreader.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SettingFragment mFragment = new SettingFragment();
        SettingContract.Presenter mPresenter = new SettingPresenter(mFragment);
        mPresenter.start();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.frame_setting, mFragment)
                                   .commit();
        setTitle(getString(R.string.title_settings));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("screen", getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
