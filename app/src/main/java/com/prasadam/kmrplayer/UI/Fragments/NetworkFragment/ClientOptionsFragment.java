package com.prasadam.kmrplayer.UI.Fragments.NetworkFragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.SubClasses.PreferenceFragment;
import com.prasadam.kmrplayer.UI.Fragments.DialogFragment.NearbyDevicesDetails_DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/24/2016.
 */

public class ClientOptionsFragment extends Fragment {

    private static final String KEY_SOCKET_CLIENT_SEND_WITHOUT_CONFIRMATION = "KEY_SOCKET_CLIENT_SEND_WITHOUT_CONFIRMATION";
    private static final String KEY_GROUP_LISTEN = "KEY_GROUP_LISTEN";
    private static NearbyDevicesDetails_DialogFragment nearbyDevicesDetails_dialogFragment;
    @BindView (R.id.generic_fragment_container) FrameLayout fragmentContainer;
    private final NSD serverObject;

    public ClientOptionsFragment(NSD serverObject, NearbyDevicesDetails_DialogFragment nearbyDevicesDetails_dialogFragment){
        ClientOptionsFragment.nearbyDevicesDetails_dialogFragment = nearbyDevicesDetails_dialogFragment;
        this.serverObject = serverObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_generic_recycler_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
    public void onResume() {
        super.onResume();
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getChildFragmentManager().beginTransaction().replace(fragmentContainer.getId(), new SettingsFragment(serverObject)).commit();
    }

    public static class SettingsFragment extends PreferenceFragment{

        private final NSD serverObject;

        public SettingsFragment(NSD serverObject) {
            super();
            this.serverObject = serverObject;
        }
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.client_options_preferences);
            initPreferences();
            preferenceListener();
        }

        private void initPreferences() {

            SwitchPreference transferWithoutConfirmation = (SwitchPreference) findPreference(KEY_SOCKET_CLIENT_SEND_WITHOUT_CONFIRMATION);
            if(serverObject.getMacAddress() == null)
                transferWithoutConfirmation.setEnabled(false);
            else{
                if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(getContext(), serverObject.getMacAddress()))
                    transferWithoutConfirmation.setChecked(true);
            }
        }
        private void preferenceListener() {

            (findPreference(KEY_SOCKET_CLIENT_SEND_WITHOUT_CONFIRMATION)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferenceHelper.setClientTransferRequestAlwaysAccept(getContext(), serverObject.getMacAddress(), !SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(getContext(), serverObject.getMacAddress()));
                    return false;
                }
            });

            (findPreference(KEY_GROUP_LISTEN)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SocketExtensionMethods.requestGroupListen(getContext(), serverObject);
                    nearbyDevicesDetails_dialogFragment.dismissAllowingStateLoss();
                    return false;
                }
            });

        }
    }
}
