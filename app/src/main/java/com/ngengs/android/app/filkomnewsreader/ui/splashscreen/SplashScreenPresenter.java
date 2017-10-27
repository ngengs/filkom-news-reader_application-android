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

import com.ngengs.android.app.filkomnewsreader.data.enumeration.Preferences;

public class SplashScreenPresenter implements SplashScreenContract.Presenter {

    private final SplashScreenContract.View mView;

    SplashScreenPresenter(SplashScreenContract.View mView) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
    }

    @Override
    public void start() {
        mView.startMainActivity();
    }

    @Override
    public void runFirstStart(boolean firstStart) {
        if (firstStart) {
            mView.subscribeTopic(Preferences.PREF_KEY_NOTIFICATION_NEWS);
            mView.subscribeTopic(Preferences.PREF_KEY_NOTIFICATION_ANNOUNCEMENT);
            mView.changePreferences(Preferences.PREF_KEY_NOTIFICATION_NEWS, true);
            mView.changePreferences(Preferences.PREF_KEY_NOTIFICATION_ANNOUNCEMENT, true);
            mView.changePreferences(Preferences.PREF_KEY_FIRST_RUN, false);
        }
    }

    @Override
    public void runInDebug(boolean debug) {
        if (debug) {
            mView.subscribeTopic(Preferences.PREF_KEY_NOTIFICATION_DEBUG);
        } else {
            mView.unsubscribeTopic(Preferences.PREF_KEY_NOTIFICATION_DEBUG);
        }
    }
}
