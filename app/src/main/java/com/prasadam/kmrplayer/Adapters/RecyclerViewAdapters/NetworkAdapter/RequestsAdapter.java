package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.DatabaseHelperClasses.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.Client;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music.FileReceiver;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music.FileSender;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/15/2016.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewAdapter>{

    private final LayoutInflater inflater;
    private final Context context;

    public RequestsAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewAdapter(inflater.inflate(R.layout.recycler_view_event_layout, parent, false));
    }
    public void onBindViewHolder(ViewAdapter holder, final int position) {
        final IRequest request = SharedVariables.fullEventsList.get(position);

        holder.eventTime.setText(getDateStringFormat(request.getTime()));
        onEventClickListener(holder, position, request);
        eventSetter(holder, request);
        setPromptLayout(position, holder, request);
    }

    public int getItemCount() {
        return SharedVariables.fullEventsList.size();
    }

    private void eventSetter(ViewAdapter holder, IRequest request) {

        switch (request.getCommand()){

            case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:{
                if(request.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING){
                    holder.eventIcon.setImageResource(R.drawable.ic_reply_black_24dp);
                    holder.eventIcon.setScaleX(-1);
                    if(request.getServerCurrentSong() != null)
                        holder.requestTextView.setText(request.getClientName() + " is requesting you to send your current playing song (" + request.getServerCurrentSong().getTitle() + ") ?");
                    else
                        holder.requestTextView.setText(request.getClientName() + " is requesting you to send your current playing song ?");
                }

                else{
                    if(request.getServerCurrentSong() != null)
                        holder.requestTextView.setText(request.getClientName() + " requested you to send your current playing song (" + request.getServerCurrentSong().getTitle() + ") ?   (" + request.getEventState() + ")");
                    else
                        holder.requestTextView.setText(request.getClientName() + " requested you to send your current playing song (" + request.getEventState() + ")");
                }
            }
            break;

            case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:{
                if(request.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING){
                    holder.eventIcon.setImageResource(R.drawable.ic_reply_all_black_24dp);
                    holder.eventIcon.setScaleX(-1);
                    holder.requestTextView.setText(request.getClientName() + " is requesting you to receive " + request.getResult() +" songs ?");
                }

                else
                    holder.requestTextView.setText(request.getClientName() + " requested you to receive " + request.getResult() + " songs (" + request.getEventState() + ")");
            }
            break;

            case KeyConstants.SOCKET_INITIATE_GROUP_LISTEN_REQUEST: {
                if (request.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING) {
                    holder.eventIcon.setImageResource(R.drawable.ic_add_to_queue_black_24dp);
                    holder.eventIcon.setScaleX(1);
                    holder.requestTextView.setText(request.getClientName() + " is requesting you to start group listen ?");
                } else
                    holder.requestTextView.setText(request.getClientName() + " requested you to start group listen (" + request.getEventState() + ")");
            }
            break;

        }
    }
    private void setPromptLayout(final int position, final ViewAdapter holder, final IRequest request) {

        if(request.getEventState() != SocketExtensionMethods.EVENT_STATE.WAITING){
            holder.eventAcceptButton.setVisibility(View.GONE);
            holder.eventDenyButton.setVisibility(View.GONE);

            if(request.getEventState() == SocketExtensionMethods.EVENT_STATE.Denied){
                holder.eventIcon.setImageResource(R.drawable.ic_block_white_24dp);
                holder.eventIcon.setScaleX(1);
                holder.eventIcon.setColorFilter(context.getResources().getColor(R.color.red));
            }

            else if(request.getEventState() == SocketExtensionMethods.EVENT_STATE.Approved){
                if(request.getCommand().equals(KeyConstants.SOCKET_INITIATE_GROUP_LISTEN_REQUEST))
                    holder.eventIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
                else
                    holder.eventIcon.setImageResource(R.mipmap.ic_done_white_24dp);
                holder.eventIcon.setScaleX(1);
                holder.eventIcon.setColorFilter(context.getResources().getColor(R.color.teal));
            }

            else if(request.getEventState() == SocketExtensionMethods.EVENT_STATE.Completed){
                holder.eventIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
                holder.eventIcon.setScaleX(1);
                holder.eventIcon.setColorFilter(context.getResources().getColor(R.color.teal));
            }
        }

        else
            setAcceptDenyListener(position, holder, request);
    }
    private void setAcceptDenyListener(final int position, final ViewAdapter holder, final IRequest request) {

        holder.eventDenyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setEventState(SocketExtensionMethods.EVENT_STATE.Denied);
                db4oHelper.updateRequestObject(context, request);
                notifyItemChanged(position);
                eventDenied(request);
            }
        });

        holder.eventAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setEventState(SocketExtensionMethods.EVENT_STATE.Approved);
                db4oHelper.updateRequestObject(context, request);
                notifyItemChanged(position);
                eventApproved(request);
            }
        });
    }
    private void onEventClickListener(ViewAdapter holder, final int position, final IRequest request) {

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new MaterialDialog.Builder(context)
                        .items(R.array.event_context_menu)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which == 0){
                                    db4oHelper.removeRequestObject(context, request);
                                    SharedVariables.fullEventsList.remove(request);
                                    notifyItemRemoved(position);
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
    }
    private String getDateStringFormat(Date eventDate){

        Calendar currentCalender = Calendar.getInstance();
        Calendar eventCalender = Calendar.getInstance();
        currentCalender.setTime(new Date());
        eventCalender.setTime(eventDate);

        if(eventCalender.get(Calendar.YEAR) != currentCalender.get(Calendar.YEAR))
            return new SimpleDateFormat("hh:mm a dd MMM yy").format(eventCalender.getTime());

        else if(eventCalender.get(Calendar.MONTH) != currentCalender.get(Calendar.MONTH))
            return new SimpleDateFormat("hh:mm a dd MMM").format(eventCalender.getTime());

        else if(eventCalender.get(Calendar.DAY_OF_MONTH) != currentCalender.get(Calendar.DAY_OF_MONTH))
            return new SimpleDateFormat("hh:mm a dd").format(eventCalender.getTime());

        else if((eventCalender.get(Calendar.HOUR) != currentCalender.get(Calendar.HOUR)) || (eventCalender.get(Calendar.MINUTE) != currentCalender.get(Calendar.MINUTE)))
            return new SimpleDateFormat("hh:mm a").format(eventCalender.getTime());

        return "just now";
    }

    private void eventDenied(final IRequest request) {
        switch (request.getCommand()){

            case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:{
                SocketExtensionMethods.requestStrictModePermit();
                IRequest requestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                Client quickShareResponse = new Client(request.getClientIpAddress(), requestMessage);
                quickShareResponse.execute();
            }
            break;

            case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:{
                SocketExtensionMethods.requestStrictModePermit();
                IRequest requestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                Client quickShareResponse = new Client(request.getClientIpAddress(), requestMessage);
                quickShareResponse.execute();
            }
            break;

            case KeyConstants.SOCKET_INITIATE_GROUP_LISTEN_REQUEST:{
                SocketExtensionMethods.requestStrictModePermit();
                IRequest requestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_LISTEN_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                Client groupListenResponse = new Client(request.getClientIpAddress(), requestMessage);
                groupListenResponse.execute();
            }
            break;
        }
    }
    private void eventApproved(final IRequest request) {

        switch (request.getCommand()){

            case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketExtensionMethods.requestStrictModePermit();
                        final String currentSongFilePath = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getData();
                        ArrayList<Song> songArrayList = new ArrayList<>();
                        songArrayList.add(PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER));
                        IRequest requestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK, songArrayList);
                        Client quickShareResponse = new Client(request.getClientIpAddress(), requestMessage);
                        quickShareResponse.execute();
                        try {
                            Thread.sleep(1000);
                            FileSender fileSender = new FileSender(context, request);
                            fileSender.sendFile(currentSongFilePath);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            break;


            case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:{
                SocketExtensionMethods.requestStrictModePermit();
                db4oHelper.pushSongTransferObject(context, request.getSongsToTransferArrayList());
                IRequest requestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                Client quickShareResponse = new Client(request.getClientIpAddress(), requestMessage);
                quickShareResponse.execute();
                FileReceiver nioServer = new FileReceiver(context, request);
                nioServer.execute();
            }
            break;

            case KeyConstants.SOCKET_INITIATE_GROUP_LISTEN_REQUEST:{
                SocketExtensionMethods.requestStrictModePermit();
                PlayerConstants.groupListeners.add(request);
                IRequest requestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_LISTEN_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                Client groupListenResponse = new Client(request.getClientIpAddress(), requestMessage);
                groupListenResponse.execute();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        SocketExtensionMethods.sendGroupListenSongBroadCast(context);
                    }
                }, 800);
            }
            break;
        }
    }

    public class ViewAdapter extends RecyclerView.ViewHolder {

        @BindView (R.id.event_time) TextView eventTime;
        @BindView (R.id.request_text_view) TextView requestTextView;
        @BindView (R.id.event_accept) ImageView eventAcceptButton;
        @BindView (R.id.event_deny) ImageView eventDenyButton;
        @BindView (R.id.event_icon) ImageView eventIcon;
        @BindView (R.id.event_root_layout) RelativeLayout relativeLayout;

        public ViewAdapter(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            requestTextView.setSelected(true);
        }
    }
}