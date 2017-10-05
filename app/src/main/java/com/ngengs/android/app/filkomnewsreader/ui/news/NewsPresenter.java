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

import com.ngengs.android.app.filkomnewsreader.data.enumeration.Types;
import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.data.response.NewsListResponseData;
import com.ngengs.android.app.filkomnewsreader.network.Connection;
import com.ngengs.android.app.filkomnewsreader.network.FilkomService;
import com.ngengs.android.app.filkomnewsreader.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsPresenter implements NewsContract.Presenter {

    private final NewsContract.View mView;
    private List<News> mData;
    private int mPageNow;
    private int mPageTotal;
    private Disposable mDisposable;
    private FilkomService mFilkomService;
    private Logger mLogger;

    public NewsPresenter(NewsContract.View mView, Logger logger) {
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
        setNews(new ArrayList<>(), 1, 1);
        mFilkomService = Connection.build(mView.getCacheDirectory());
    }

    @Override
    public void getNewsFromServer(int page) {
        mLogger.d("getNewsFromServer() called with: page = [" + page + "], pageTotal = [" +
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
                        .listNews(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                newsListResponse -> onDataLoad(
                                        newsListResponse.getData()),
                                this::onDataError,
                                this::onDataComplete
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
    public List<News> getNews() {
        mLogger.d("getNews() called");
        return mData;
    }

    @Override
    public void setNews(List<News> news, int pageNow, int pageTotal) {
        mLogger.d("setNews() called with: news = [" + news + "], pageNow = [" + pageNow +
                  "], pageTotal = [" + pageTotal + "]");
        if (mData == null) {
            mData = new ArrayList<>(news);
        } else {
            mData.clear();
            mData.addAll(news);
        }
        mView.showIndicator(false);
        mView.showProgress(false);
        mView.addNews(mData);
        mPageNow = pageNow;
        mPageTotal = pageTotal;
    }

    @Override
    public void onClick(int position) {
        mLogger.d("onClick() called with: position = [" + position + "]");
        if (position < mData.size()) {
            mView.openNewsDetail(mData.get(position));
        }
    }

    @Override
    public void scrollingHandle(int visibleItemCount, int totalItemCount,
                                int firstVisibleItemPosition) {
        boolean inBottom = (firstVisibleItemPosition + visibleItemCount + 5) >=
                           totalItemCount;
        if (inBottom && (mPageNow < mPageTotal)) {
            this.getNewsFromServer(mPageNow + 1);
        }
    }

    private void onDataLoad(NewsListResponseData responseData) {
        mLogger.d("onDataLoad() called with: responseData = [" + responseData + "]");
        mView.showIndicator(false);
        if (responseData != null) {
            mPageTotal = responseData.getPageTotal();
            mPageNow = responseData.getPageNow();
            if (responseData.getNews() != null && responseData.getNews().size() > 0) {
                if (mPageNow == 1) mView.clearNews();
                mData.addAll(responseData.getNews());
                mView.addNews(responseData.getNews());
            }
        }
    }

    private void onDataError(Throwable throwable) {
        mLogger.e(throwable, "onDataError: ");
        mView.setIndicator(Types.TYPE_INDICATOR_ERROR);
    }

    private void onDataComplete() {
        mLogger.d("onDataComplete: %s", "");
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
