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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.ui.base.BaseFragment;
import com.adkdevelopment.rssreader.ui.contracts.DetailContract;
import com.adkdevelopment.rssreader.ui.presenters.DetailPresenter;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class DetailFragment extends BaseFragment implements DetailContract.View {

    private static final String TAG = DetailFragment.class.getSimpleName();

    private DetailPresenter mPresenter;

    @BindView(R.id.detail_title)
    TextView mTextTitle;
    @BindView(R.id.detail_description)
    TextView mTextDescription;
    @BindView(R.id.detail_link)
    TextView mTextLink;
    @BindView(R.id.backdrop)
    ImageView mBackdrop;
    private Unbinder mUnbinder;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(NewsObject item) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(NewsObject.NEWS_EXTRA, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        mPresenter = new DetailPresenter();
        mPresenter.attachView(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mPresenter.loadData(getArguments());
        } else {
            mPresenter.loadData(getActivity().getIntent());
        }
    }

    @Override
    public void showData(NewsObject newsItem) {

        //ImageView backdrop = ButterKnife.findById(getActivity(), R.id.backdrop);
        if (mBackdrop != null) {
            Picasso.with(getContext()).load(newsItem.getThumbnail()).into(mBackdrop);
        }

        mTextTitle.setText(newsItem.getTitle());
        mTextDescription.setText(newsItem.getDescription());
        String learMore = getString(R.string.learn_more) + " " + newsItem.getLink();
        mTextLink.setText(learMore);
    }

    @Override
    public void showError() {
        Log.d(TAG, "Error");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}

