package com.prasadam.kmrplayer.SubClasses.CustomArrayList;

import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Created by Prasadam Saiteja on 8/29/2016.
 */

public abstract class SongsArrayList extends ArrayList<Song> {

    public SongsArrayList(){}
    public SongsArrayList(ArrayList<Song> song){
        super.addAll(song);
        onArrayListChanged();
    }

    public boolean add(Song song) {
        boolean result = super.add(song);
        onItemAddedListener(this.size() - 1);
        return result;
    }
    public void add(int index, Song song){
        super.add(index, song);
        onItemAddedListener(index);
    }
    public boolean addAll(Collection<? extends Song> song) {
        boolean result = super.addAll(song);
        onArrayListChanged();
        return result;
    }
    public boolean addAll(int index, Collection<? extends Song> song) {
        boolean result = super.addAll(index, song);
        onArrayListChanged();
        return result;
    }

    public Song set(int index, Song song){
        Song result = super.set(index, song);
        onItemAddedListener(index);
        return result;
    }

    public Song remove(int index){
        Song result =  super.remove(index);
        onItemRemovedListener(index);
        return result;
    }
    public boolean remove(Song song){
        int index = super.indexOf(song);
        boolean result = super.remove(song);
        onItemRemovedListener(index);
        return result;
    }
    public boolean removeAll(Collection<?> song) {
        boolean result = super.removeAll(song);
        onArrayListChanged();
        return result;
    }
    public void removeRange(int fromIndex, int toIndex){
        super.removeRange(fromIndex, toIndex);
        onArrayListChanged();
    }

    public void clear(){
        super.clear();
        onArrayListChanged();
    }

    public abstract void onArrayListChanged();
    public abstract void onItemRemovedListener(int index);
    public abstract void onItemAddedListener(int index);
}
