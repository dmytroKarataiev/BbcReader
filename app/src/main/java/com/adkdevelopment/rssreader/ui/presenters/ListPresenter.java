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

import android.util.Log;

import com.adkdevelopment.rssreader.App;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.data.remote.Item;
import com.adkdevelopment.rssreader.data.remote.Rss;
import com.adkdevelopment.rssreader.ui.base.BaseMvpPresenter;
import com.adkdevelopment.rssreader.ui.contracts.ListContract;
import com.adkdevelopment.rssreader.utils.Utilities;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter for the ListFragment.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class ListPresenter
        extends BaseMvpPresenter<ListContract.View>
        implements ListContract.Presenter {

    private static final String TAG = ListPresenter.class.getSimpleName();

    private Subscription mSubscription;

    @Override
    public List<NewsRealm> getData() {
        return App.getDataManager().findAll(NewsRealm.class);
    }

    @Override
    public void fetchData() {
        checkViewAttached();
        getMvpView().showProgress(true);

        mSubscription = App.getApiManager().getNewsService().getNews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Rss>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showData(getData());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error Getting: " + e);
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(Rss rss) {
                        Log.d(TAG, "rss.getChannel().getItem().size():" + rss.getChannel().getItem().size());
                        for (Item each : rss.getChannel().getItem()) {
                            App.getDataManager().add(Utilities.convertNews(each));
                            Log.d(TAG, "App.getDataManager().findAll(NewsItem.class).size():"
                                    + App.getDataManager().findAll(NewsRealm.class).size());
                        }
                    }
                });
        getMvpView().showProgress(false);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
