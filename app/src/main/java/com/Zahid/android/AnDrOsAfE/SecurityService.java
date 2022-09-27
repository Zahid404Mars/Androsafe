package com.Zahid.android.AnDrOsAfE;
/**
 * @author jahid hasan
 * SecurityService class to start a foreground Service
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.Zahid.android.AnDrOsAfE.R;

public class SecurityService extends Service {
    static boolean serviceRunning;
    static int failedPasswordCount;
    static String deviceDetails;
    static String senderEmail;

    static{
        serviceRunning=false;
        failedPasswordCount=0;
        deviceDetails=Build.MODEL+" "+Build.MANUFACTURER;  //Device details to be send along with mail
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceRunning=true;
        initiateNotificationChannel();
        //pendingIntent shall open app after a click on notification
        Intent i1=new Intent(SecurityService.this,MainActivity.class);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,i1,0);

        Notification notification=new NotificationCompat.Builder(this,"SecurityApp").setContentTitle("AnDrOsAfE")
                .setContentText("Theft-Alert Running").setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).build();
        //Primary Code Begins here
        new loginWatch();
        //Primary Code Ends here
        startForeground(1,notification);
        return START_STICKY;
    }


    private void initiateNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel("SecurityApp","SChannel1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }


    @Override
    public void onDestroy() {
        serviceRunning=false;
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }
}
