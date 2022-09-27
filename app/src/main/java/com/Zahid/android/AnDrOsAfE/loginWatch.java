package com.Zahid.android.AnDrOsAfE;

/**
 * @author jahid hasan
 * loginWatch class, handle enabling and disabling of security Policies
 * by the user
 */
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class loginWatch extends DeviceAdminReceiver {

    /**
     * This method is called once device admin is enabled
     */
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context,"Device Admin Enabled",Toast.LENGTH_SHORT).show();
        Log.i("loginWatch","Device Admin Enabled");
    }

    /**
     * This method is called once device admin is disabled
     */
    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context,"Device Admin Disabled",Toast.LENGTH_SHORT).show();
        Log.i("loginWatch","Device Admin Disabled");
    }

    /***
     * This method will be called everytime an incorrect PIN is entered
     */
    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordFailed(context, intent);
        SecurityService.failedPasswordCount++;
        /**
         * If the incorrect password count is odd
         * Then create a new thread, take a picture, convert picture to Base64
         * and send it
         */
        if(SecurityService.failedPasswordCount%2!=0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CameraHandler camString=new CameraHandler();
                    String encoded64=camString.takePic();
                    new MailSend(encoded64);
                    Log.d("loginWatch-passFailed", String.valueOf(SecurityService.failedPasswordCount));
                    Log.d("loginWatch-passFailed",encoded64);
                }
            }).start();
        }
    }
}
