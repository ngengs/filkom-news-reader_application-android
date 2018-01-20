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

package com.ngengs.android.app.filkomnewsreader.ui.newsdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.model.NewsContent;
import com.ngengs.android.app.filkomnewsreader.ui.imagesdetail.ImagesDetailActivity;
import com.ngengs.android.app.filkomnewsreader.utils.glideapp.GlideApp;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class NewsDetailAdapter extends RecyclerView.Adapter<NewsDetailAdapter.ViewHolder> {

    private final List<NewsContent> mData;
    private final Context mContext;

    NewsDetailAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
    }

    @Override
    public NewsDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_news_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsDetailAdapter.ViewHolder holder, int position) {
        Timber.d("onBindViewHolder() called with: holder = [" + holder + "], position = [" +
                 position + "]");
        NewsContent content = mData.get(position);
        holder.mTextDefault.setVisibility(View.GONE);
        holder.mTextQuote.setVisibility(View.GONE);
        holder.mIndicatorQuote.setVisibility(View.GONE);
        holder.mImage.setVisibility(View.GONE);
        switch (content.getType()) {
            case 2:
                holder.mTextQuote.setVisibility(View.VISIBLE);
                holder.mIndicatorQuote.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.mTextQuote.setText(
                            Html.fromHtml(content.getContent(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.mTextQuote.setText(Html.fromHtml(content.getContent()));
                }
                holder.mTextQuote.setMovementMethod(LinkMovementMethod.getInstance());
                break;
            case 3:
                holder.mImage.setVisibility(View.VISIBLE);
                holder.mImage.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, ImagesDetailActivity.class);
                    intent.putExtra(ImagesDetailActivity.INTENT_ARGS_DATA, content.getContent());
                    mContext.startActivity(intent);
                });
                GlideApp.with(mContext)
                        .load(content.getContent())
                        .thumbnail(0.05f)
                        .centerInside()
                        .into(holder.mImage);
                break;
            case 1:
            default:
                holder.mTextDefault.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.mTextDefault.setText(
                            Html.fromHtml(content.getContent(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.mTextDefault.setText(Html.fromHtml(content.getContent()));
                }
                holder.mTextDefault.setMovementMethod(LinkMovementMethod.getInstance());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(List<NewsContent> data) {
        Timber.d("add() called with: contents = [" + data.size() + "]");
        int temp = mData.size();
        mData.addAll(data);
        Timber.d("add: %s", mData.size());
        notifyItemRangeInserted(temp, data.size());
    }

    void clear() {
        if (mData.size() > 0) {
            int temp = mData.size();
            mData.clear();
            notifyItemRangeRemoved(0, temp);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextDefault;
        final TextView mTextQuote;
        final RelativeLayout mIndicatorQuote;
        final ImageView mImage;

        ViewHolder(View view) {
            super(view);
            this.mTextDefault = view.findViewById(R.id.text_default_news_detail);
            this.mTextQuote = view.findViewById(R.id.text_quote_news_detail);
            this.mIndicatorQuote = view.findViewById(R.id.indicator_quote_news_detail);
            this.mImage = view.findViewById(R.id.image_news_detail);
        }
    }
}
