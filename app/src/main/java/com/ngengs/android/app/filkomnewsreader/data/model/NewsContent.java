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

@SuppressWarnings("unused")
public class NewsContent implements Parcelable {
    public static final Creator<NewsContent> CREATOR = new Creator<NewsContent>() {
        @Override
        public NewsContent createFromParcel(Parcel in) {
            return new NewsContent(in);
        }

        @Override
        public NewsContent[] newArray(int size) {
            return new NewsContent[size];
        }
    };
    @SerializedName("type")
    private int type;
    @SerializedName("position")
    private int position;
    @SerializedName("content")
    private String content;

    private NewsContent(Parcel in) {
        type = in.readInt();
        position = in.readInt();
        content = in.readString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeInt(position);
        parcel.writeString(content);
    }
}
