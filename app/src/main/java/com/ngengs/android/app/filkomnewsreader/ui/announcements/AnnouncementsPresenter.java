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
import com.ngengs.android.app.filkomnewsreader.data.response.AnnouncementListResponseData;
import com.ngengs.android.app.filkomnewsreader.network.Connection;
import com.ngengs.android.app.filkomnewsreader.network.FilkomService;
import com.ngengs.android.app.filkomnewsreader.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementsPresenter implements AnnouncementsContract.Presenter {

    private final AnnouncementsContract.View mView;
    private List<Announcement> mData;
    private int mPageNow;
    private int mPageTotal;
    private Disposable mDisposable;
    private FilkomService mFilkomService;
    private Logger mLogger;

    public AnnouncementsPresenter(AnnouncementsContract.View mView, Logger logger) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
        this.mLogger = logger;
    }

    @Override
    public void start() {
        mLogger.d("start() called");
        setAnnouncement(new ArrayList<>(), 1, 1);
        mFilkomService = Connection.build(mView.getCacheDirectory());
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
                if (isInProgress()) {
                    unbind();
                }
                process = true;
            } else if (!isInProgress()) {
                if (page == 1) {
                    mView.setIndicator(Types.TYPE_INDICATOR_EMPTY);
                    mView.showIndicator(true);
                    mView.showProgress(true);
                }
                process = true;
            }
            if (process) {
                mDisposable = mFilkomService
                        .listAnnouncement(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                announcementListResponse -> onDataLoad(
                                        announcementListResponse.getData()),
                                this::onDataError, this::onDataComplete
                        );
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
            mView.openBrowser(mData.get(position).getLink());
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

    private void onDataLoad(AnnouncementListResponseData responseData) {
        mLogger.d("onDataLoad() called with: responseData = [" + responseData + "]");
        mView.showIndicator(false);
        if (responseData != null) {
            mLogger.d("onDataLoad: %s", "Response not Null");
            mPageTotal = responseData.getPageTotal();
            mPageNow = responseData.getPageNow();
            if (responseData.getAnnouncements() != null &&
                responseData.getAnnouncements().size() > 0) {
                mLogger.d("onDataLoad: %s", "announcement not null");
                if (mPageNow == 1) mView.clearAnnouncement();
                mData.addAll(responseData.getAnnouncements());
                mView.addAnnouncement(responseData.getAnnouncements());
            }
        }
    }

    private void onDataError(Throwable throwable) {
        mLogger.e(throwable, "onDataError: ");
        mView.setIndicator(Types.TYPE_INDICATOR_ERROR);
    }

    private void onDataComplete() {
        mLogger.d("onDataComplete() called");
        if (mPageNow == 1) mView.showProgress(false);
        if (mView.isSwipeRefreshLoading()) mView.stopSwipeRefreshLoading();
        unbind();
    }

    @Override
    public void unbind() {
        mLogger.d("unbind() called");
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
    }

    @Override
    public boolean isInProgress() {
        return mDisposable != null;
    }
}
