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

import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.network.Connection;
import com.ngengs.android.app.filkomnewsreader.network.FilkomService;
import com.ngengs.android.app.filkomnewsreader.utils.logger.Logger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailPresenter implements NewsDetailContract.Presenter {

    private final NewsDetailContract.View mView;
    private News mData;
    private Disposable mDisposable;
    private FilkomService mFilkomService;
    private Logger mLogger;

    public NewsDetailPresenter(NewsDetailContract.View mView, Logger logger) {
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
        mDisposable = null;
        mFilkomService = Connection.build(mView.getCacheDirectory());
    }

    @Override
    public void loadDetailFromServer() {
        mLogger.d("loadDetailFromServer() called");
        if (mData != null && mData.getContent() != null) {
            onDataLoad(mData);
            onDataComplete();
        } else {
            if (!mView.isNetworkConnected()) {
                mLogger.d("loadDetailFromServer: %s", "Error");
            } else if (mData != null && !isInProgress()) {
                mView.showProgress(true);

                mDisposable = mFilkomService.detailNews(mData.getId())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    newsDetailResponse -> onDataLoad(
                                                            newsDetailResponse.getData()),
                                                    this::onDataError, this::onDataComplete
                                            );
            } else {
                throw new RuntimeException("Data not loaded before request start");
            }
        }
    }

    @Override
    public News getNews() {
        mLogger.d("getNews() called");
        return mData;
    }

    @Override
    public void setNews(News news) {
        mLogger.d("setNews() called with: news = [" + news + "]");
        onDataLoad(news);
    }

    private void onDataLoad(News data) {
        mLogger.d("onDataLoad() called with: data = [" + data + "]");
        if (data != null) {
            News temp = mData;
            mData = data;
            if (temp == null && mData.getTitle() != null) {
                mView.setNewsTitle(mData.getTitle());
            }
            if (temp == null && mData.getImage() != null) {
                mView.setNewsImage(mData.getImage());
            }
            if (temp == null && mData.getDate() != null) {
                mView.setNewsDate(mData.getDate());
            }
            if (mData.getContent() != null) {
                mView.clearDetail();
                mView.addDetail(mData);
            }
        }
    }

    private void onDataComplete() {
        mLogger.d("onDataComplete() called");
        mView.showProgress(false);
        if (mData != null && mData.getLink() != null) {
            mView.showShareButton(true);
        }
        mDisposable = null;
    }

    private void onDataError(Throwable throwable) {
        mLogger.e(throwable, "onDataError: ");
        mDisposable = null;
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
