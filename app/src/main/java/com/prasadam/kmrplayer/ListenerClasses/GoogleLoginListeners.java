package com.prasadam.kmrplayer.ListenerClasses;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;

/*
 * Created by Prasadam Saiteja on 7/2/2016.
 */

public class GoogleLoginListeners {

    private static final int RC_SIGN_IN = 0;
    public static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions gso;
    private Activity mActivity;
    private static String TAG = "Google Login Listener";

    public GoogleLoginListeners(Activity mActivity) {
        this.mActivity = mActivity;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .enableAutoManage((FragmentActivity) mActivity, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
    }

    public void TerminateConnection() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void signInMethod() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Toast.makeText(mActivity, mActivity.getString(R.string.Logged_in_as_text) + acct.getDisplayName(), Toast.LENGTH_SHORT).show();
                MainActivity.navHeaderProfileName.setText(acct.getDisplayName());
                //MainActivity.navHeaderProfilePic.setImageURI(acct.getPhotoUrl());
            }

            //updateUI(true);
        } //else
        //updateUI(false);

    }
}
