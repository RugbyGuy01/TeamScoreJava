package com.golfpvcc.teamscore.Adapters;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by vinnie on 9/5/2016.
 * This class handling the course list swipes
 */
public class SimpleTouchCallback extends ItemTouchHelper.Callback {
    private SwipeListener mSwipeListener;

    /*
    Setup the interface with the adapter, need to get the swipe object know to the class
    */
    public SimpleTouchCallback(SwipeListener listener) {
        mSwipeListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.END);
    }

    /*
    The user can not drag an item off the screen
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /*
    The user can swipe items off the screen - left - to right only
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /*
    The on MOve is called when a user drags an item off the screen
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    /*
    This function is called when the user starts a swipe on a course record on the screen. if not a course record do not let the item move.
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof AdapterListCourses.CourseHolder) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
    /*
    This function is called when the user starts a swipe on a course record on the screen. if not a course record do not let the item move.
     */
    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof AdapterListCourses.CourseHolder) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
    /*
        The on Swiped is called after the user has swiped the item. However you can not swipte the footer. The course holder holds a course record.
         */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        if (viewHolder instanceof AdapterListCourses.CourseHolder) {
            mSwipeListener.onSwipe(viewHolder.getLayoutPosition());
        }
    }
}
