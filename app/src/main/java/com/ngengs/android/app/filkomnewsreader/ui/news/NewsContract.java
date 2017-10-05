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

package com.ngengs.android.app.filkomnewsreader.ui.news;

import android.support.annotation.NonNull;

import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.ui.NetworkBasePresenter;
import com.ngengs.android.app.filkomnewsreader.ui.NetworkBaseView;

import java.util.List;

interface NewsContract {
    interface Presenter extends NetworkBasePresenter {
        void getNewsFromServer(int page);

        int getPageNow();

        int getPageTotal();

        List<News> getNews();

        void setNews(List<News> news, int pageNow, int pageTotal);

        void onClick(int position);

        void scrollingHandle(int visibleItemCount, int totalItemCount,
                             int firstVisibleItemPosition);
    }

    interface View extends NetworkBaseView<Presenter> {
        void addNews(@NonNull List<News> news);

        void clearNews();

        void showIndicator(boolean show);

        void setIndicator(int indicatorType);

        void showProgress(boolean show);

        void openNewsDetail(@NonNull News news);

        boolean isSwipeRefreshLoading();

        void stopSwipeRefreshLoading();
    }
}
