package com.aware.plugin.template;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Locations;
import com.aware.utils.Aware_Plugin;

import java.security.Provider;

public class Plugin extends Aware_Plugin{
    Context context = this;
    public static final String PACKAGE_NAME = "com.aware.plugin.template";
    private static LocationManager locationManager = null;
    public static int UPDATE_TIME_GPS = 180;
    public static ContextProducer context_producer;
    private static final String ACTION_AWARE_PLUG_LOCATIONS = "ACTION_AWARE_PLUG_LOCATIONS";



    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::"+getResources().getString(R.string.app_name);
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        Log.i(TAG, "is it called? plugin.java");
        //Initialize our plugin's settings
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_TEMPLATE).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);
        }

        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS, true);


        if(Aware.getSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_LOCATION_GPS).length() > 0 ) {
            UPDATE_TIME_GPS = Integer.parseInt(Aware.getSetting(getApplicationContext(),Aware_Preferences.FREQUENCY_LOCATION_GPS));
        }


        //Activate programmatically any sensors/plugins you need here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER,true);
        //NOTE: if using plugin with dashboard, you can specify the sensors you'll use there.

        //Any active plugin/sensor shares its overall context using broadcasts
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                Log.i(TAG, "sending broadcast called");
                Intent intent = new Intent(Locations.ACTION_AWARE_LOCATIONS);
                sendBroadcast(intent);
                //Broadcast your context here
            }
        };

        context_producer = CONTEXT_PRODUCER;

        //Add permissions you need (Support for Android M) e.g.,
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //To sync data to the server, you'll need to set this variables from your ContentProvider
        //DATABASE_TABLES = Provider.DATABASE_TABLES;
        //TABLES_FIELDS = Provider.TABLES_FIELDS;
        //CONTEXT_URIS = new Uri[]{ Provider.Table_Data.CONTENT_URI }

        //Activate plugin
        Aware.startPlugin(this, PACKAGE_NAME);
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TAG = Aware.getSetting(getApplicationContext(),Aware_Preferences.DEBUG_TAG).length()>0?Aware.getSetting(getApplicationContext(),Aware_Preferences.DEBUG_TAG):"AWARE::Locations";
        //Check if the user has toggled the debug messages
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Deactivate any sensors/plugins you activated here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);

        //Stop plugin
        Aware.stopPlugin(this, PACKAGE_NAME);
    }

    public class SensorListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
//
                Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
                if (batteryIntent != null) {
                    Log.d(TAG, "i am recieved");
                }

            }
            catch (Exception ex) {
                Log.e("CurrentWidget", ex.getMessage());
                ex.printStackTrace();
            }

            try {
                context_producer.onContext();
                Log.d(TAG, "received");
            }catch (Exception e){
                Log.e(TAG,"Couldn't send broadcast");
                e.printStackTrace();
            }


        }
    }
}
