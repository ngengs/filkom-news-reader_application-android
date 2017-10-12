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

import com.ngengs.android.app.filkomnewsreader.data.enumeration.Types;
import com.ngengs.android.app.filkomnewsreader.data.model.Announcement;
import com.ngengs.android.app.filkomnewsreader.data.sources.AnnouncementSource;
import com.ngengs.android.app.filkomnewsreader.data.sources.BaseSource;
import com.ngengs.android.app.filkomnewsreader.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsPresenter
        implements AnnouncementsContract.Presenter, BaseSource.LoadListDataCallback<Announcement> {

    private final AnnouncementsContract.View mView;
    private AnnouncementSource mSource;
    private List<Announcement> mData;
    private int mPageNow;
    private int mPageTotal;
    private Logger mLogger;

    AnnouncementsPresenter(AnnouncementsContract.View mView, Logger logger) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
        this.mLogger = logger;
        mSource = AnnouncementSource.getInstance(mView.getCacheDirectory());
    }

    @Override
    public void start() {
        mLogger.d("start() called");
        setAnnouncement(new ArrayList<>(), 1, 1);
    }

    @Override
    public void getAnnouncementFromServer(int page) {
        mLogger.d("getAnnouncementFromServer() called with: page = [" + page + "], pageTotal = [" +
                  mPageTotal + "]");
        if (!mView.isNetworkConnected()) {
            if (page == 1) {
                mView.setIndicator(Types.TYPE_INDICATOR_NO_NETWORK);
                mView.showIndicator(true);
                mView.showProgress(false);
            }
        } else if (page <= mPageTotal) {
            boolean process = false;
            if (mView.isSwipeRefreshLoading() && page == 1) {
                if (mSource.isInProcess()) {
                    unbind();
                }
                process = true;
            } else if (!mSource.isInProcess()) {
                if (page == 1) {
                    mView.setIndicator(Types.TYPE_INDICATOR_EMPTY);
                    mView.showIndicator(true);
                    mView.showProgress(true);
                }
                process = true;
            }
            if (process) {
                mSource.getDataPaged(page, this);
            }
        }
    }

    @Override
    public int getPageNow() {
        return mPageNow;
    }

    @Override
    public int getPageTotal() {
        return mPageTotal;
    }

    @Override
    public List<Announcement> getAnnouncement() {
        mLogger.d("getAnnouncement() called");
        return mData;
    }

    @Override
    public void setAnnouncement(List<Announcement> announcement, int pageNow,
                                int pageTotal) {
        mLogger.d("setAnnouncement() called with: announcement = [" + announcement +
                  "], pageNow = [" + pageNow + "], pageTotal = [" + pageTotal + "]");
        if (mData == null) {
            mData = new ArrayList<>(announcement);
        } else {
            mData.clear();
            mData.addAll(announcement);
        }
        mView.showIndicator(false);
        mView.showProgress(false);
        mView.addAnnouncement(mData);
        mPageNow = pageNow;
        mPageTotal = pageTotal;
    }

    @Override
    public boolean onClick(int position) {
        mLogger.d("onClick() called with: position = [" + position + "]");
        if (position < mData.size()) {
            String url = mData.get(position).getLink();
            if(mView.isInAppBrowser()) mView.openInAppBrowser(url);
            else mView.openBrowser(url);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onLongClick(int position) {
        mLogger.d("onLongClick() called with: position = [" + position + "]");
        if (position < mData.size()) {
            mView.shareLink(mData.get(position).getTitle(), mData.get(position).getLink());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void scrollingHandle(int visibleItemCount, int totalItemCount,
                                int firstVisibleItemPosition) {
        boolean inBottom = (firstVisibleItemPosition + visibleItemCount + 5) >=
                           totalItemCount;
        if (inBottom && (mPageNow < mPageTotal)) {
            this.getAnnouncementFromServer(mPageNow + 1);
        }
    }

    @Override
    public void unbind() {
        mLogger.d("unbind() called");
        mSource.release();
    }

    @Override
    public void onDataLoaded(int pageNow, int pageTotal, List<Announcement> data) {
        mLogger.d("onDataLoaded() called with: pageNow = [" + pageNow + "], pageTotal = [" +
                  pageTotal + "], data = [" + data + "]");
        mView.showIndicator(false);
        if (data != null) {
            mLogger.d("onDataLoad: %s", "Response not Null");
            mPageTotal = pageTotal;
            mPageNow = pageNow;
            if (data.size() > 0) {
                mLogger.d("onDataLoad: %s", "announcement not null");
                if (mPageNow == 1) mView.clearAnnouncement();
                mData.addAll(data);
                mView.addAnnouncement(data);
            }
        }
    }

    @Override
    public void onDataFailed(Throwable throwable) {
        mLogger.e(throwable, "onDataFailed: ");
        mView.setIndicator(Types.TYPE_INDICATOR_ERROR);
    }

    @Override
    public void onDataFinished() {
        mLogger.d("onDataFinished() called");
        if (mPageNow == 1) mView.showProgress(false);
        if (mView.isSwipeRefreshLoading()) mView.stopSwipeRefreshLoading();
        unbind();
    }
}
