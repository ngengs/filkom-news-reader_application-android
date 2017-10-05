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

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.utils.listener.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final Context mContext;
    private final List<News> mData;
    private OnClickListener mListener;

    NewsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
        this.mListener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = mData.get(position);
        holder.mTitle.setText(news.getTitle());
        holder.mDescription.setText(news.getShortDescription());

        Glide.with(mContext).load(news.getImage()).thumbnail(0.05f).into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void add(List<News> data) {
        int temp = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(temp, data.size());
    }

    void clear() {
        if (getItemCount() > 0) {
            int temp = mData.size();
            mData.clear();
            notifyItemRangeRemoved(0, temp);
        }
    }

    void setClickListener(OnClickListener mListener) {
        this.mListener = mListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImage;
        final TextView mTitle;
        final TextView mDescription;
        final CardView mCards;

        ViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.item_news_image);
            mTitle = view.findViewById(R.id.item_news_title);
            mDescription = view.findViewById(R.id.item_news_description);
            mCards = view.findViewById(R.id.item_news_cards);
            mCards.setOnClickListener(view1 -> {
                if (mListener != null) {
                    mListener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
