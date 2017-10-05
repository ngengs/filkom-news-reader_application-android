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

package com.ngengs.android.app.filkomnewsreader.ui.announcements;

import android.support.annotation.NonNull;

import com.ngengs.android.app.filkomnewsreader.data.model.Announcement;
import com.ngengs.android.app.filkomnewsreader.ui.NetworkBasePresenter;
import com.ngengs.android.app.filkomnewsreader.ui.NetworkBaseView;

import java.util.List;

interface AnnouncementsContract {
    interface Presenter extends NetworkBasePresenter {
        void getAnnouncementFromServer(int page);

        int getPageNow();

        int getPageTotal();

        List<Announcement> getAnnouncement();

        void setAnnouncement(List<Announcement> announcement, int pageNow, int pageTotal);

        boolean onClick(int position);

        boolean onLongClick(int position);

        void scrollingHandle(int visibleItemCount, int totalItemCount,
                             int firstVisibleItemPosition);
    }

    interface View extends NetworkBaseView<Presenter> {
        void addAnnouncement(@NonNull List<Announcement> announcements);

        void clearAnnouncement();

        void showIndicator(boolean show);

        void setIndicator(int indicatorType);

        void showProgress(boolean show);

        void openBrowser(String url);

        void shareLink(String title, String url);

        boolean isSwipeRefreshLoading();

        void stopSwipeRefreshLoading();
    }
}
