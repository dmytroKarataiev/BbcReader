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

import android.util.Log;

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
}
