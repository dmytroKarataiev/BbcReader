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

import android.content.Context;

import com.adkdevelopment.rssreader.data.contracts.Manager;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.data.local.NewsRealm;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import rx.Observable;

/**
 * Datamanager to perform all Database-related work.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class DataManager implements Manager.DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private Realm mRealm;

    @Override
    public void init(Context context) {
        mRealm = getRealmInstance();
    }

    @Override
    public void clear() {

    }

    private Realm getRealmInstance() {
        return Realm.getDefaultInstance();
    }

    /**
     * Add RealmObjects to the Database.
     *
     * @param model object to add
     * @param <T>   type of the object
     * @return added RealmObject.
     */
    public <T extends RealmObject> T add(T model) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(model);
        mRealm.commitTransaction();
        return model;
    }

    /**
     * Returns all news objects.
     *
     * @return all matching elements
     */
    public Observable<List<NewsObject>> findAll() {

        List<NewsObject> objects = new ArrayList<>();
        mRealm.where(NewsRealm.class).findAll().sort(NewsRealm.PUBDATE, Sort.DESCENDING)
                .asObservable()
                .subscribe(newsItems -> {
                    for (NewsRealm each : newsItems) {
                        objects.add(convertNews(each));
                    }
                });

        return Observable.just(objects);
    }

    /**
     * Converts NewsItem to NewsObject to be able to pass it between threads.
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
     * @param clazz which we are looking for
     * @param query to find all matching objects to.
     * @param <T>   type of the object
     * @return all objects containing query
     */
    public <T extends RealmObject> List<T> search(Class<T> clazz, String query) {
        return mRealm.where(clazz)
                // TODO: 8/10/16 add search
                .contains("", query)
                .findAll();
    }
}
