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
import com.ngengs.android.app.filkomnewsreader.data.sources.NewsSource;
import com.ngengs.android.app.filkomnewsreader.utils.logger.Logger;

public class NewsDetailPresenter
        implements NewsDetailContract.Presenter, NewsSource.LoadSingleDataCallback<News> {

    private final NewsDetailContract.View mView;
    private NewsSource mSource;
    private News mData;
    private Logger mLogger;

    NewsDetailPresenter(NewsDetailContract.View mView, Logger logger) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
        this.mLogger = logger;
        mSource = NewsSource.getInstance(mView.getCacheDirectory());
    }

    @Override
    public void start() {
        mLogger.d("start() called");
    }

    @Override
    public void loadDetailFromServer() {
        mLogger.d("loadDetailFromServer() called");
        if (mData != null && mData.getContent() != null) {
            onDataLoaded(mData);
            onDataFinished();
        } else {
            if (!mView.isNetworkConnected()) {
                mLogger.d("loadDetailFromServer: %s", "Error");
            } else if (mData != null && !mSource.isInProcess()) {
                mView.showProgress(true);

                mSource.getData(mData.getId(), this);
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
        onDataLoaded(news);
    }

    @Override
    public void openBrowser() {
        mLogger.d("openBrowser() called");
        if (mData.getLink() != null && !mData.getLink().equalsIgnoreCase("")) {
            String url = mData.getLink();
            if (mView.isInAppBrowser()) {
                mView.openInAppBrowser(url);
            } else {
                mView.openBrowser(url);
            }
        }
    }

    @Override
    public void shareLink() {
        mLogger.d("shareLink() called");
        if (mData.getLink() != null && mData.getTitle() != null &&
            !mData.getTitle().equalsIgnoreCase("") &&
            !mData.getLink().equalsIgnoreCase("")) {
            mView.shareLink(mData.getTitle(), mData.getLink());
        }
    }

    @Override
    public void unbind() {
        mLogger.d("unbind() called");
        mSource.release();
    }

    @Override
    public void onDataLoaded(News data) {
        mLogger.d("onDataLoaded() called with: data = [" + data + "]");
        if (data != null) {
            News temp = mData;
            if (temp == null) {
                temp = new News(data.getId());
            }
            mData = data;
            if (temp.getTitle() == null && mData.getTitle() != null) {
                mView.setNewsTitle(mData.getTitle());
            }
            if (temp.getImage() == null && mData.getImage() != null) {
                mView.setNewsImage(mData.getImage());
            }
            if (temp.getDate() == null && mData.getDate() != null) {
                mView.setNewsDate(mData.getDate());
            }
            if (mData.getContent() != null) {
                mView.clearDetail();
                mView.addDetail(mData);
            }
        }
    }

    @Override
    public void onDataFailed(Throwable throwable) {
        mLogger.e(throwable, "onDataFailed: ");
    }

    @Override
    public void onDataFinished() {
        mLogger.d("onDataFinished() called");
        mView.showProgress(false);
        if (mData != null && mData.getLink() != null) {
            mView.showShareButton(true);
        }
        unbind();
    }
}
