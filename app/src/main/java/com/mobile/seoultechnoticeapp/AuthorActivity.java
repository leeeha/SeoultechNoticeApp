package com.mobile.seoultechnoticeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.DialogInterface;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AuthorActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        ArrayList<String> data = new ArrayList<>();
        ListView list = findViewById(R.id.author_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);

        EditText edtItem = findViewById(R.id.edtItem);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnDialog = findViewById(R.id.btnDialog);

        btnAdd.setOnClickListener(view -> {
            String text = edtItem.getText().toString();
            if(!text.isEmpty() && !data.contains(text)){
                data.add(text);
                adapter.notifyDataSetChanged();
                edtItem.setText("");
            }
            else{
                Toast.makeText(getApplicationContext(), "작성자를 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        btnDialog.setOnClickListener(view -> {
            AlertDialog.Builder dlg = new AlertDialog.Builder(AuthorActivity.this);
            dlg.setTitle("공지사항이 자주 올라오는 곳들입니다!");

            String[] authors = getResources().getStringArray(R.array.author_data);
            boolean[] checkArray = new boolean[authors.length];

            // 체크한 항목 중에서 기존 리스트에 존재하지 않는 것만 추가하기
            dlg.setMultiChoiceItems(authors, checkArray, (dialogInterface, i, b) -> {
                if (!data.contains(authors[i])) {
                    data.add(authors[i]);
                    adapter.notifyDataSetChanged();
                }
            });

            // 체크된 항목이 하나라도 있으면 토스트 메시지 띄우기
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    int cnt = 0;
                    for (int i = 0; i < authors.length; i++) {
                        if(checkArray[i]) cnt++; // 체크가 된 항목의 개수 세기
                    }

                    if(cnt > 0){
                        Toast.makeText(AuthorActivity.this, "추가 완료", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dlg.show();
        });

        // 추가한 항목을 롱클릭하면 삭제
        list.setOnItemLongClickListener((adapterView, view, i, l) -> {
            data.remove(i);
            adapter.notifyDataSetChanged();

            return false;
        });
    }
}
