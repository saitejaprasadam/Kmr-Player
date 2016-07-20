package com.prasadam.kmrplayer.socketClasses.NetworkServiceDiscovery;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NSDServer {

    private static Context context;
    private static String SERVICE_NAME = ExtensionMethods.deviceName();
    private static String SERVICE_TYPE = "_kmr._tcp.";
    public static NsdManager mNsdManager;
    private static NsdServiceInfo serviceInfo;

    public static void startService(Context thiscontext) {
        context = thiscontext;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        registerService(1231);
    }
    public static void registerService(int port) {

        serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(ExtensionMethods.isTablet(context))
                serviceInfo.setAttribute(KeyConstants.DEVICE_TYPE, KeyConstants.TABLET);
            else
                serviceInfo.setAttribute(KeyConstants.DEVICE_TYPE, KeyConstants.MOBILE);
        }
        serviceInfo.setPort(port);

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public static NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {

        @Override
        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            String mServiceName = NsdServiceInfo.getServiceName();
            SERVICE_NAME = mServiceName;
            Log.d("NsdserviceOnRegister", "Registered name : " + mServiceName);
        }

        @Override
        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
            // NsdManager.unregisterService() called and passed in this listener.
            Log.d("NsdserviceOnUnregister", "Service Unregistered : " + serviceInfo.getServiceName());
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.d("NsdOnUnregisterFailed", "Service unregistration failed" );
        }
    };
}
