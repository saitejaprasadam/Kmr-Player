package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.Fragments.NoItemsFragment;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

/*
 * Created by Prasadam Saiteja on 7/14/2016.
 */

public class ActivityHelper {

    public static void setDisplayHome(AppCompatActivity appCompatActivity){

        if(appCompatActivity.getSupportActionBar() != null ){
            appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_chevron_left_white_24dp);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static NoItemsFragment showEmptyFragment(Activity activtiy, String message){
        NoItemsFragment newFragment = new NoItemsFragment();
        FragmentTransaction ft = activtiy.getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, newFragment).addToBackStack(KeyConstants.EMPTY_FRAGMENT_TAG).commit();
        newFragment.setDescriptionTextView(message);
        return newFragment;
    }

    public static NoItemsFragment showEmptyFragment(Activity activtiy, String message, FrameLayout fragmentContainer){
        NoItemsFragment newFragment = new NoItemsFragment();
        FragmentTransaction ft = activtiy.getFragmentManager().beginTransaction();
        ft.add(fragmentContainer.getId(), newFragment).addToBackStack(KeyConstants.EMPTY_FRAGMENT_TAG).commit();
        newFragment.setDescriptionTextView(message);
        return newFragment;
    }
}