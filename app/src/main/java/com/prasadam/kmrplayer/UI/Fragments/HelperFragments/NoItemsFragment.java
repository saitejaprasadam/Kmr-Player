package com.prasadam.kmrplayer.UI.Fragments.HelperFragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prasadam.kmrplayer.R;

/*
 * Created by Prasadam Saiteja on 9/24/2016.
 */

public class NoItemsFragment extends Fragment{

    private TextView DescriptionTextView;
    private String description;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_no_items_layout, container, false);
        DescriptionTextView = (TextView) rootView.findViewById(R.id.description_text_view);
        DescriptionTextView.setText(description);
        return rootView;
    }

    public void setDescriptionTextView(String description){
        this.description = description;
        if(DescriptionTextView != null)
            DescriptionTextView.setText(description);
    }
}
