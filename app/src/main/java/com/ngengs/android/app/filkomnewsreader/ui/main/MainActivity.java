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

package com.ngengs.android.app.filkomnewsreader.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.ui.announcements.AnnouncementsFragment;
import com.ngengs.android.app.filkomnewsreader.ui.news.NewsFragment;
import com.ngengs.android.app.filkomnewsreader.ui.settings.SettingActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {
    public static final String INTENT_ARGS_TYPE = "TYPE";
    private DrawerLayout mDrawer;
    private MainContract.Presenter mPresenter;
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (mFragmentManager == null) mFragmentManager = getSupportFragmentManager();

        mPresenter = new MainPresenter(this);
        int fragmentType = mPresenter.getFragmentType();
        if (getIntent() != null) {
            fragmentType = getIntent().getIntExtra(INTENT_ARGS_TYPE, fragmentType);
        }
        if (savedInstanceState != null) {
            mFragment = mFragmentManager.findFragmentById(R.id.frame_main);
            fragmentType = savedInstanceState.getInt("TYPE", fragmentType);
            mPresenter.setFragmentType(fragmentType);
            setTitle(savedInstanceState.getString("TITLE"));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("screen", getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
            mPresenter.setFragmentType(fragmentType);
            mPresenter.start();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("TYPE", mPresenter.getFragmentType());
        outState.putString("TITLE", String.valueOf(getTitle()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed() called");
        mPresenter.handleBack();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Timber.d("onNavigationItemSelected() called with: item = [" + id + "]");
        boolean changed;
        switch (id) {
            case R.id.nav_news:
                changed = mPresenter.changeFragmentNews();
                break;
            case R.id.nav_announcement:
                changed = mPresenter.changeFragmentAnnouncement();
                break;
            case R.id.nav_setting:
                changed = true;
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                throw new RuntimeException(
                        "Navigation menu with id: " + id + " not handle correctly");
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return changed;
    }

    @Override
    public void setPresenter(@NonNull MainContract.Presenter presenter) {
        Timber.d("setPresenter() called with: presenter = [" + presenter + "]");
        mPresenter = presenter;
    }

    private void changeFragment(Fragment fragment) {
        int fragmentType = mPresenter.getFragmentType();
        Timber.d("changeFragment() called with: fragmentType = [" + fragmentType + "]");
        if (mFragment == null) {
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.frame_main, fragment)
                                       .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.frame_main, fragment)
                                       .commit();
        }
        mFragment = fragment;
    }

    @Override
    public void changeFragmentNews() {
        setTitle(R.string.title_news);
        mNavigationView.getMenu().findItem(R.id.nav_news).setChecked(true);
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 10);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "News");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        changeFragment(NewsFragment.newInstance());
    }

    @Override
    public void changeFragmentAnnouncement() {
        setTitle(R.string.title_announcement);
        mNavigationView.getMenu().findItem(R.id.nav_announcement).setChecked(true);
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 11);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Announcement");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        changeFragment(AnnouncementsFragment.newInstance());
    }

    @Override
    public void superBack() {
        super.onBackPressed();
    }

    @Override
    public boolean isDrawerOpen() {
        return mDrawer.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void closeDrawer() {
        mDrawer.closeDrawer(GravityCompat.START);
    }
}
