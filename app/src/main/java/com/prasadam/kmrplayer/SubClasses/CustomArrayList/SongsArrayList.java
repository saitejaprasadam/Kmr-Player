package com.prasadam.kmrplayer.SubClasses.CustomArrayList;

import com.prasadam.kmrplayer.ModelClasses.Song;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Created by Prasadam Saiteja on 8/29/2016.
 */

public abstract class SongsArrayList extends ArrayList<Song> {

    public SongsArrayList(){}
    public SongsArrayList(ArrayList<Song> song){
        super.addAll(song);
        notifyDataSetChanged();
    }

    public boolean add(Song song) {
        boolean result = super.add(song);
        notifyItemInserted(this.size() - 1);
        return result;
    }
    public void add(int index, Song song){
        super.add(index, song);
        notifyItemInserted(index);
    }
    public boolean addAll(Collection<? extends Song> song) {
        boolean result = super.addAll(song);
        notifyDataSetChanged();
        return result;
    }
    public boolean addAll(int index, Collection<? extends Song> song) {
        boolean result = super.addAll(index, song);
        notifyDataSetChanged();
        return result;
    }
    public boolean setArrayList(Collection<? extends Song> song) {
        super.clear();
        boolean result = super.addAll(song);
        notifyDataSetChanged();
        return result;
    }

    public Song set(int index, Song song){
        Song result = super.set(index, song);
        notifyItemChanged(index);
        return result;
    }

    public Song remove(int index){
        Song result =  super.remove(index);
        notifyItemRemoved(index);
        return result;
    }
    public boolean remove(Song song){
        int index = super.indexOf(song);
        boolean result = super.remove(song);
        notifyItemRemoved(index);
        return result;
    }
    public boolean removeAll(Collection<?> song) {
        boolean result = super.removeAll(song);
        notifyDataSetChanged();
        return result;
    }
    public void removeRange(int fromIndex, int toIndex){
        super.removeRange(fromIndex, toIndex);
        notifyDataSetChanged();
    }

    public void clear(){
        super.clear();
        notifyDataSetChanged();
    }

    public abstract void notifyDataSetChanged();
    public abstract void notifyItemRemoved(int index);
    public abstract void notifyItemInserted(int index);
    public abstract void notifyItemChanged(int index);
}