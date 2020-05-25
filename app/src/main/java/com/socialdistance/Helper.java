package com.socialdistance;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;

public class Helper {

    Context mContext;
    LocationManager locationManager;
    NotificationUtils notificationUtils;

    public Helper(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isLocationServiceEnabled() {
        locationManager = (LocationManager) mContext.getSystemService( Context.LOCATION_SERVICE );
        boolean isEnabled = false;

        if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER) ) {
            isEnabled = true;
        }

        return isEnabled;
    }

}
