package com.prasadam.kmrplayer.socketClasses.NetworkServiceDiscovery;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.prasadam.kmrplayer.NearbyDevicesActivity;
import com.prasadam.kmrplayer.QuickShareActivity;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NSDClient {

    private static String SERVICE_NAME = ExtensionMethods.deviceName();
    private static String SERVICE_TYPE = "_kmr._tcp.";
    public static NsdManager mNsdManager;
    public static ArrayList<NSD> devicesList = new ArrayList<>();

    public static void startSearch(Context context){
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }
    public static NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {

        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d("nsdservice", "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            // A service was found! Do something with it.
            Log.d("nsdservice", "Service discovery success : " + service);
            Log.d("nsdservice", "Host = "+ service.getServiceName());
            Log.d("nsdservice", "port = " + String.valueOf(service.getPort()));

            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                // Service type is the string containing the protocol and
                // transport layer for this service.
                Log.d("nsdservice", "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(SERVICE_NAME)) {
                //Name of the service
                Log.d("nsdservice", "Same machine: " + SERVICE_NAME);
            } else {
                Log.d("nsdservice", "Diff Machine : " + service.getServiceName());
                // connect to the service and obtain serviceInfo
                mNsdManager.resolveService(service, new mResolveListener());
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            try{
                Log.d("nsdserviceLost", "service lost" + service.getHost().toString());
                for (NSD device : devicesList) {
                    Log.d("devices", device.GetClientNSD().getHost().toString());
                    if(device.GetClientNSD().getHost().toString().equals(service.getHost().toString())){
                        Log.d("Removed", "from list");
                        devicesList.remove(device);
                        NearbyDevicesActivity.updateAdapater();
                        QuickShareActivity.updateAdapater();
                    }
                }
            }

            catch (Exception ignored){}
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i("nsdserviceDstopped", "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e("nsdServiceSrartDfailed", "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e("nsdserviceStopDFailed", "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }
    };
    public static class mResolveListener implements NsdManager.ResolveListener{

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e("nsdservicetag", "Resolve failed " + errorCode);
            Log.e("nsdservicetag", "serivce = " + serviceInfo);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {

            Log.d("nsdservicetag", "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                Log.d("nsdservicetag", "Same IP.");
                return;
            }

            boolean found = false;
            for (NSD device : devicesList) {
                if(device.GetClientNSD().getHost().toString().equals(serviceInfo.getHost().toString()))
                    found = true;
            }

            if(!found){
                devicesList.add(new NSD(serviceInfo));
                NearbyDevicesActivity.updateAdapater();
                QuickShareActivity.updateAdapater();
            }

        }
    }
}