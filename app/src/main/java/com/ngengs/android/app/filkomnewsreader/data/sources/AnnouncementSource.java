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

package com.ngengs.android.app.filkomnewsreader.data.sources;

import android.support.annotation.NonNull;

import com.ngengs.android.app.filkomnewsreader.data.model.Announcement;
import com.ngengs.android.app.filkomnewsreader.data.response.AnnouncementListResponseData;
import com.ngengs.android.app.filkomnewsreader.network.Connection;
import com.ngengs.android.app.filkomnewsreader.network.FilkomService;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementSource implements BaseSource<Announcement> {
    private final FilkomService mFilkomService;
    private Disposable mDisposable;

    private AnnouncementSource(FilkomService mFilkomService) {
        this.mFilkomService = mFilkomService;
        this.mDisposable = null;
    }

    public static AnnouncementSource getInstance(@NonNull File cacheLocation) {
        return new AnnouncementSource(Connection.build(cacheLocation));
    }

    @Override
    public void getData(@NonNull String id,
                        @NonNull LoadSingleDataCallback<Announcement> callback) {
        // Not Implemented
    }

    @Override
    public void getDataPaged(int page, @NonNull LoadListDataCallback<Announcement> callback) {
        mDisposable = mFilkomService
                .listAnnouncement(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        announcementListResponse -> {
                            if (announcementListResponse.getCode() == 200) {
                                AnnouncementListResponseData data
                                        = announcementListResponse.getData();
                                callback.onDataLoaded(data.getPageNow(), data.getPageTotal(),
                                                      data.getAnnouncements());
                            } else {
                                callback.onDataFailed(new Throwable("Something wrong"));
                            }
                        }, callback::onDataFailed, callback::onDataFinished);
    }

    @Override
    public boolean isInProcess() {
        return mDisposable != null;
    }

    @Override
    public void release() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
    }
}
