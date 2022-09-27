package com.Zahid.android.AnDrOsAfE;

/**
 * @author jahid hasan
 * MainActivity class, launcher activity
 */

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Zahid.android.AnDrOsAfE.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MainActivity extends AppCompatActivity {

    private CheckBox adminEnabled;

    private DevicePolicyManager devicePolicyManager;

    private TextView statusTV,countTV,reset;

    private EditText emailET;

    private Button tickIV;




    private ComponentName compName;
    private boolean isAdminActive;

    @Override
    protected void onResume() {
        super.onResume();
        isAdminActive = devicePolicyManager.isAdminActive(compName);
        if(SecurityService.senderEmail!=null)
            emailET.setHint(SecurityService.senderEmail);
        if(isAdminActive){
            statusTV.setText("ON");
            statusTV.setTextColor(Color.GREEN);
        }
        else{
            statusTV.setText("OFF");
            statusTV.setTextColor(Color.RED);
        }
        countTV.setText(String.valueOf(SecurityService.failedPasswordCount));
        //First Set the CheckBox
        if(!isAdminActive && adminEnabled.isChecked()){
                adminEnabled.toggle();
        }
        else if(isAdminActive && !adminEnabled.isChecked()){
            adminEnabled.toggle();
        }
        else if(!isAdminActive)
            Toast.makeText(MainActivity.this,"Admin Privilege Needed",Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O) //Requires minimum Oreo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This will disable night mode for the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  //Inflate objects from activity_main.xml
        adminEnabled=findViewById(R.id.adminAccessCheckBox);
        emailET=findViewById(R.id.et_email);
        tickIV=findViewById(R.id.tv_tick);
        statusTV=findViewById(R.id.tv_status);
        countTV=findViewById(R.id.tv_count);
        reset=findViewById(R.id.tv_Reset);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this,loginWatch.class);




        /**
         * If security service is not running, then start it
         * Foreground service will keep on running, even if the app stops
         */
        ///


        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
             permissionToken.continuePermissionRequest();
            }
        }).check();

        ///





        if(!SecurityService.serviceRunning){
                    startForegroundService(new Intent(MainActivity.this,SecurityService.class));
                }


                tickIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SecurityService.senderEmail=emailET.getText().toString();
                        emailET.setHint(SecurityService.senderEmail);
                        Toast.makeText(MainActivity.this,"Email Set:"+SecurityService.senderEmail,Toast.LENGTH_SHORT).show();
                    }
                });


                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SecurityService.failedPasswordCount=0;
                        countTV.setText(String.valueOf(0));
                    }
                });

                adminEnabled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isAdminActive){
                            //ACTION_ADD_DEVICE_ADMIN: Prompt user to give admin privilege to this app
                            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            //EXTRA_DEVICE_ADMIN: The ComponentName identifier object of the admin component
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin Access Needed To Check If Incorrect PIN Entered");
                            startActivityForResult(intent,3 );  //The request code will be used while fetching result
                            isAdminActive = devicePolicyManager.isAdminActive(compName);   //Check if the admin is enabled
                        }
                        else
                        {
                            devicePolicyManager.removeActiveAdmin(compName);
                            isAdminActive = false;
                            statusTV.setText("OFF");
                            statusTV.setTextColor(Color.RED);
                            Toast.makeText(MainActivity.this,"Admin Privilege Revoked",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            switch (requestCode) {
                case 3:
                    if (resultCode == RESULT_OK)
                        Toast.makeText(MainActivity.this, "Admin Privilege Granted", Toast.LENGTH_SHORT);
                    else
                    Toast.makeText(MainActivity.this, "Failed to Enable Admin Privilege", Toast.LENGTH_SHORT).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}