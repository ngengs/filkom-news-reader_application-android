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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ngengs.android.app.filkomnewsreader.data.enumeration.Types;
import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.ui.newsdetail.NewsDetailActivity;
import com.ngengs.android.app.filkomnewsreader.utils.NetworkUtils;
import com.ngengs.android.app.filkomnewsreader.utils.logger.AppLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements NewsContract.View {

    private NewsContract.Presenter mPresenter;

    private ImageView mImageIndicator;
    private TextView mTextIndicator;
    private ProgressBar mProgressIndicator;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private NewsAdapter mAdapter;
    private RecyclerView.OnScrollListener mScrollListener;
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */
    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView() called with: inflater = [" + inflater + "], container = [" +
                 container + "], savedInstanceState = [" + savedInstanceState + "]");
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_news, container, false);

        mRecyclerView = mView.findViewById(R.id.recycler_news);
        mImageIndicator = mView.findViewById(R.id.image_frame_news_indicator);
        mTextIndicator = mView.findViewById(R.id.text_frame_news_indicator);
        mProgressIndicator = mView.findViewById(R.id.progress_frame_news_indicator);
        mSwipeRefresh = mView.findViewById(R.id.swipe_refresh_news);

        mPresenter = new NewsPresenter(this, new AppLogger());
        mAdapter = new NewsAdapter(getContext());
        mAdapter.setClickListener(position -> mPresenter.onClick(position));
        mPresenter.start();

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPresenter.scrollingHandle(mLayoutManager.getChildCount(),
                                           mLayoutManager.getItemCount(),
                                           mLayoutManager.findFirstVisibleItemPosition());
            }
        };
        mRefreshListener = () -> mPresenter.getNewsFromServer(1);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        if (savedInstanceState != null) {
            int tempPageNow = savedInstanceState.getInt("PAGE_NOW", 1);
            int tempPageTotal = savedInstanceState.getInt("PAGE_TOTAL", 1);
            List temp = new ArrayList();
            //noinspection ConstantConditions,unchecked
            temp.addAll(savedInstanceState.getParcelableArrayList("DATA"));
            //noinspection unchecked
            mPresenter.setNews((List<News>) temp, tempPageNow, tempPageTotal);
        } else {
            mPresenter.getNewsFromServer(1);
        }
        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("DATA", new ArrayList<>(mPresenter.getNews()));
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
        mPresenter = null;
        mScrollListener = null;
        mRefreshListener = null;
    }

    @Override
    public void setPresenter(@NonNull NewsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void addNews(@NonNull List<News> news) {
        Timber.d("addNews() called with: news = [" + news + "]");
        mAdapter.add(news);
    }

    @Override
    public void clearNews() {
        Timber.d("clearNews() called");
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
                        VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_news, null));
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
    public void openNewsDetail(@NonNull News news) {
        Timber.d("openNewsDetail() called with: news = [" + news + "]");
        logClickEvent("detail");
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("DATA", news);
        startActivity(intent);
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
    public File getCacheDirectory() {
        Timber.d("getCacheDirectory() called. Cache dir: %s",
                 getActivity().getCacheDir().getAbsolutePath());
        return getActivity().getCacheDir();
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkAvailable(getActivity().getApplicationContext());
    }

    @SuppressWarnings("SameParameterValue")
    private void logClickEvent(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        mFirebaseAnalytics.logEvent("click", bundle);
    }
}
