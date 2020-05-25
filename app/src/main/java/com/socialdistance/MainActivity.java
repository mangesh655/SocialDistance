package com.socialdistance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.altbeacon.beacon.BeaconConsumer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtDistance;
    private Button btnScan;
    private BluetoothAdapter bluetoothAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    private boolean isBluetoothEnabled = false;
    NotificationUtils notificationUtils;
    Helper helper;
    AlarmHandler alarmHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationUtils = new NotificationUtils(getApplicationContext());
        helper = new Helper(getApplicationContext());
        alarmHandler = new AlarmHandler(MainActivity.this);

        edtDistance = findViewById(R.id.edt_distance);
        btnScan = findViewById(R.id.btn_scan);

        alarmHandler.cancelAlarmManager();
        alarmHandler.setAlarmManager();


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showNoBluetoothDialog();
        }
        else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestLocationPermission();
            }
            else {
                startBluetoothService();
            }

        }

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bluetoothAdapter == null) {
                    showNoBluetoothDialog();
                }
                else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestLocationPermission();
                    }
                    else {
                        startBluetoothService();
                    }
                }
            }
        });
    }

    private void startBluetoothService() {

        isBluetoothEnabled = bluetoothAdapter.isEnabled();
        if (!isBluetoothEnabled) {
            notificationUtils.createNotification(Config.bluetoothEnabledMsg);
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH);
            bluetoothAdapter.enable();

        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

               if (!helper.isLocationServiceEnabled()) {
                   //notificationUtils.createNotification(Config.locationDisabledMsg);
                   buildAlertMessageNoGps();
               }
               else {
                   //notificationUtils.createNotification(Config.bluetoothEnabledMsg);
                   Intent i = new Intent(MainActivity.this, BluetoothScanningService.class);
                   startService(i);

                   /*int distance = Integer.parseInt(edtDistance.getText().toString());
                   SharedPreferences.Editor editor = sharedPref.edit();
                   editor.putInt("Distance", distance);
                   editor.commit();*/



               }

            }
        }

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isMyServiceRunning(BluetoothScanningService.class)) {
                startService(intent);
            }
        } else {
            if (!isMyServiceRunning(BluetoothScanningService.class)) {
                startService(intent);
            }
        }*/

    }

    /*private boolean isMyServiceRunning(Class<BluetoothScanningService> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;

    }*/

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        notificationUtils.createNotification(Config.bluetoothEnabledMsg);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        notificationUtils.createNotification(Config.locationDisabledMsg);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                // do you work now
                                startBluetoothService();
                            }

                            showSettingsDialog();

                            /*// check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings

                            }*/
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("Android 6.0  or above requires you to open location service to find nearby devices." +
                "You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public void showNoBluetoothDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Not compatible")
                .setMessage("Your phone does not support Bluetooth")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        //Log.e("MainActivity", "Resumed");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mReceiver);
        //stopService(intent);

    }


}
