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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.utils.CommonUtils;

import timber.log.Timber;

public class InAppBrowserActivity extends AppCompatActivity implements InAppBrowserContract.View {

    public static final String TAG_URL = "URL";
    private InAppBrowserContract.Presenter mPresenter;
    private TextView mTitle;
    private TextView mSubtitle;
    private WebView mWebview;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_browser);
        mTitle = findViewById(R.id.in_app_browser_title);
        mSubtitle = findViewById(R.id.in_app_browser_subtitle);
        mWebview = findViewById(R.id.in_app_browser_webview);
        Toolbar mToolbar = findViewById(R.id.in_app_browser_toolbar);
        String linkUrl = getIntent().getStringExtra(TAG_URL);
        Timber.d("onCreate: %s", linkUrl);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mPresenter = new InAppBrowserPresenter(this, linkUrl);
        mPresenter.setTitle("");
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mPresenter.setTitle(title);
            }

        });
        mPresenter.start();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("screen", getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_in_app_browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.menu_open_link_in_app_browser:
                mPresenter.selectMenuOpenBrowser();
                break;
            case R.id.menu_share_in_app_browser:
                mPresenter.selectMenuShare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(@NonNull InAppBrowserContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void loadBrowser(@NonNull String url) {
        Timber.d("loadBrowser() called with: url = [" + url + "]");
        mWebview.loadUrl(url);
        mWebview.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void setAppTitle(@NonNull String title) {
        Timber.d("setAppTitle() called with: title = [" + title + "]");
        setTitle(String.valueOf(title));
        mTitle.setText(String.valueOf(title));
    }

    @Override
    public void setAppUrl(@NonNull String url) {
        Timber.d("setAppUrl() called with: url = [" + url + "]");
        mSubtitle.setText(String.valueOf(url));
    }

    @Override
    public void openOtherBrowser(@NonNull String url) {
        Timber.d("openOtherBrowser() called with: url = [" + url + "]");
        logClickEvent("browser");
        CommonUtils.openLinkInBrowser(this, url);
    }

    @Override
    public void shareLink(@NonNull String url, @NonNull String title) {
        Timber.d("shareLink() called with: url = [" + url + "], title = [" + title + "]");
        logClickEvent("share");
        CommonUtils.shareLink(this, title, url);
    }

    private void logClickEvent(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        mFirebaseAnalytics.logEvent("click", bundle);
    }
}
