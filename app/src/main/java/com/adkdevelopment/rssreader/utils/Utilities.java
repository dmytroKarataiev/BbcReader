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

package com.adkdevelopment.rssreader.utils;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewAnimationUtils;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.data.remote.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilities class with helper functions.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class Utilities {
    private static final String TAG = Utilities.class.getSimpleName();

    /**
     * Converts date format EEE, d MMM yyyy HH:mm:ss Z to long
     * @param unformattedDate in a String.
     * @return long representation of the date.
     */
    public static long convertDate(String unformattedDate) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        Date date = new Date();

        try {
            date = simpleDateFormat.parse(unformattedDate);
        } catch (ParseException e) {
            Log.e(TAG, "e:" + e);
        }

        return date.getTime();

    }

    /**
     * Method to convert Item to a Realm NewsItem for further addition to a Database.
     * @param item Item to convert.
     * @return NewsItem to insert to a database.
     */
    public static NewsRealm convertNews(Item item) {
        NewsRealm newsItem = new NewsRealm();
        newsItem.setDescription(item.getDescription());
        newsItem.setPubDate(convertDate(item.getPubDate()));
        newsItem.setLink(item.getLink());
        newsItem.setTitle(item.getTitle());
        newsItem.setThumbnail(item.getThumbnail().getUrl());
        newsItem.setWidth(Integer.parseInt(item.getThumbnail().getWidth()));
        newsItem.setHeight(Integer.parseInt(item.getThumbnail().getHeight()));
        return newsItem;
    }

    /**
     * Returns formatted relative data
     * @param millis date to format in milliseconds
     * @return String with relative date (ex: 7 days ago)
     */
    public static String getRelativeDate(Long millis) {
        Date date = new Date(millis);
        return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
    }

    public static int sBlueColor;
    public static int sWhiteColor;

    /**
     * Animates RecyclerView card on click with revealing effect
     * @param viewHolder to make animation on
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animationCard(RecyclerView.ViewHolder viewHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sBlueColor == 0) {
                sBlueColor = ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.colorPrimary);
            }
            if (sWhiteColor == 0) {
                sWhiteColor = ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white);
            }

            int finalRadius = (int) Math.hypot(viewHolder.itemView.getWidth() / 2, viewHolder.itemView.getHeight() / 2);

            Animator anim = ViewAnimationUtils.createCircularReveal(viewHolder.itemView,
                    viewHolder.itemView.getWidth() / 2,
                    viewHolder.itemView.getHeight() / 2, 0, finalRadius);

            viewHolder.itemView.setBackgroundColor(sBlueColor);
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    viewHolder.itemView.setBackgroundColor(sWhiteColor);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * Method to check if device is connected to the internet
     * @param context from which call is being made
     * @return true if connected, false otherwise
     */
    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * Retrieves palette from a drawable and returns colors.
     * @param drawable to get palette from.
     * @return int[] with 3 colors.
     */
    public static int[] getPalette(BitmapDrawable drawable) {

        int[] colors = new int[]{0, 0, 0};
        if (drawable != null) {
            Palette palette = Palette.from(drawable.getBitmap()).generate();

            colors[0] = palette.getLightVibrantColor(0);
            if (colors[0] == 0) {
                colors[0] = palette.getLightMutedColor(0);
            }

            colors[1] = palette.getVibrantColor(0);
            if (colors[1] == 0) {
                colors[1] = palette.getMutedColor(0);
            }

            colors[2] = palette.getDarkVibrantColor(0);
            if (colors[2] == 0) {
                colors[2] = palette.getDarkMutedColor(0);
            }
        }

        return colors;
    }
}
