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

package com.adkdevelopment.rssreader.data.contracts;

import android.content.Context;
import android.content.SharedPreferences;

import com.adkdevelopment.rssreader.data.RssService;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.data.remote.Item;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

/**
 * Contract for all managers in the app.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public interface Manager {

    void init(Context context);

    void clear();

    /**
     * SharedPreferences manager.
     */
    interface PrefsManager extends Manager {
        SharedPreferences getSharedPrefs();
    }

    /**
     * Manages all model-related issues: data fetching, database work,
     * retrieval of information.
     */
    interface DataManager extends Manager {
        <T extends RealmObject> T addTask(T model);

        Observable<List<NewsObject>> findAll();

        Observable<List<NewsRealm>> addBulk(List<Item> list);

        <T extends RealmObject> List<T> search(Class<T> clazz, String query);
    }

    /**
     * Manages all REST-related work through Retrofit 2.0.
     */
    interface ApiManager extends Manager {
        RssService getNewsService();
    }
}
