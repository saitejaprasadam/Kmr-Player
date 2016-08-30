package com.prasadam.kmrplayer.Interfaces;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NowPlayingPlaylistAdapter;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;

/*
 * Created by Prasadam Saiteja on 6/30/2016.
 */

public class NowPlayingPlaylistInterfaces {

    public interface ItemTouchHelperAdapter {

        boolean onItemMove(int fromPosition, int toPosition, NowPlayingPlaylistAdapter.MyViewHolder sourceViewHolder ,NowPlayingPlaylistAdapter.MyViewHolder targetViewHolder);
        void onItemDismiss(int position);
    }
    public interface ItemTouchHelperViewHolder {
        void onItemSelected();
        void onItemClear();
    }
    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public static class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        public static final float ALPHA_FULL = 1.0f;
        private static float swipingAlphaValue = 0.5f;

        private final ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // Set movement flags based on the layout manager
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                swipingAlphaValue = viewHolder.itemView.getAlpha();
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }

            // Notify the adapter of the move
            mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition(), (NowPlayingPlaylistAdapter.MyViewHolder) source, (NowPlayingPlaylistAdapter.MyViewHolder) target);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            // Notify the adapter of the dismissal
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Fade out the view as it is swiped out of the parent's bounds
                //NowPlayingPlaylistAdapter.MyViewHolder temp = (NowPlayingPlaylistAdapter.MyViewHolder) viewHolder;
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // We only want the active item to change
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    // Let the view holder know that this item is being moved or dragged
                    ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            Log.d(String.valueOf(viewHolder.getAdapterPosition()), String.valueOf(PlayerConstants.SONG_NUMBER));
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            if(viewHolder.getAdapterPosition() < PlayerConstants.SONG_NUMBER){
                Log.d("Half", " color");
                viewHolder.itemView.setAlpha(0.5f);
            }

            else{
                Log.d("Full", " color");
                viewHolder.itemView.setAlpha(ALPHA_FULL);
            }

            /*
            if (viewHolder instanceof NowPlayingPlaylistInterfaces.ItemTouchHelperViewHolder) {
                // Tell the view holder it's time to restore the idle state
                NowPlayingPlaylistInterfaces.ItemTouchHelperViewHolder itemViewHolder = (NowPlayingPlaylistInterfaces.ItemTouchHelperViewHolder) viewHolder;
                //itemViewHolder.onItemClear();
            }*/
        }
    }
}
