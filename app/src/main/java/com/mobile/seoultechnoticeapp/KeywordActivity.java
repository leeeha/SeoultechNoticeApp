package com.mobile.seoultechnoticeapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class KeywordActivity extends Activity {
    ArrayList<String> data;
    ListView list;
    ArrayAdapter<String> adapter;
    EditText edtItem;
    Button btnAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);

        data = new ArrayList<>();
        list = findViewById(R.id.keyword_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);

        edtItem = findViewById(R.id.edtItem);
        btnAdd = findViewById(R.id.btnAdd);

        // 버튼을 클릭하면, 키워드 추가
        btnAdd.setOnClickListener(view -> {
            String query = edtItem.getText().toString();

            if(!query.isEmpty()){
                data.add(query);
                adapter.notifyDataSetChanged();

                edtItem.setText("");
            }
            else{
                Toast.makeText(this, "키워드를 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        // 추가한 항목을 롱클릭하면 삭제
        list.setOnItemLongClickListener((adapterView, view, i, l) -> {
            data.remove(i);
            adapter.notifyDataSetChanged();
            return false;
        });
    }
}
