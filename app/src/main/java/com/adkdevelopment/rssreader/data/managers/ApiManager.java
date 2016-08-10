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

import com.adkdevelopment.rssreader.data.RssService;
import com.adkdevelopment.rssreader.data.contracts.Manager;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Api Manager which does all network-related work with BBC API.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class ApiManager implements Manager.ApiManager {

    private final String BASE_URL = "http://feeds.bbci.co.uk/news/";

    private Retrofit RSS_ADAPTER;
    private RssService RSS_SERVICE;

    @Override
    public RssService getNewsService() {
        return RSS_SERVICE;
    }

    @Override
    public void init(Context context) {
        initRetrofit();
        initService();
    }

    /**
     * Initialises Retrofit with a BASE_URL, XML converted and Rx adapter.
     */
    private void initRetrofit() {
        RSS_ADAPTER = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private void initService() {
        RSS_SERVICE = RSS_ADAPTER.create(RssService.class);
    }

    @Override
    public void clear() {
        RSS_ADAPTER = null;
        RSS_SERVICE = null;
    }
}
