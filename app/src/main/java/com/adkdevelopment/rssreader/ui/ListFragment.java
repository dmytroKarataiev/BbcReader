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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adkdevelopment.rssreader.R;
import com.adkdevelopment.rssreader.data.local.NewsObject;
import com.adkdevelopment.rssreader.ui.adapters.ListAdapter;
import com.adkdevelopment.rssreader.ui.base.BaseFragment;
import com.adkdevelopment.rssreader.ui.contracts.ListContract;
import com.adkdevelopment.rssreader.ui.interfaces.ItemClickListener;
import com.adkdevelopment.rssreader.ui.interfaces.OnFragmentListener;
import com.adkdevelopment.rssreader.ui.presenters.ListPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Dmytro Karataiev on 8/10/16.
 */
public class ListFragment extends BaseFragment
        implements ListContract.View, ItemClickListener<Integer, View, List<NewsObject>> {

    private ListPresenter mPresenter;
    private ListAdapter mAdapter;

    private OnFragmentListener mListener;

    // to prevent NPE with SharedTransitions while updating the data
    private boolean mInProgress;

    // Due to using Presenter - we have to save position of a RecyclerView manually
    private static final String POSITION = "pos";
    private int mPosition = 0;

    // RxAndroid EditText Subscription
    private Subscription mSubscription;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list_empty_text)
    TextView mListEmpty;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private Unbinder mUnbinder;

    private static final String TAG = ListFragment.class.getSimpleName();

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListFragment.
     */
    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

        mPresenter = new ListPresenter(getContext());
        mPresenter.attachView(this);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // on force refresh downloads all data
            mPosition = 0;
            mPresenter.fetchData();
        });

        mPresenter.requestData();
        mPresenter.fetchData();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EditText editText = ButterKnife.findById(getActivity(), R.id.searchBar);
        if (editText != null) {

            mSubscription = RxTextView.textChangeEvents(editText)
                    .debounce(400, TimeUnit.MILLISECONDS)
                    // filters onCreate event when there was nothing in the EditText,
                    // so the list of movies won't be updated
                    .filter(textViewTextChangeEvent -> !(textViewTextChangeEvent.count() == 0 &&
                            textViewTextChangeEvent.before() == 0))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getSearchObserver());
        }
    }

    /**
     * Creates an Observer for an EditText Subscription.
     * @return Observer which fetches an update.
     */
    private Observer<TextViewTextChangeEvent> getSearchObserver() {
        return new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error!" + e);
            }

            @Override
            public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                String searchParameter = onTextChangeEvent.view().getText().toString();

                if (onTextChangeEvent.view().getText().length() < 4) {
                    searchParameter = "";
                }

                mPresenter.requestData(searchParameter);
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mUnbinder.unbind();
    }

    @Override
    public void showData(List<NewsObject> itemList) {
        mListEmpty.setVisibility(View.GONE);
        mAdapter.setTasks(itemList, this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mPosition);
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
        mInProgress = isInProgress;
        if (isInProgress) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(Integer position, View view, List<NewsObject> item) {
        if (mListener != null && !mInProgress) {
            mListener.onFragmentInteraction(position, view, item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        outState.putInt(POSITION, mPosition);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(POSITION);
        }
    }

    /**
     * Helper method which is called from PagerActivity when press on TabLayout twice
     * Scrolls the screen to the top
     */
    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

}
