package com.mobile.seoultechnoticeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

public class IntroActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        IntroThread introThread = new IntroThread(handler);
        introThread.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };
}
