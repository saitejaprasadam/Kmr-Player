package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasadam.kmrplayer.ModelClasses.TransferableSong;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/*
 * Created by Prasadam Saiteja on 9/22/2016.
 */

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewAdapter>{

    private final LayoutInflater inflater;
    private final Context context;

    public TransferAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public TransferAdapter.ViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewAdapter(inflater.inflate(R.layout.recycler_view_transfer_layout, parent, false));
    }
    public void onBindViewHolder(TransferAdapter.ViewAdapter holder, int position) {
        final TransferableSong transferableSong = SharedVariables.fullTransferList.get(position);

        holder.songTitle.setText(transferableSong.getSong().getTitle());
        holder.songArtist.setText(transferableSong.getSong().getArtist());

        setActionIcon(holder, transferableSong);
    }
    public int getItemCount() {
        return SharedVariables.fullTransferList.size();
    }

    private void setActionIcon(TransferAdapter.ViewAdapter holder, TransferableSong transferableSong) {

        if(transferableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.WAITING){
            holder.transferActionButton.setImageResource(R.drawable.ic_close_white_24dp);
            holder.transferActionButton.setColorFilter(context.getResources().getColor(R.color.red));
        }

        else if(transferableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.IN_PROGRESS){
            holder.transferActionButton.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        else if(transferableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.Completed){
            holder.progressBar.setVisibility(View.GONE);
            holder.transferActionButton.setVisibility(View.VISIBLE);
            holder.transferActionButton.setImageResource(R.drawable.ic_done_all_white_24dp);
            holder.transferActionButton.setColorFilter(context.getResources().getColor(R.color.teal));
        }

        else if(transferableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.Denied){
            holder.progressBar.setVisibility(View.GONE);
            holder.transferActionButton.setVisibility(View.VISIBLE);
            holder.transferActionButton.setImageResource(R.drawable.ic_block_white_24dp);
            holder.transferActionButton.setColorFilter(context.getResources().getColor(R.color.red));
        }
    }

    public class ViewAdapter extends RecyclerView.ViewHolder {

        @BindView(R.id.songs_album_art) ImageView albumArt;
        @BindView(R.id.songTitle) TextView songTitle;
        @BindView(R.id.songArtist) TextView songArtist;
        @BindView(R.id.transfer_action_button) ImageView transferActionButton;
        @BindView(R.id.transfer_progress_bar) MaterialProgressBar progressBar;
        @BindView(R.id.transfer_actions) FrameLayout action_container;

        public ViewAdapter(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}