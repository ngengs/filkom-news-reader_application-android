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

import com.ngengs.android.app.filkomnewsreader.ui.BasePresenter;
import com.ngengs.android.app.filkomnewsreader.ui.BaseView;

class SplashScreenContract {
    interface Presenter extends BasePresenter {
        void runFirstStart(boolean firstStart);
    }

    @SuppressWarnings("SameParameterValue")
    interface View extends BaseView<Presenter> {
        void startMainActivity();

        void changePreferences(String key, boolean value);

        void subscribeTopic(String key);
    }
}
