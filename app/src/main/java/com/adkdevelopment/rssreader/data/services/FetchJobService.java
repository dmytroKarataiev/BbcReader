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

package com.adkdevelopment.rssreader.data.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.adkdevelopment.rssreader.App;
import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.managers.PrefsManager;
import com.adkdevelopment.rssreader.data.remote.Rss;
import com.adkdevelopment.rssreader.ui.MainActivity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * JobService to fetch news and issue notifications.
 * Created by Dmytro Karataiev on 8/10/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FetchJobService extends JobService {

    private static final String TAG = FetchJobService.class.getSimpleName();

    private Rss mRss;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // fetches news from bbc
        App.getApiManager().getNewsService().getNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Rss>() {
                    @Override
                    public void onCompleted() {
                        if (mRss != null
                                && mRss.getChannel().getItem().size() > 0) {
                            addToDb();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "e:" + e);
                    }

                    @Override
                    public void onNext(Rss rss) {
                        mRss = rss;
                    }
                });

        sendNotification();
        return false;
    }

    /**
     * Adds news to the database.
     */
    private void addToDb() {
        App.getDataManager().addBulk(mRss.getChannel().getItem())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Job Error Getting: " + e);
                    }

                    @Override
                    public void onNext(Boolean added) {
                    }
                });
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    /**
     * Raises a notification with a biggest earthquake with each sync
     */
    private void sendNotification() {

        PrefsManager prefsManager = new PrefsManager(getBaseContext());

        Context context = getApplicationContext();
        final String NOTIFICATION_GROUP = "notif_group";
        final int NOTIFICATION_ID_1 = 101;

        if (prefsManager.receiveNotifications()) {
            //checking the last update and notify if it's the first of the day
            if (System.currentTimeMillis() - prefsManager.getLastNotification()
                    >= DateUtils.DAY_IN_MILLIS) {

                Intent intent = new Intent(context, MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        NOTIFICATION_ID_1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                builder
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.articles_notification))
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.logo_title)
                        .setTicker(context.getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.articles_notification)))
                        .setGroup(NOTIFICATION_GROUP)
                        .setGroupSummary(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify(NOTIFICATION_ID_1, builder.build());

                prefsManager.setLastNotification();
            }

        }
    }

}
