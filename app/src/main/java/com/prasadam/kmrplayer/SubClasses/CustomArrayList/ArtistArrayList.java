package com.prasadam.kmrplayer.SubClasses.CustomArrayList;

import com.prasadam.kmrplayer.AudioPackages.modelClasses.Artist;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Created by Prasadam Saiteja on 8/29/2016.
 */

public abstract class ArtistArrayList extends ArrayList<Artist> {

    public ArtistArrayList(){}
    public ArtistArrayList(ArrayList<Artist> artist){
        super.addAll(artist);
        onArrayListChanged();
    }

    public boolean add(Artist artist) {
        boolean result = super.add(artist);
        onItemAddedListener(this.size() - 1);
        return result;
    }
    public void add(int index, Artist artist){
        super.add(index, artist);
        onItemAddedListener(index);
    }
    public boolean addAll(Collection<? extends Artist> artist) {
        boolean result = super.addAll(artist);
        onArrayListChanged();
        return result;
    }
    public boolean addAll(int index, Collection<? extends Artist> artist) {
        boolean result = super.addAll(index, artist);
        onArrayListChanged();
        return result;
    }

    public Artist set(int index, Artist artist){
        Artist result = super.set(index, artist);
        onItemAddedListener(index);
        return result;
    }

    public Artist remove(int index){
        Artist result =  super.remove(index);
        onItemRemovedListener(index);
        return result;
    }
    public boolean remove(Artist artist){
        int index = super.indexOf(artist);
        boolean result = super.remove(artist);
        onItemRemovedListener(index);
        return result;
    }
    public boolean removeAll(Collection<?> artist) {
        boolean result = super.removeAll(artist);
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
