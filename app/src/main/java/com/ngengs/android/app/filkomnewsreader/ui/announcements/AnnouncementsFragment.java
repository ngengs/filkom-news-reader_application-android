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


import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.enumeration.Preferences;
import com.ngengs.android.app.filkomnewsreader.data.enumeration.Types;
import com.ngengs.android.app.filkomnewsreader.data.model.Announcement;
import com.ngengs.android.app.filkomnewsreader.ui.inappbrowser.InAppBrowserOpenHelper;
import com.ngengs.android.app.filkomnewsreader.utils.CommonUtils;
import com.ngengs.android.app.filkomnewsreader.utils.NetworkUtils;
import com.ngengs.android.app.filkomnewsreader.utils.logger.AppLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementsFragment extends Fragment implements AnnouncementsContract.View {
    private AnnouncementsContract.Presenter mPresenter;

    private ImageView mImageIndicator;
    private TextView mTextIndicator;
    private ProgressBar mProgressIndicator;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private AnnouncementsAdapter mAdapter;
    private RecyclerView.OnScrollListener mScrollListener;
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    private SharedPreferences mSharedPreferences;


    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AnnouncementsFragment.
     */
    public static AnnouncementsFragment newInstance() {
        return new AnnouncementsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView() called with: inflater = [" + inflater + "], container = [" +
                 container + "], savedInstanceState = [" + savedInstanceState + "]");
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_announcements, container, false);

        mRecyclerView = mView.findViewById(R.id.recycler_announcement);
        mImageIndicator = mView.findViewById(R.id.image_frame_announcement_indicator);
        mTextIndicator = mView.findViewById(R.id.text_frame_announcement_indicator);
        mProgressIndicator = mView.findViewById(R.id.progress_frame_announcement_indicator);
        mSwipeRefresh = mView.findViewById(R.id.swipe_refresh_announcement);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mPresenter = new AnnouncementsPresenter(this, new AppLogger());
        mAdapter = new AnnouncementsAdapter(getContext());
        mAdapter.setClickListener(position -> mPresenter.onClick(position));
        mAdapter.setLongClickListener(position -> mPresenter.onLongClick(position));
        mPresenter.start();

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPresenter.scrollingHandle(mLayoutManager.getChildCount(),
                                           mLayoutManager.getItemCount(),
                                           mLayoutManager.findFirstVisibleItemPosition());
            }
        };
        mRefreshListener = () -> mPresenter.getAnnouncementFromServer(1);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        if (savedInstanceState != null) {
            int tempPageNow = savedInstanceState.getInt("PAGE_NOW", 1);
            int tempPageTotal = savedInstanceState.getInt("PAGE_TOTAL", 1);
            List temp = new ArrayList();
            //noinspection ConstantConditions,unchecked
            temp.addAll(savedInstanceState.getParcelableArrayList("DATA"));
            //noinspection unchecked
            mPresenter.setAnnouncement((List<Announcement>) temp, tempPageNow, tempPageTotal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationManager mNotificationManager
                        = (NotificationManager) getContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null) {
                    StatusBarNotification[] notifications
                            = mNotificationManager.getActiveNotifications();
                    if (notifications != null) {
                        for (StatusBarNotification item : notifications) {
                            if (item.getTag() != null
                                && item.getTag()
                                       .equalsIgnoreCase(Types.NOTIFICATION_CHANNEL_ANNOUNCEMENT)) {
                                mNotificationManager.cancel(item.getTag(), item.getId());
                            }
                        }
                    }
                }
            }
            mPresenter.getAnnouncementFromServer(1);
        }
        return mView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("DATA", new ArrayList<>(mPresenter.getAnnouncement()));
        outState.putInt("PAGE_NOW", mPresenter.getPageNow());
        outState.putInt("PAGE_TOTAL", mPresenter.getPageTotal());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mSwipeRefresh.setOnRefreshListener(null);
        mAdapter.setClickListener(null);
        mAdapter.setLongClickListener(null);
        mPresenter = null;
        mScrollListener = null;
        mRefreshListener = null;
    }

    @Override
    public void setPresenter(@NonNull AnnouncementsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void addAnnouncement(@NonNull List<Announcement> announcements) {
        Timber.d("addNews() called with: announcements = [" + announcements + "]");
        mAdapter.add(announcements);
    }

    @Override
    public void clearAnnouncement() {
        Timber.d("clearAnnouncement() called");
        mAdapter.clear();
    }

    @Override
    public void showIndicator(boolean show) {
        Timber.d("showIndicator() called with: show = [" + show + "]");
        mImageIndicator.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mTextIndicator.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setIndicator(int indicatorType) {
        Timber.d("setIndicator() called with: indicatorType = [" + indicatorType + "]");
        switch (indicatorType) {
            case Types.TYPE_INDICATOR_EMPTY:
                mImageIndicator.setImageDrawable(
                        VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_announcement,
                                                    null));
                mTextIndicator.setText(R.string.indicator_load_data);
                break;
            case Types.TYPE_INDICATOR_ERROR:
                mImageIndicator.setImageDrawable(
                        VectorDrawableCompat.create(getResources(), R.drawable.ic_alert, null));
                mTextIndicator.setText(R.string.indicator_error);
                break;
            case Types.TYPE_INDICATOR_NO_NETWORK:
                mImageIndicator.setImageDrawable(
                        VectorDrawableCompat.create(getResources(), R.drawable.ic_network_off,
                                                    null));
                mTextIndicator.setText(R.string.indicator_error_no_network);
                break;
            default:
                throw new RuntimeException(
                        "Indicator type " + indicatorType + " not handle correctly");
        }

    }

    @Override
    public void showProgress(boolean show) {
        Timber.d("showProgress() called with: show = [" + show + "]");
        mProgressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void openBrowser(@NonNull String url) {
        Timber.d("openBrowser() called with: url = [" + url + "]");
        logClickEvent("open_browser");
        CommonUtils.openLinkInBrowser(getContext(), url);
    }

    @Override
    public void openInAppBrowser(@NonNull String url) {
        Timber.d("openBrowser() called with: url = [" + url + "]");
        logClickEvent("open_in_app_browser");
        InAppBrowserOpenHelper.open(getContext(), url);
    }

    @Override
    public void shareLink(@NonNull String title, @NonNull String url) {
        Timber.d("shareLink() called with: title = [" + title + "], url = [" + url + "]");
        logClickEvent("share");
        CommonUtils.shareLink(getContext(), title, url);
    }

    @Override
    public boolean isSwipeRefreshLoading() {
        return mSwipeRefresh.isRefreshing();
    }

    @Override
    public void stopSwipeRefreshLoading() {
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean isInAppBrowser() {
        return mSharedPreferences.getBoolean(Preferences.PREF_KEY_IN_APP_BROWSER, true);
    }

    @Override
    public File getCacheDirectory() {
        Timber.d("getCacheDirectory() called. Cache dir: %s",
                 getActivity().getCacheDir().getAbsolutePath());
        return getActivity().getCacheDir();
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkAvailable(getActivity().getApplicationContext());
    }

    private void logClickEvent(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        mFirebaseAnalytics.logEvent("click", bundle);
    }
}
