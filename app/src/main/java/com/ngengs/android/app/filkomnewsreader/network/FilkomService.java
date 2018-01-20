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

package com.ngengs.android.app.filkomnewsreader.network;

import com.ngengs.android.app.filkomnewsreader.data.response.AnnouncementListResponse;
import com.ngengs.android.app.filkomnewsreader.data.response.NewsDetailResponse;
import com.ngengs.android.app.filkomnewsreader.data.response.NewsListResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@SuppressWarnings("unused")
public interface FilkomService {
    @GET("news/list")
    Observable<NewsListResponse> listNews(@Query("page") int page);

    @GET("news/id/{id}")
    Observable<NewsDetailResponse> detailNews(@Path("id") String id);

    @GET("news/search")
    Observable<NewsListResponse> searchNews(@Query("q") String searchText, @Query("page") int page);

    @GET("announcement/list")
    Observable<AnnouncementListResponse> listAnnouncement(@Query("page") int page);

    @GET("announcement/search")
    Observable<AnnouncementListResponse> searchAnnouncement(@Query("q") String searchText,
                                                            @Query("page") int page);
}
