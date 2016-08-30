package com.prasadam.kmrplayer.SubClasses.CustomArrayList;

import com.prasadam.kmrplayer.AudioPackages.modelClasses.Album;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Created by Prasadam Saiteja on 8/29/2016.
 */

public abstract class AlbumArrayList extends ArrayList<Album> {

    public AlbumArrayList(){}
    public AlbumArrayList(ArrayList<Album> album){
        super.addAll(album);
        onArrayListChanged();
    }

    public boolean add(Album album) {
        boolean result = super.add(album);
        onItemAddedListener(this.size() - 1);
        return result;
    }
    public void add(int index, Album album){
        super.add(index, album);
        onItemAddedListener(index);
    }
    public boolean addAll(Collection<? extends Album> album) {
        boolean result = super.addAll(album);
        onArrayListChanged();
        return result;
    }
    public boolean addAll(int index, Collection<? extends Album> album) {
        boolean result = super.addAll(index, album);
        onArrayListChanged();
        return result;
    }

    public Album set(int index, Album album){
        Album result = super.set(index, album);
        onItemAddedListener(index);
        return result;
    }

    public Album remove(int index){
        Album result =  super.remove(index);
        onItemRemovedListener(index);
        return result;
    }
    public boolean remove(Album album){
        int index = super.indexOf(album);
        boolean result = super.remove(album);
        onItemRemovedListener(index);
        return result;
    }
    public boolean removeAll(Collection<?> album) {
        boolean result = super.removeAll(album);
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
