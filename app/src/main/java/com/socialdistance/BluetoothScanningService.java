package com.socialdistance;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BlendMode;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class BluetoothScanningService extends Service {

    private static final String TAG = BluetoothScanningService.class.getSimpleName();
    MyCustomBroadcastReceiver myCustomBroadcastReceiver;
    Helper helper;

    @Override
    public void onCreate() {
        super.onCreate();

        myCustomBroadcastReceiver = new MyCustomBroadcastReceiver();
        helper = new Helper(getApplicationContext());


        if (Build.VERSION.SDK_INT >= 26) {

            String CHANNEL_ID = "SOCIAL DISTANCE";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Social Distancing")
                    .setContentText("Stay Safe from COVID-19")
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Social Distance",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            Notification notification = builder.build();

            startForeground(1, notification);
        }
        else {
            startForeground(1, new Notification());
        }



        //Log.e(TAG, "Service Created" );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        //Log.e(TAG, "Service Started" );

        IntentFilter gpsFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        gpsFilter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        IntentFilter discoverStartedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        this.registerReceiver(myCustomBroadcastReceiver, gpsFilter);
        this.registerReceiver(myCustomBroadcastReceiver, discoverStartedFilter);
        this.registerReceiver(myCustomBroadcastReceiver, discoveryFinishedFilter);
        this.registerReceiver(myCustomBroadcastReceiver, foundFilter);

        //Toast.makeText(getApplicationContext(), "Discovery Started...", Toast.LENGTH_LONG).show();

        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        //Log.e(TAG, mAdapter.toString());
        mAdapter.cancelDiscovery();
        mAdapter.startDiscovery();


        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e("SERVICE", "DESTROYED");
        unregisterReceiver(myCustomBroadcastReceiver);
        Toast.makeText(getApplicationContext(), "Service Destroyed...", Toast.LENGTH_SHORT).show();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, MyCustomBroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);

    }


}
