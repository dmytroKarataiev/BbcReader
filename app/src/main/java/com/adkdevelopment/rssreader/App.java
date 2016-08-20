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

package com.adkdevelopment.rssreader;

import android.app.Application;
import android.content.Context;

import com.adkdevelopment.rssreader.data.managers.ApiManager;
import com.adkdevelopment.rssreader.data.managers.DataManager;
import com.adkdevelopment.rssreader.data.managers.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Custom App class to init all the needed components. Instantiated through the Manifest.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class App extends Application {

    private static Context sContext;

    private static DataManager sDataManager;
    private static ApiManager sApiManager;
    private static PreferenceManager sSharedPrefManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        setupRealmDefaultInstance();
    }

    /**
     * Lazy initialisation of an Api Manager.
     * @return ApiManager
     */
    public static ApiManager getApiManager() {
        if (sApiManager == null) {
            sApiManager = new ApiManager();
            sApiManager.init(getContext());
        }
        return sApiManager;
    }

    /**
     * Lazy initialisation of an Shared Preferences Manager.
     * @return SharedPreferencesManager.
     */
    public static PreferenceManager getSharedPrefManager() {
        if (sSharedPrefManager == null) {
            sSharedPrefManager = new PreferenceManager();
            sSharedPrefManager.init(getContext());
        }
        return sSharedPrefManager;
    }

    /**
     * Lazy initialisation of an DataManager.
     * @return DataManager.
     */
    public static DataManager getDataManager() {
        if (sDataManager == null) {
            sDataManager = new DataManager();
            sDataManager.init(getContext());
        }
        return sDataManager;
    }

    /**
     * Sets up database, deletes on changes.
     * We can possibly improve this part by adding migration rules in the production-ready app.
     */
    private static void setupRealmDefaultInstance() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(sContext)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    /**
     * Method to get App context.
     * @return Context of the App.
     */
    public static Context getContext() {
        return sContext;
    }

    /**
     * Clears everything if necessary.
     */
    public void clear() {
        sApiManager.clear();
        sDataManager.clear();
        sSharedPrefManager.clear();
    }
}
