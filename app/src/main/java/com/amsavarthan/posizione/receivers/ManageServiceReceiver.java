package com.amsavarthan.posizione.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.amsavarthan.posizione.services.AccidentDetectionService;
import com.amsavarthan.posizione.ui.activities.MainActivity;
import com.amsavarthan.posizione.services.LocationService;
import com.amsavarthan.posizione.ui.activities.ParentLockActivity;
import com.amsavarthan.posizione.ui.activities.SettingsActivity;

import static android.content.Context.MODE_PRIVATE;

public class ManageServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.amsavarthan.posizione.START")) {

            boolean showNotification=context.getSharedPreferences("service", MODE_PRIVATE).getBoolean("notification",true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, LocationService.class).putExtra("notification", showNotification));
            } else {
                context.startService(new Intent(context, LocationService.class).putExtra("notification", showNotification));
            }

        }else if(intent.getAction().equals("com.amsavarthan.posizione.STOP")){

            String password=context.getSharedPreferences("lock",MODE_PRIVATE).getString("password","0");
            if(password.equals("0")) {

                try {
                    MainActivity.getInstance().location_switch.setChecked(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.getSharedPreferences("lock", MODE_PRIVATE).edit().putString("password", "0").apply();
                context.stopService(new Intent(context, LocationService.class));

            }else{

                Toast.makeText(context, "Disable parental lock to stop tracking service", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.setClass(context, ParentLockActivity.class);
                i.putExtra("fromNotification",true);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }

        }else if(intent.getAction().equals("com.amsavarthan.posizione.accident_detector.START")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, AccidentDetectionService.class));
            } else {
                context.startService(new Intent(context, AccidentDetectionService.class));
            }

        }else if(intent.getAction().equals("com.amsavarthan.posizione.accident_detector.STOP")){
            try{
                SettingsActivity.getInstance().aSwitch.setChecked(false);
            }catch (Exception e){
                e.printStackTrace();
            }
            context.stopService(new Intent(context, AccidentDetectionService.class));

        }
    }
}
