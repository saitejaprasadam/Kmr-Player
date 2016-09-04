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
import de.umass.lastfm.cache.MemoryCache;

/*
 * Created by Prasadam Saiteja on 7/22/2016.
 */

public class LastFm {

    private static final String LAST_FM_API_KEY = "251ca27bf77054e57f679434c3873727";
    private static final String LAST_FM_SHARED_SECRET_KEY = "c9e9d748c2c156b814eee7675b0a4862";
    private static final String LAST_FM_USER_NAME = "saitejaprasadam";
    private static final String LAST_FM_PASSWORD = "saiteja@62627248";
    public static Session LastFMSession;

    public static void initializeLastFm(){
        SocketExtensionMethods.requestStrictModePermit();

        Caller lastFmCaller = Caller.getInstance();
        lastFmCaller.setUserAgent(System.getProperties().getProperty("http.agent"));
        lastFmCaller.setDebugMode(true);
        lastFmCaller.setCache(new MemoryCache());
        LastFMSession = Authenticator.getMobileSession(LAST_FM_USER_NAME, LAST_FM_PASSWORD, LAST_FM_API_KEY, LAST_FM_SHARED_SECRET_KEY);
    }

    public static ArrayList<LastFmImage> getLastFmImages(String artistName){

        PaginatedResult<Image> temp = Artist.getImages(artistName, LAST_FM_API_KEY);
        Log.d("result count ", String.valueOf(temp.getTotalPages()));

        return null;
    }
}
