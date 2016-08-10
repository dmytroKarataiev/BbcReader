/*
 * MIT License
 *
 * Copyright (c) 2016. Dmytro Karataiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adkdevelopment.rssreader.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.utils.Utilities;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

/**
 * ViewHolder for a News Item element in a RecyclerView.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class ListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_news)
    ImageView mImageThumbnail;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.text_news_title)
    TextView mTextTitle;
    @BindView(R.id.text_news_date)
    TextView mTextDate;

    public ListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Sets the data to the News item in a list view
     * @param item which data to use.
     */
    public void setData(NewsRealm item) {

        mTextTitle.setText(item.getTitle());
        mTextDate.setText(Utilities.getRelativeDate(item.getPubDate()));
        mImageThumbnail.setContentDescription(item.getTitle());

        Picasso.with(itemView.getContext())
                .load(item.getThumbnail())
                .resize(NewsRealm.THUMBNAIL_SIZE, NewsRealm.THUMBNAIL_SIZE)
                .onlyScaleDown()
                .centerCrop()
                .error(R.drawable.image_placeholder)
                .into(mImageThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }
}
