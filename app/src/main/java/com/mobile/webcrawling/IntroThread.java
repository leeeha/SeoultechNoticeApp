package com.mobile.webcrawling;

import android.os.Handler;
import android.os.Message;

public class IntroThread extends Thread {
    private Handler handler;

    public IntroThread(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        Message msg = new Message();
        try {
            // sleep 메소드가 종료되면 핸들러를 통해
            // 메인 스레드에 메시지 전달
            Thread.sleep(1500);
            msg.what = 1;
            handler.sendEmptyMessage(msg.what);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
