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

import com.ngengs.android.app.filkomnewsreader.data.enumeration.Preferences;

import timber.log.Timber;

public class SettingPresenter implements SettingContract.Presenter {

    private final SettingContract.View mView;

    SettingPresenter(SettingContract.View mView) {
        if (mView != null) {
            this.mView = mView;
        } else {
            throw new RuntimeException("Cant bind view");
        }
    }

    @Override
    public void start() {
        mView.setPresenter(this);
    }

    @Override
    public void updateNotificationPreferences(String key, boolean status) {
        Timber.d("updateNotificationPreferences() called with: key = [" + key + "], status = [" +
                 status + "]");
        if (key.equalsIgnoreCase(Preferences.PREF_KEY_NOTIFICATION_NEWS) ||
            key.equalsIgnoreCase(Preferences.PREF_KEY_NOTIFICATION_ANNOUNCEMENT)) {
            if (status) {
                mView.subscribeTopic(key);
            } else {
                mView.unsubscribeTopic(key);
            }
        }
    }
}
