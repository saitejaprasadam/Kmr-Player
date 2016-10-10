package com.prasadam.kmrplayer.LastFmHelperClasses;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Playlist;
import de.umass.lastfm.Session;

/*
 * Created by Prasadam Saiteja on 10/6/2016.
 */

public class LastFm {

    private static final String key = "a8c623209fc2f3d64243bcf9e5304f0c";// api key
    private static final String secret = "c90482b4cb55b3c1f85e0936e1e3d739";   // api secret
    private static final String user = "saitejaprasadam";     // user name
    private static final String password = "saiteja@62627248"; // user's password

    public static void initLastFm(){

        Caller.getInstance().setUserAgent("tst");
        Caller.getInstance().setDebugMode(true);

        Session session = Authenticator.getMobileSession(user, password, key, secret);
        Playlist playlist = Playlist.create("example playlist", "description", session);
    }
}
