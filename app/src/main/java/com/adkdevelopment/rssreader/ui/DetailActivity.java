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

package com.adkdevelopment.rssreader.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.ui.adapters.PagerAdapter;
import com.adkdevelopment.rssreader.ui.base.BaseActivity;
import com.adkdevelopment.rssreader.ui.behavior.ZoomOutPageTransformer;
import com.adkdevelopment.rssreader.ui.contracts.DetailActivityContract;
import com.adkdevelopment.rssreader.ui.presenters.DetailActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * DetailActivity for a phone, which show additional info about news.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class DetailActivity extends BaseActivity implements DetailActivityContract.View {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.pager)
    ViewPager mPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private DetailActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mPresenter = new DetailActivityPresenter();
        mPresenter.attachView(this);

        mPresenter.loadData(getIntent());

        initActionBar();

    }

    /**
     * Performs all preparation procedures to initialize Toolbar and ActionBar
     */
    private void initActionBar() {

        // Initialize a custom Toolbar
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        // Add back button to the actionbar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.logo_title);
            actionBar.setTitle(R.string.title_main);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // copy the behavior of the hardware back button
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showData(List<NewsObject> itemList) {
        PagerAdapter mAdapter = new PagerAdapter(getSupportFragmentManager());
        mAdapter.setNewsItems(itemList);
        mPager.setAdapter(mAdapter);

        // zoom effect on swipe
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setCurrentItem(getIntent().getIntExtra(NewsRealm.NEWS_POSITION, 0));
    }

    @Override
    public void showError(Throwable e) {
        Log.e(TAG, "e:" + e);
    }
}
