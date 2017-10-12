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

package com.ngengs.android.app.filkomnewsreader.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class News implements Parcelable {
    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("short_desc")
    private String shortDescription;
    @SerializedName("link")
    private String link;
    @SerializedName("date")
    private Date date;
    @SerializedName("content")
    private List<NewsContent> content;

    public News(String id){
        this.id = id;
    }

    private News(Parcel in) {
        id = in.readString();
        title = in.readString();
        image = in.readString();
        shortDescription = in.readString();
        link = in.readString();
        content = in.createTypedArrayList(NewsContent.CREATOR);
        long tmpDate = in.readLong();
        date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<NewsContent> getContent() {
        return content;
    }

    public void setContent(List<NewsContent> content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(image);
        parcel.writeString(shortDescription);
        parcel.writeString(link);
        parcel.writeTypedList(content);
        parcel.writeLong(date != null ? date.getTime() : -1);
    }
}
