package com.mobile.webcrawling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    TextView notice1, notice2, notice3;
    TextView alarm1, alarm2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notice1 = findViewById(R.id.notice1);
        notice2 = findViewById(R.id.notice2);
        notice3 = findViewById(R.id.notice3);
        alarm1 = findViewById(R.id.alarm1);
        alarm2 = findViewById(R.id.alarm2);

        // 대학 공지사항
        notice1.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
            intent.putExtra("topBarText", notice1.getText());
            startActivity(intent);
        });

        // 학사공지
        notice2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
            intent.putExtra("topBarText", notice2.getText());
            startActivity(intent);
        });

        // 장학공지
        notice3.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
            intent.putExtra("topBarText", notice3.getText());
            startActivity(intent);
        });

        // 키워드 추가
        alarm1.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), KeywordActivity.class);
            startActivity(intent);
        });

        // 작성자 추가
        alarm2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AuthorActivity.class);
            startActivity(intent);
        });
    }
}