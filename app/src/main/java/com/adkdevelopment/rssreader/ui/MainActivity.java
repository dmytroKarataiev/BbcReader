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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.ui.base.BaseActivity;
import com.adkdevelopment.rssreader.ui.contracts.MainContract;
import com.adkdevelopment.rssreader.ui.interfaces.OnFragmentListener;
import com.adkdevelopment.rssreader.ui.presenters.MainPresenter;
import com.adkdevelopment.rssreader.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main class to start the App. Determines whether we have a phone or a tablet.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class MainActivity extends BaseActivity
        implements MainContract.View, OnFragmentListener {

    private MainPresenter mPresenter;

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device
    private boolean mTwoPane;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MainPresenter(this);
        mPresenter.attachView(this);
        mPresenter.scheduleJob();

        initActionBar();

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            mTwoPane = true;
        }
    }

    /**
     * Performs all preparation procedures to initialize Toolbar and ActionBar
     */
    private void initActionBar() {

        // Set up ActionBar and corresponding icons
        mToolbar.setOnClickListener(view -> {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment);
            if (fragment != null && fragment instanceof ListFragment) {
                ((ListFragment) fragment).scrollToTop();
            }
        });
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.drawable.logo_title);
            actionBar.setTitle(R.string.title_main);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onFragmentInteraction(Integer position, View view, List<NewsObject> item) {
        if (!mTwoPane) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(NewsRealm.NEWS_POSITION, position);
            intent.putExtra(NewsRealm.NEWS_EXTRA, (ArrayList) item);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                @SuppressWarnings("unchecked")
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                        .toBundle();
                startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        } else {
            Bundle args = new Bundle();
            args.putParcelable(NewsRealm.NEWS_EXTRA, item.get(position));
            DetailFragment fragment = DetailFragment.newInstance(item.get(position));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
