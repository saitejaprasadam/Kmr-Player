package com.prasadam.kmrplayer.LastFm;
import android.util.Log;

import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.util.ArrayList;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Image;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Session;

/*
 * Created by Prasadam Saiteja on 7/22/2016.
 */

public class LastFm {

    private static final String LAST_FM_API_KEY = "80398fb118bcdc777efb211aeaf4e299";
    private static final String LAST_FM_SHARED_SECRET_KEY = "9372bd0bf1b2a338be3d35c918732563";
    private static final String LAST_FM_USER_NAME = "saitejaprasadam";
    private static final String LAST_FM_PASSWORD = "saiteja@62627248";
    public static Session LastFMSession;

    public static void initializeLastFm(){
        SocketExtensionMethods.requestStrictModePermit();
        Caller.getInstance().setUserAgent("tst");
        Caller.getInstance().setDebugMode(true);
        LastFMSession = Authenticator.getMobileSession(LAST_FM_USER_NAME, LAST_FM_PASSWORD, LAST_FM_API_KEY, LAST_FM_SHARED_SECRET_KEY);
    }

    public static ArrayList<LastFmImage> getLastFmImages(String artistName){

        PaginatedResult<Image> temp = Artist.getImages(artistName, LAST_FM_API_KEY);
        Log.d("result count ", String.valueOf(temp.getTotalPages()));

        return null;
    }
}
