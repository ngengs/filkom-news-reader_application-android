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

package com.ngengs.android.app.filkomnewsreader.ui.newsdetail;

import android.support.annotation.NonNull;

import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.ui.NetworkBasePresenter;
import com.ngengs.android.app.filkomnewsreader.ui.NetworkBaseView;

import java.util.Date;

interface NewsDetailContract {
    interface Presenter extends NetworkBasePresenter {
        void loadDetailFromServer();

        News getNews();

        void setNews(News news);

        void openBrowser();

        void shareLink();
    }

    interface View extends NetworkBaseView<Presenter> {
        void addDetail(News news);

        void clearDetail();

        void showProgress(boolean show);

        void showShareButton(boolean show);

        void setNewsTitle(@NonNull String title);

        void setNewsImage(@NonNull String imageUrl);

        void setNewsDate(@NonNull Date date);

        void shareLink(@NonNull String title, @NonNull String url);

        void openBrowser(@NonNull String url);

        void openInAppBrowser(@NonNull String url);

        boolean isInAppBrowser();
    }
}
