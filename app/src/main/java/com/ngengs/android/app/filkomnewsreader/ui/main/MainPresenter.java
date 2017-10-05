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

import com.ngengs.android.app.filkomnewsreader.data.enumeration.Types;

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mView;
    private int mFragmentType;


    public MainPresenter(MainContract.View mView) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
        this.mFragmentType = Types.TYPE_NEWS;
    }

    @Override
    public void start() {
        changeFragment(mFragmentType);
    }

    private void changeFragment(int fragmentType) {
        setFragmentType(fragmentType);
        switch (fragmentType) {
            case Types.TYPE_NEWS:
                mView.changeFragmentNews();
                break;
            case Types.TYPE_ANNOUNCEMNT:
                mView.changeFragmentAnnouncement();
                break;
            default:
                throw new RuntimeException("Fragment type " + fragmentType + " not supported");
        }
    }

    @Override
    public boolean changeFragmentNews() {
        if (mFragmentType != Types.TYPE_NEWS) {
            changeFragment(Types.TYPE_NEWS);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean changeFragmentAnnouncement() {
        if (mFragmentType != Types.TYPE_ANNOUNCEMNT) {
            changeFragment(Types.TYPE_ANNOUNCEMNT);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getFragmentType() {
        return mFragmentType;
    }

    @Override
    public void setFragmentType(int fragmentType) {
        mFragmentType = fragmentType;
    }

    @Override
    public void handleBack() {
        if (mView.isDrawerOpen()) {
            mView.closeDrawer();
        } else {
            if (mFragmentType != Types.TYPE_NEWS) {
                changeFragment(Types.TYPE_NEWS);
            } else {
                mView.superBack();
            }
        }
    }
}
