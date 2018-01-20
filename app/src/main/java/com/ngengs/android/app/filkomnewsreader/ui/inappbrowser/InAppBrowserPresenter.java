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

package com.ngengs.android.app.filkomnewsreader.ui.inappbrowser;

public class InAppBrowserPresenter implements InAppBrowserContract.Presenter {

    private final InAppBrowserContract.View mView;
    private String mLink;
    private String mTitle;

    InAppBrowserPresenter(InAppBrowserContract.View mView, String mLink) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
        if (mLink != null && !mLink.equalsIgnoreCase("")) {
            this.mLink = mLink;
        } else {
            throw new RuntimeException("Link url is null");
        }
    }

    @Override
    public void start() {
        this.mTitle = null;
        mView.loadBrowser(mLink);
        mView.setAppUrl(mLink);
    }

    @Override
    public void selectMenuOpenBrowser() {
        if (mLink != null && !mLink.equalsIgnoreCase("")) {
            mView.openOtherBrowser(mLink);
        } else {
            throw new RuntimeException("Open empty link in browser");
        }
    }

    @Override
    public void selectMenuShare() {
        if (mLink != null && !mLink.equalsIgnoreCase("")) {
            mView.shareLink(mLink, mTitle);
        } else {
            throw new RuntimeException("Share empty link");
        }
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
        mView.setAppTitle(mTitle);
    }

    @Override
    public boolean handleUri(String url) {
        boolean handle = false;

        if (url.contains("filkom.ub.ac.id")) {
            handle = true;
            mView.setAppUrl(url);
            mView.loadBrowser(url);
        }

        return handle;
    }
}
