package com.github.leondevlifelog.gesturelockviewsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.leondevlifelog.gesturelockview.GestureLockView;

/**
 * @author liang
 * 日期 2017-11-02 14:04:52
 */

public class MainActivity extends AppCompatActivity {

    private GestureLockView customGestureView;
    private GestureLockView defaultGestureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customGestureView = findViewById(R.id.customGestureLockView);
        defaultGestureView = findViewById(R.id.defaultGestureLockView);
        GestureLockView.OnCheckPasswordListener onCheckPasswordListener = new GestureLockView.OnCheckPasswordListener() {
            @Override
            public boolean onCheckPassword(String passwd) {
                return passwd.equals("abcdef");
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        };
        customGestureView.setOnCheckPasswordListener(onCheckPasswordListener);
        defaultGestureView.setOnCheckPasswordListener(onCheckPasswordListener);
        Switch safeModel = findViewById(R.id.switch1);
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customGestureView.setSecurityMode(isChecked);
                defaultGestureView.setSecurityMode(isChecked);
            }
        };
        safeModel.setOnCheckedChangeListener(listener);
    }
}
