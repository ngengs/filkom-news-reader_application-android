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

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.model.Announcement;
import com.ngengs.android.app.filkomnewsreader.utils.listener.OnClickListener;
import com.ngengs.android.app.filkomnewsreader.utils.listener.OnLongClickListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.ViewHolder> {

    private static final int TYPE_DATA = 0;
    private static final int TYPE_PROGRESS = 1;
    private final Context mContext;
    private final List<Announcement> mData;
    private OnClickListener mClickListener;
    private OnLongClickListener mLongClickListener;

    AnnouncementsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
        this.mClickListener = null;
        this.mLongClickListener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_announcements, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Announcement announcement = mData.get(position);
        holder.mTitle.setText(announcement.getTitle());
        holder.mDate.setText(DateFormat.getLongDateFormat(mContext).format(announcement.getDate()));
    }

    @Override
    public int getItemViewType(int position) {
        return (position < mData.size()) ? TYPE_DATA : TYPE_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setLongClickListener(OnLongClickListener mLongClickListener) {
        this.mLongClickListener = mLongClickListener;
    }

    void add(List<Announcement> data) {
        Timber.d("add() called with: data = [" + data + "]");
        int temp = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(temp, data.size());
    }

    void clear() {
        Timber.d("clear() called");
        if (getItemCount() > 0) {
            int temp = mData.size();
            mData.clear();
            notifyItemRangeRemoved(0, temp);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTitle;
        final TextView mDate;
        final CardView mCards;

        ViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.item_announcement_title);
            mDate = view.findViewById(R.id.item_announcement_date);
            mCards = view.findViewById(R.id.item_announcement_cards);
            mCards.setOnClickListener(view1 -> {
                if (mClickListener != null) {mClickListener.onClick(getAdapterPosition());}
            });
            mCards.setOnLongClickListener(
                    view1 -> mLongClickListener != null &&
                             mLongClickListener.onLongClick(getAdapterPosition()));
        }
    }
}
