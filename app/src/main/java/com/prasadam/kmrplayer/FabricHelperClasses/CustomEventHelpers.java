package com.prasadam.kmrplayer.FabricHelperClasses;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDServer;

/*
 * Created by Prasadam Saiteja on 9/4/2016.
 */

public class CustomEventHelpers {

    public static void quickShareEventRegister(String fileName){

        String name = NSDServer.SERVICE_NAME;
        if(name == null)
            name = "null";

        Answers.getInstance().logCustom(new CustomEvent("Quick Share")
                .putCustomAttribute("Device Name", name)
                .putCustomAttribute("File Name", fileName));

    }

    public static void reportBug(CharSequence input) {

        Answers.getInstance().logCustom(new CustomEvent("Reported bugs")
                .putCustomAttribute("Bug", String.valueOf(input)));
    }
    public static void sendSuggestion(CharSequence input) {
        Answers.getInstance().logCustom(new CustomEvent("Suggestions")
                .putCustomAttribute("Suggestion", String.valueOf(input)));
    }

    public static void registerUser(String username) {

        Answers.getInstance().logCustom(new CustomEvent("Registered users")
                .putCustomAttribute("Username", username)
                .putCustomAttribute("Device Model", ExtensionMethods.getDeviceModelName()));
    }
    public static void addUserSession(String username) {

        Answers.getInstance().logCustom(new CustomEvent("user sessions")
                .putCustomAttribute("Username", username)
                .putCustomAttribute("Device Model", ExtensionMethods.getDeviceModelName()));
    }

    public static void addUserSong(String userName, Song currentSong) {
        if(currentSong != null)
            Answers.getInstance().logCustom(new CustomEvent("user current song")
                    .putCustomAttribute("Username", userName)
                    .putCustomAttribute("Current song", currentSong.getTitle()));
    }
}