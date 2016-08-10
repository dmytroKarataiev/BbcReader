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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsRealm;
import com.adkdevelopment.rssreader.ui.adapters.ListAdapter;
import com.adkdevelopment.rssreader.ui.base.BaseFragment;
import com.adkdevelopment.rssreader.ui.contracts.ListContract;
import com.adkdevelopment.rssreader.ui.interfaces.ItemClickListener;
import com.adkdevelopment.rssreader.ui.presenters.ListPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class ListFragment extends BaseFragment
        implements ListContract.View, ItemClickListener<NewsRealm, View> {

    private ListPresenter mPresenter;
    private ListAdapter mAdapter;
    private boolean isUpdating;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list_empty_text)
    TextView mListEmpty;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private Unbinder mUnbinder;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        mAdapter = new ListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPresenter = new ListPresenter();
        mPresenter.attachView(this);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // on force refresh downloads all data
            mPresenter.fetchData();
        });

        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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


    @Override
    public void showData(List<NewsRealm> itemList) {
        mListEmpty.setVisibility(View.GONE);
        isUpdating = false;
        mAdapter.setTasks(itemList, this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmpty() {
        mListEmpty.setText(getString(R.string.recyclerview_empty_text));
        mListEmpty.setVisibility(View.VISIBLE);
        mAdapter.setTasks(null, null);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        mListEmpty.setVisibility(View.VISIBLE);
        mListEmpty.setText(R.string.fragment_error);
    }

    @Override
    public void showProgress(boolean isInProgress) {
        if (isInProgress) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(NewsRealm item, View view) {
        Log.d("ListFragment", item.getDescription());
    }
}
