package com.socialdistance;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;


public class MyCustomBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MyCustomBroadcastReceiver.class.getSimpleName();
    NotificationUtils notificationUtils;
    Helper helper;
    AlarmHandler alarmHandler;

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();


        notificationUtils = new NotificationUtils(context);
        helper = new Helper(context);
        alarmHandler = new AlarmHandler(context);

        //Toast.makeText(context, "Context 556 : " + context.toString(), Toast.LENGTH_SHORT).show();
        //Log.e(TAG, "Context 556 : " + context.toString());
        //Toast.makeText(context, "Intent : " + intent.getPackage(), Toast.LENGTH_SHORT).show();

        if (intent.getPackage() != null) {
            //Log.e(TAG, "Intent : " + intent.getPackage());
        }
        else {
            //Log.e(TAG, "Intent : NULL");
        }


        if (action.equals(BluetoothDevice.ACTION_FOUND)) {

            //Log.e(TAG, "FOUND RECEIVER");
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
            //intent.getShortExtra(BluetoothDevice.)
            //Log.e(TAG, device.getName());
            //Log.e(TAG, device.getAddress());
            //Log.e(TAG, String.valueOf(rssi));

            vibrateAndRing(context);

        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            //Log.e("SERVICE", "DISCOVERY STARTED : 222");
            Toast.makeText(context, "Discovery Started", Toast.LENGTH_SHORT).show();

        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            //Log.e("SERVICE", "DISCOVERY FINISHED");
            Toast.makeText(context, "Discovery Finished", Toast.LENGTH_SHORT).show();
            //BluetoothAdapter.getDefaultAdapter().startDiscovery();
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            //Log.e(TAG, "STATE CHANGED");

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    //Log.e("MainActivity", "OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    //Log.e("MainActivity", "Turning OFF");
                    notificationUtils.createNotification(Config.bluetoothDisabledMsg);
                    break;
                case BluetoothAdapter.STATE_ON:
                    //Log.e("MainActivity", "ON");
                    alarmHandler.cancelAlarmManager();
                    alarmHandler.setAlarmManager();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    //Log.e("MainActivity", "Turning ON");
                    if(helper.isLocationServiceEnabled()) {
                        notificationUtils.createNotification(Config.bluetoothEnabledMsg);
                        //Log.e("BROADCAST", "GPS ON");
                    }else {
                        notificationUtils.createNotification(Config.locationDisabledMsg);
                        //Log.e("BROADCAST", "GPS OFF");
                    }
                    break;
            }
        }
        else if (action.matches(LocationManager.PROVIDERS_CHANGED_ACTION) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            if(helper.isLocationServiceEnabled()) {

                if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    notificationUtils.createNotification(Config.bluetoothEnabledMsg);
                }
                else {
                    notificationUtils.createNotification(Config.bluetoothDisabledMsg);
                }


                //Log.e("BROADCAST", "GPS ON");
            }else {
                notificationUtils.createNotification(Config.locationDisabledMsg);
                //Log.e("BROADCAST", "GPS OFF");
            }
        }
        else if(action.equals("alarmrecieved")) {
           //BluetoothAdapter.getDefaultAdapter().enable();
            //Log.e(TAG, "Alarm Recieved.");
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    /** This is Vibrate Method
     *
     * @param mContext
     */
    public void vibrateAndRing(Context mContext) {

        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        long[] mVibratePattern = new long[]{0, 400, 800, 600, 800, 800, 800, 1000};
        int[] mAmplitudes = new int[]{0, 255, 0, 255, 0, 255, 0, 255};


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, -1));
        } else {
            //deprecated in API 26
            long[] pattern = {0, 200, 0}; //0 to start now, 200 to vibrate 200 ms, 0 to sleep for 0 ms.
            v.vibrate(pattern, -1); // 0 to repeat endlessly.
        }
    }


}
