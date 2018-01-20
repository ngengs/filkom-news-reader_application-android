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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ngengs.android.app.filkomnewsreader.R;

import timber.log.Timber;

public class SettingFragment extends PreferenceFragmentCompat implements SettingContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;
    private SettingContract.Presenter mPresenter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.settings_preferences);
    }

    @Override
    public void onDestroyView() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey(), false);
                }
            } else {
                updatePreference(preference, preference.getKey(), false);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updatePreference(findPreference(s), s, true);
    }


    private void updatePreference(Preference preference, String key, boolean onChanged) {
        Timber.d(
                "updatePreference() called with: preference = [" + preference + "], key = [" + key +
                "]");
        if (preference == null) {
            return;
        }
        if (preference instanceof SwitchPreferenceCompat) {
            if (mPresenter != null && onChanged) {
                boolean status = mSharedPreferences.getBoolean(key, true);
                mPresenter.updateNotificationPreferences(key, status);
            }
        }
    }

    @Override
    public void setPresenter(@NonNull SettingContract.Presenter presenter) {
        Timber.d("setPresenter() called with: presenter = [" + presenter + "]");
        mPresenter = presenter;
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
