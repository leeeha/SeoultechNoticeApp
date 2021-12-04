package com.mobile.webcrawling;

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
            String query = edtItem.getText().toString();
            if(!query.isEmpty()){
                data.add(query);
                adapter.notifyDataSetChanged();
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

            dlg.setMultiChoiceItems(authors, checkArray, (dialogInterface, i, b) -> {
                // 체크박스한 항목들 모두 리스트뷰에 추가하기
                data.add(authors[i]);
                adapter.notifyDataSetChanged();
            });

            dlg.setPositiveButton("추가", (dialogInterface, i) -> {
                Toast.makeText(AuthorActivity.this, "추가 완료",
                        Toast.LENGTH_SHORT).show();
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
