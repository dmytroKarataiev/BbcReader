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

package com.adkdevelopment.rssreader.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.ui.interfaces.ItemClickListener;
import com.adkdevelopment.rssreader.ui.viewholders.ListViewHolder;
import com.adkdevelopment.rssreader.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to show all news in a RecyclerView.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<NewsObject> mNews;
    private ItemClickListener<Integer, View, NewsObject> mListener;

    public void setTasks(List<NewsObject> newsItems,
                         ItemClickListener<Integer, View, NewsObject> listener) {
        mNews = new ArrayList<>(newsItems);
        mListener = listener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);

        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListViewHolder viewHolder, int position) {
        final int pos = viewHolder.getAdapterPosition();
        viewHolder.setData(mNews.get(position));
        viewHolder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                // nice reveal animation
                Utilities.animationCard(viewHolder);
                mListener.onItemClicked(pos, viewHolder.itemView, mNews.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNews == null ? 0 : mNews.size();
    }

}
