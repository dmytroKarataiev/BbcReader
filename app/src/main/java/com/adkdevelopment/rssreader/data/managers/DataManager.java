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
import com.adkdevelopment.rssreader.data.local.NewsRealm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;

/**
 * Datamanager to perform all Database-related work.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class DataManager implements Manager.DataManager {

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
     * Returns all objects matching to the class parameter
     *
     * @param clazz class of the object
     * @param <T>   type of of the object
     * @return all matching elements
     */
    public <T extends RealmObject> Observable<List<T>> findAll(Class<T> clazz) {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    subscriber.onNext(mRealm.where(clazz)
                            .findAll()
                            .sort(NewsRealm.PUBDATE, Sort.DESCENDING));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        });
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
