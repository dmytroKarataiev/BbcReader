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

package com.adkdevelopment.rssreader.data.managers;

import android.text.format.DateUtils;
import android.util.Log;

import com.adkdevelopment.rssreader.data.contracts.Manager;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.data.remote.Item;
import com.adkdevelopment.rssreader.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;

/**
 * Datamanager to perform all Database-related work.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class DataManager implements Manager.DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    @Override
    public void init() {
    }

    @Override
    public void clear() {
    }

    /**
     * Add RealmObjects to the Database.
     *
     * @param model object to add
     * @return added RealmObject.
     */
    @Override
    public <T extends RealmObject> T addTask(T model) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
        realm.close();
        return model;
    }

    /**
     * Adds data in bulk to the database.
     *
     * @param list of objects to add to the DB.
     * @return Observable of added and converted to Realm Object items.
     */
    @Override
    public Observable<Boolean> addBulk(final List<Item> list) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    for (Item each : list) {
                        addTask(Utilities.convertNews(each));
                    }
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Returns all news objects.
     *
     * @return all matching elements
     */
    @Override
    public Observable<List<NewsObject>> findAll() {

        // Delete news older than 24 hours.

        Realm mRealm = Realm.getDefaultInstance();

        RealmResults<NewsRealm> results = mRealm.where(NewsRealm.class)
                .lessThan(NewsRealm.PUBDATE, System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS)
                .findAll();
        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();

        List<NewsObject> objects = new ArrayList<>();
        results = mRealm.where(NewsRealm.class).findAll().sort(NewsRealm.PUBDATE, Sort.DESCENDING);
        //noinspection Convert2streamapi
        for (NewsRealm each : results) {
            objects.add(convertNews(each));
        }

        mRealm.close();
        return Observable.just(objects);
    }

    /**
     * Converts NewsItem to NewsObject to be able to pass it between threads.
     *
     * @param newsItem to convert.
     * @return NewsObject from newsItem.
     */
    private static NewsObject convertNews(NewsRealm newsItem) {
        NewsObject news = new NewsObject();
        news.setDescription(newsItem.getDescription());
        news.setHeight(newsItem.getHeight());
        news.setTitle(newsItem.getTitle());
        news.setLink(newsItem.getLink());
        news.setWidth(newsItem.getWidth());
        news.setPubDate(newsItem.getPubDate());
        news.setThumbnail(newsItem.getThumbnail());

        return news;
    }

    /**
     * Returns all matching objects to the query parameter
     *
     * @param query to find all matching objects to.
     * @return all objects containing query
     */
    @Override
    public Observable<List<NewsObject>> search(String query) {
        List<NewsObject> objects = new ArrayList<>();
        Realm mRealm = Realm.getDefaultInstance();

        RealmResults<NewsRealm> results = mRealm.where(NewsRealm.class)
                .contains(NewsRealm.TITLE, query, Case.INSENSITIVE)
                .findAll()
                .sort(NewsRealm.PUBDATE, Sort.DESCENDING);

        //noinspection Convert2streamapi
        for (NewsRealm each : results) {
            objects.add(convertNews(each));
        }

        mRealm.close();
        return Observable.just(objects);
    }
}
