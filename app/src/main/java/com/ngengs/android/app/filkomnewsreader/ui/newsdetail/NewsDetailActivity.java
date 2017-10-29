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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.enumeration.Preferences;
import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.ui.inappbrowser.InAppBrowserOpenHelper;
import com.ngengs.android.app.filkomnewsreader.utils.CommonUtils;
import com.ngengs.android.app.filkomnewsreader.utils.NetworkUtils;
import com.ngengs.android.app.filkomnewsreader.utils.glideapp.GlideApp;
import com.ngengs.android.app.filkomnewsreader.utils.logger.AppLogger;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import timber.log.Timber;

public class NewsDetailActivity extends AppCompatActivity implements NewsDetailContract.View {

    public static final String INTENT_ARGS_DATA = "DATA";

    private ImageView mImage;
    private TextView mTitle;
    private TextView mDate;
    private ProgressBar mProgress;
    private RecyclerView mRecyclerView;
    private NewsDetailAdapter mAdapter;
    private NewsDetailContract.Presenter mPresenter;
    private FloatingActionButton mFabShare;
    private FirebaseAnalytics mFirebaseAnalytics;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NewsDetailPresenter(this, new AppLogger());
        mPresenter.start();
        setContentView(R.layout.activity_news_detail);
        mImage = findViewById(R.id.image_news_detail);
        Toolbar mToolbar = findViewById(R.id.toolbar_news_detail);
        mTitle = findViewById(R.id.text_title_news_detail);
        mDate = findViewById(R.id.text_date_news_detail);
        mProgress = findViewById(R.id.progress_news_detail);
        mRecyclerView = findViewById(R.id.recycler_news_detail);
        mFabShare = findViewById(R.id.fab_share);
        mFabShare.setOnClickListener(view -> mPresenter.shareLink());
        showShareButton(false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mAdapter = new NewsDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        News data = null;
        if (getIntent() != null) {
            data = getIntent().getParcelableExtra(INTENT_ARGS_DATA);
        }
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable("DATA");
            Bundle bundle = new Bundle();
            bundle.putString("screen", getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        }

        setSupportActionBar(mToolbar);
        if (data != null) {
            mPresenter.setNews(data);
            mPresenter.loadDetailFromServer();
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, data.getId());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, data.getTitle());
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "detail");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        } else {
            Timber.e("onCreate: %s", "Empty data, cant start");
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("DATA", mPresenter.getNews());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPresenter.getNews() != null && !TextUtils.isEmpty(mPresenter.getNews().getLink())) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_news_detail_menu, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_open_link_news_detail:
                mPresenter.openBrowser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(@NonNull NewsDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void addDetail(News news) {
        Timber.d("addDetail() called with: news = [" + news.getContent().size() + "]");
        mAdapter.add(news.getContent());
        invalidateOptionsMenu();

    }

    @Override
    public void clearDetail() {
        mAdapter.clear();
    }

    @Override
    public void showProgress(boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showShareButton(boolean show) {
        mFabShare.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNewsTitle(@NonNull String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        mTitle.setText(title);
    }

    @Override
    public void setNewsImage(@NonNull String imageUrl) {
        GlideApp.with(this).load(imageUrl).thumbnail(0.05f).into(mImage);
    }

    @Override
    public void setNewsDate(@NonNull Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(this);
        String stringDate = dateFormat.format(date);
        mDate.setText(stringDate);
    }

    @Override
    public void shareLink(@NonNull String title, @NonNull String url) {
        Timber.d("shareLink() called with: title = [" + title + "], url = [" + url + "]");
        logClickEvent("share");
        CommonUtils.shareLink(this, mPresenter.getNews().getTitle(),
                              mPresenter.getNews().getLink());
    }

    @Override
    public void openBrowser(@NonNull String url) {
        Timber.d("openBrowser() called with: url = [" + url + "]");
        logClickEvent("browser");
        CommonUtils.openLinkInBrowser(this, mPresenter.getNews().getLink());
    }

    @Override
    public void openInAppBrowser(@NonNull String url) {
        Timber.d("openInAppBrowser() called with: url = [" + url + "]");
        logClickEvent("in_app_browser");
        InAppBrowserOpenHelper.open(this, url);
    }

    @Override
    public boolean isInAppBrowser() {
        return mSharedPreferences.getBoolean(Preferences.PREF_KEY_IN_APP_BROWSER, true);
    }

    @Override
    public File getCacheDirectory() {
        Timber.d("getCacheDirectory() called. Cache dir: %s",
                 getCacheDir().getAbsolutePath());
        return getCacheDir();
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkAvailable(this);
    }

    private void logClickEvent(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        mFirebaseAnalytics.logEvent("click", bundle);
    }
}
