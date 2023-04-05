package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText vidText = findViewById(R.id.editTextVid);
        EditText hostText = findViewById(R.id.editTextHost);

        TextView text = findViewById(R.id.message);
        Button btn = findViewById(R.id.button);
        String textName = checkServiceExecution()?"STOP":"START";
        btn.setText(textName);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn.getText().equals("START")) {
                    Log.d("MainActivity", "onClicked: Start");
                    if(vidText.getText().length() == 0 || hostText.getText().length() == 0){
                        // Error Toast
                        Toast toast = Toast.makeText(getApplication(), "Please input your Host/VID.", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        btn.setText("STOP");
                        Intent intent = new Intent(getApplication(), WatchService.class);
                        intent.putExtra("Host", hostText.getText().toString());
                        intent.putExtra("Vid", vidText.getText().toString());
                        startService(intent);
                    }
                } else {
                    Log.d("MainActivity", "onClicked: Stop");
                    btn.setText("START");
                    Intent intent = new Intent(getApplication(), WatchService.class);
                    stopService(intent);
                }
            }
        });
        if (!checkReadStatsPermission()) {
            text.setText("Check your permission. Please allow permission of USAGE_ACCESS_SETTINGS.");
            btn.setEnabled(false);
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        } else {
            text.setText("Ready to watch Usage Apps");
            btn.setEnabled(true);
        }
    }

    private boolean checkReadStatsPermission() {
        AppOpsManager aom = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = aom.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            return checkPermission("android.permission.PACKAGE_USAGE_STATS", android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private boolean checkServiceExecution() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)){
            if(WatchService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}