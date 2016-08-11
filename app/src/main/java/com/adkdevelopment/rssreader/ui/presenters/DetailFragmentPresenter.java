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

package com.adkdevelopment.rssreader.ui.presenters;

import android.content.Intent;
import android.os.Bundle;

import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.ui.base.BaseMvpPresenter;
import com.adkdevelopment.rssreader.ui.contracts.DetailFragmentContract;

/**
 * Presenter for a DetailFragment.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class DetailFragmentPresenter
        extends BaseMvpPresenter<DetailFragmentContract.View>
        implements DetailFragmentContract.Presenter {

    private NewsObject mNewsItem;

    @Override
    public void loadData(Intent intent) {
        if (intent == null) {
            getMvpView().showError();
        } else {
            mNewsItem = intent.getParcelableExtra(NewsRealm.NEWS_EXTRA);
            getMvpView().showData(mNewsItem);
        }
    }

    @Override
    public void loadData(Bundle bundle) {
        if (bundle.getParcelable(NewsRealm.NEWS_EXTRA) == null) {
            getMvpView().showError();
        } else {
            mNewsItem = bundle.getParcelable(NewsRealm.NEWS_EXTRA);
            getMvpView().showData(mNewsItem);
        }
    }

    /**
     * Creates a share intent for the ShareActionProvider with details about news.
     * @return Intent with extra info about news.
     */
    @Override
    public Intent getShareIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNewsItem.getTitle() + "\n"
                + mNewsItem.getDescription() + "\n"
                + mNewsItem.getLink());
        return sendIntent;
    }
}
