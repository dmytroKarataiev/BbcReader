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
import android.content.SharedPreferences;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.contracts.Manager;

/**
 * SharedPreferences manager.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class PreferenceManager implements Manager.PrefsManager {

    private static SharedPreferences sPref;
    private static Context sContext;

    public PreferenceManager() {
    }

    @Override
    public void init(Context context) {
        sPref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        sContext = context;
    }

    @Override
    public void clear() {
        sPref.edit().clear().apply();
    }

    @Override
    public SharedPreferences getSharedPrefs() {
        return sPref;
    }

    /**
     * Returns true is user wants to receive notifications,
     * false otherwise.
     * @return boolean to send notifications.
     */
    @Override
    public boolean receiveNotifications() {
        return getSharedPrefs()
                .getBoolean(sContext.getString(R.string.sharedprefs_key_notifications), true);
    }

    /**
     * Returns time in long of last issued notification.
     * @return long time of last notification.
     */
    @Override
    public long getLastNotification() {
        return getSharedPrefs()
                .getLong(sContext.getString(R.string.sharedprefs_key_lastnotification), 0);
    }

    /**
     * Sets last notification date to current time.
     */
    @Override
    public void setLastNotification() {
        getSharedPrefs()
                .edit()
                .putLong(sContext.getString(R.string.sharedprefs_key_lastnotification),
                        System.currentTimeMillis())
                .apply();
    }

    /**
     * Method to get SyncInterval from SharedPreferences
     * @return interval in minutes
     */
    @Override
    public int getSyncInterval() {
        String syncFrequency = getSharedPrefs()
                .getString(sContext.getString(R.string.sharedprefs_key_syncfrequency), "7200");

        return Integer.parseInt(syncFrequency);
    }
}
