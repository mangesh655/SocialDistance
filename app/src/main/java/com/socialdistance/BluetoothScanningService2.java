package com.socialdistance;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BluetoothScanningService2 extends IntentService {

    MyCustomBroadcastReceiver myCustomBroadcastReceiver;
    private static final String TAG = BluetoothScanningService2.class.getSimpleName();

    public BluetoothScanningService2() {
        super("BluetoothScanningService2");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Log.e(TAG, "Service Started" );

        IntentFilter gpsFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        gpsFilter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        IntentFilter discoverStartedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(myCustomBroadcastReceiver, gpsFilter);
        this.registerReceiver(myCustomBroadcastReceiver, discoverStartedFilter);
        this.registerReceiver(myCustomBroadcastReceiver, discoveryFinishedFilter);

        //Toast.makeText(getApplicationContext(), "Discovery Started...", Toast.LENGTH_LONG).show();

        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        //Log.e(TAG, mAdapter.toString());
        mAdapter.cancelDiscovery();
        mAdapter.startDiscovery();

    }
}
