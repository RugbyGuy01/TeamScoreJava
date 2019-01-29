package com.golfpvcc.teamscore.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.golfpvcc.teamscore.Extras.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vinnie on 9/2/2016.
 */
public class CourseListRecyclerView extends RecyclerView {
    private List<View> mNonEmptyViews = Collections.emptyList();  // Views that display when we have course records ( Database is not empty)
    // The Collections call make sure we do not have null pointers in our list.
    private List<View> mEmptyViews = Collections.emptyList();     // Views that display when we have no course records ( Database is empty)
    /*
    This is the sub class used by the Course List Recycler view
     */
    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            tooggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            tooggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            tooggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            tooggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            tooggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            tooggleViews();
        }
    };

    /*
    We are going to toggle the views on the screen, make sure the Adapter is valid and we have a item in each of the arrays
     */
    private void tooggleViews() {
        if (getAdapter() != null && !mEmptyViews.isEmpty() && !mNonEmptyViews.isEmpty()) {
            if (getAdapter().getItemCount() == 0) { // the adapter doesn't any items to display
                // show all empty views
                Util.showViews(mEmptyViews);    // Hide the recycler view - ie the adapter
                setVisibility(View.GONE);
                Util.hideViews(mNonEmptyViews); // hide all of the views which are meant to be hidden

            } else {
                Util.showViews(mNonEmptyViews); // show all non empty views
                setVisibility(View.VISIBLE);    // show the recycler view - ie the adapter
                Util.hideViews(mEmptyViews);    // hide all of the views which are meant to be hidden
            }
        }
    }

    /*
    This is the constructor for the Course List Recycler View
     */
    public CourseListRecyclerView(Context context) {
        super(context);
    }

    public CourseListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
    This is making a custom adapter for the golf app.
     */
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver); // here our sub class
            mDataObserver.onChanged();                          // this function will call our sub class on change method.
        }
    }

    /*
    custom method to hide or show the tool bar based on the database records.
     */
    public void hideIfEmpty(View... views) {
        mNonEmptyViews = Arrays.asList(views);  // we are passing in a list of view
    }

    /*
    custom method to hide or show the tool bar based on the database records.
     */
    public void showIfEmpty(View... EmptyViews) {
        mEmptyViews = Arrays.asList(EmptyViews);    // we are passing in a list of view
    }
}
