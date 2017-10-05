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

package com.ngengs.android.app.filkomnewsreader.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.ngengs.android.app.filkomnewsreader.data.model.Announcement;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class AnnouncementListResponseData implements Parcelable {
    public static final Creator<AnnouncementListResponseData> CREATOR
            = new Creator<AnnouncementListResponseData>() {
        @Override
        public AnnouncementListResponseData createFromParcel(Parcel in) {
            return new AnnouncementListResponseData(in);
        }

        @Override
        public AnnouncementListResponseData[] newArray(int size) {
            return new AnnouncementListResponseData[size];
        }
    };
    @SerializedName("page_now")
    private int pageNow;
    @SerializedName("total_page")
    private int pageTotal;
    @SerializedName("announcement")
    private List<Announcement> announcements;

    protected AnnouncementListResponseData(Parcel in) {
        pageNow = in.readInt();
        pageTotal = in.readInt();
        announcements = in.createTypedArrayList(Announcement.CREATOR);
    }

    public int getPageNow() {
        return pageNow;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(
            List<Announcement> announcements) {
        this.announcements = announcements;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(pageNow);
        parcel.writeInt(pageTotal);
        parcel.writeTypedList(announcements);
    }
}
