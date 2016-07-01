package com.prasadam.kmrplayer.adapterClasses.uiAdapters;

import android.support.v7.widget.RecyclerView;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NowPlayingPlaylistAdapter;

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
}
