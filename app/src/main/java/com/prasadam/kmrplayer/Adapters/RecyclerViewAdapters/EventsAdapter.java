package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.Client;
import com.prasadam.kmrplayer.SocketClasses.Event;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.FileReceiver;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.FileSender;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/15/2016.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewAdapter>{

    private LayoutInflater inflater;
    private Context context;

    public EventsAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_event_layout, parent, false);
        return new ViewAdapter(view);
    }
    public void onBindViewHolder(ViewAdapter holder, final int position) {
        final Event event = db4oHelper.getEventObjects(context).get(position);

        holder.eventTime.setText(getDateStringFormat(event.getTime()));
        onEventClickListener(holder, position, event);
        eventSetter(holder, event);
        setPromptLayout(position, holder, event);
    }

    public int getItemCount() {
        return db4oHelper.getEventObjects(context).size();
    }

    private void eventSetter(ViewAdapter holder, Event event) {

        switch (event.getCommand()){

            case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:{
                if(event.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING){
                    holder.eventIcon.setImageResource(R.drawable.ic_reply_black_24dp);
                    holder.eventIcon.setScaleX(-1);
                    holder.requestTextView.setText(event.getClientName() + " is requesting you to send your current playing song ?");
                }

                else
                    holder.requestTextView.setText(event.getClientName() + " requested you to send your current playing song ?   (" + event.getEventState() + ")");
            }
            break;

            case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:{
                if(event.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING){
                    holder.eventIcon.setImageResource(R.drawable.ic_reply_all_black_24dp);
                    holder.eventIcon.setScaleX(-1);
                    holder.requestTextView.setText(event.getClientName() + " is requesting you to receive " + event.getResult() +" songs ?");
                }

                else
                    holder.requestTextView.setText(event.getClientName() + " requested you to receive " + event.getResult() + " songs ?   (" + event.getEventState() + ")");
            }
            break;

        }
    }
    private void setPromptLayout(final int position, final ViewAdapter holder, final Event event) {

        if(event.getEventState() != SocketExtensionMethods.EVENT_STATE.WAITING){
            holder.eventAcceptButton.setVisibility(View.GONE);
            holder.eventDenyButton.setVisibility(View.GONE);

            if(event.getEventState() == SocketExtensionMethods.EVENT_STATE.Denied){
                holder.eventIcon.setImageResource(R.drawable.ic_block_white_24dp);
                holder.eventIcon.setScaleX(1);
                holder.eventIcon.setColorFilter(context.getResources().getColor(R.color.red));
            }

            else if(event.getEventState() == SocketExtensionMethods.EVENT_STATE.Approved){
                holder.eventIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
                holder.eventIcon.setScaleX(1);
                holder.eventIcon.setColorFilter(context.getResources().getColor(R.color.Teal));
            }
        }

        else
            setAcceptDenyListener(position, holder, event);
    }
    private void setAcceptDenyListener(final int position, final ViewAdapter holder, final Event event) {

        holder.eventDenyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.setEventState(SocketExtensionMethods.EVENT_STATE.Denied);
                db4oHelper.updateEventObject(context, event);
                notifyItemChanged(position);
                eventDenied(event);
            }
        });

        holder.eventAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.setEventState(SocketExtensionMethods.EVENT_STATE.Approved);
                db4oHelper.updateEventObject(context, event);
                notifyItemChanged(position);
                eventApproved(event);
            }
        });
    }
    private void onEventClickListener(ViewAdapter holder, final int position, final Event event) {

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new MaterialDialog.Builder(context)
                        .items(R.array.event_context_menu)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which == 0){
                                    db4oHelper.removeEventObject(context, event);
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

    private void eventDenied(Event event) {
        switch (event.getCommand()){

            case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:{
                SocketExtensionMethods.requestStrictModePermit();
                String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                Client quickShareResponse = new Client(event.getClientIpAddress(), result);
                quickShareResponse.execute();
            }
            break;

            case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:{
                SocketExtensionMethods.requestStrictModePermit();
                String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, event.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                Client quickShareResponse = new Client(event.getClientIpAddress(), result);
                quickShareResponse.execute();
            }
            break;
        }
    }
    private void eventApproved(final Event event) {

        switch (event.getCommand()){

            case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketExtensionMethods.requestStrictModePermit();
                        final String currentSongFilePath = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getData();
                        String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                        Client quickShareResponse = new Client(event.getClientIpAddress(), result);
                        quickShareResponse.execute();
                        try {
                            Thread.sleep(1000);
                            FileSender fileSender = new FileSender(event.getClientIpAddress());
                            fileSender.sendFile(currentSongFilePath);
                            fileSender.endConnection();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            break;


            case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:{
                SocketExtensionMethods.requestStrictModePermit();
                String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, event.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                Client quickShareResponse = new Client(event.getClientIpAddress(), result);
                quickShareResponse.execute();
                FileReceiver nioServer = new FileReceiver(context, Integer.valueOf(event.getResult()));
                nioServer.execute();
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
        }
    }
}