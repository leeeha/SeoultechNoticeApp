package com.mobile.seoultechnoticeapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class NoticeActivity extends Activity {
    TextView topBarText;
    final int pageNum = 3;
    RecyclerView recyclerView;
    ArrayList<ItemObject> totalList;
    RecyclerAdapter recyclerAdapter;
    EditText edtQuery;
    Button btnSort, btnGoMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        topBarText = findViewById(R.id.topBarText);
        Intent topBarIntent = getIntent();
        String noticeTitle = topBarIntent.getStringExtra("topBarText");
        topBarText.setText(noticeTitle);

        edtQuery = findViewById(R.id.edtQuery);
        btnSort = findViewById(R.id.btnSort);
        btnGoMain = findViewById(R.id.btnGoMain);

        recyclerView = findViewById(R.id.recyclerview_main_list);

        // 크롤링 데이터를 저장할 리스트 생성
        totalList = new ArrayList<>();

        // equals여야 문자열의 내용을 비교 (등호는 객체의 메모리 주소가 같은지 비교)
        if(noticeTitle.equals("대학 공지사항")){
            initRecyclerView("https://www.seoultech.ac.kr/service/info/notice/?bidx=4691&bnum=4691&allboard=false&page=",
                    "&size=14&searchtype=1&searchtext=");
        }
        else if(noticeTitle.equals("학사공지")){
            initRecyclerView("https://www.seoultech.ac.kr/service/info/matters/?bidx=6112&bnum=6112&allboard=true&page=",
                    "&size=16&searchtype=1&searchtext=");
        }
        else { // 장학공지
            initRecyclerView("https://www.seoultech.ac.kr/service/info/janghak/?bidx=5233&bnum=5233&allboard=true&page=",
                    "&size=14&searchtype=1&searchtext=");
        }

        // 리사이클러뷰의 레이아웃 매니저 지정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 리사이클러뷰의 어댑터 지정
        recyclerAdapter = new RecyclerAdapter(totalList);
        recyclerView.setAdapter(recyclerAdapter);

        // 검색어를 URL 뒤에 붙여서 그 결과를 크롤링으로 다시 가져오기
        edtQuery.setOnKeyListener((view, keyCode, event) -> {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                if(noticeTitle.equals("대학 공지사항")){
                    searchQuery("https://www.seoultech.ac.kr/service/info/notice/?bidx=4691&bnum=4691&allboard=false&page=",
                            "&size=14&searchtype=1&searchtext=");

                }
                else if(noticeTitle.equals("학사공지")){
                    searchQuery("https://www.seoultech.ac.kr/service/info/matters/?bidx=6112&bnum=6112&allboard=true&page=",
                            "&size=16&searchtype=1&searchtext=");
                }
                else { // 장학공지
                    searchQuery("https://www.seoultech.ac.kr/service/info/janghak/?bidx=5233&bnum=5233&allboard=true&page=",
                            "&size=14&searchtype=1&searchtext=");
                }
            }
            return false;
        });

        // 조회수 기준으로 내림차순 정렬해서 보여주기
        btnSort.setOnClickListener(view -> {
            // 원본 리스트의 모든 아이템을 newList에 복사 후 정렬하기
            ArrayList<ItemObject> newList = new ArrayList<>(totalList);
            Collections.sort(newList);

            totalList.clear();
            totalList.addAll(newList);
            recyclerAdapter.notifyDataSetChanged();

            Toast.makeText(NoticeActivity.this, "조회수 높은 순으로 정렬", Toast.LENGTH_SHORT).show();
        });

        // 메인 홈페이지로 가는 버튼
        btnGoMain.setOnClickListener(view -> {
            if(topBarText.getText().equals("대학 공지사항")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.seoultech.ac.kr/service/info/notice/"));
                startActivity(intent);
                Toast.makeText(NoticeActivity.this, "대학 공지사항으로 이동", Toast.LENGTH_SHORT).show();
            }
            else if(topBarText.getText().equals("학사공지")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.seoultech.ac.kr/service/info/matters/"));
                startActivity(intent);
                Toast.makeText(NoticeActivity.this, "학사공지로 이동", Toast.LENGTH_SHORT).show();
            }
            else{ // 장학공지
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.seoultech.ac.kr/service/info/janghak/"));
                startActivity(intent);
                Toast.makeText(NoticeActivity.this, "장학공지로 이동", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initRecyclerView(String start, String end) {
        String[] initURL = new String[pageNum];

        // 3개의 페이지에 있는 모든 리스트 합치기
        for (int i = 0; i < pageNum; i++) {
            initURL[i] = start + (i + 1) + end;

            // get()에서 반환되는 여러 개의 리스트를 하나로 합치기
            WebCrawlingTask task = new WebCrawlingTask(this);
            try {
                totalList.addAll(task.execute(initURL[i]).get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 3개의 페이지에서 중복되는 공지사항은 제외
        for (int i = 0; i < totalList.size(); i++) {
            if(i >= 20 && totalList.get(i).index.equals("공지")){
                totalList.remove(i);
                i--;
                // 리스트 크기가 자동으로 1씩 줄어들기 때문에
                // 바로 다음 항목도 검사하려면 위치가 그대로여야 한다. 그래서 -- ++
            }
        }
    }

    public void searchQuery(String start, String end) {
        String[] searchURL = new String[pageNum];
        String query = edtQuery.getText().toString();

        if (!query.isEmpty()) {
            totalList.clear();

            // 검색 결과로 나온 3개의 리스트 합치기
            for (int i = 0; i < pageNum; i++) {
                searchURL[i] = start + (i + 1) + end + query;
                WebCrawlingTask task = new WebCrawlingTask(this);
                try {
                    totalList.addAll(task.execute(searchURL[i]).get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 3개의 페이지에서 중복되는 공지사항은 제외
            for (int i = 0; i < totalList.size(); i++) {
                if(i >= 20 && totalList.get(i).index.equals("공지")){
                    totalList.remove(i);
                    i--;
                }
            }

            // 상단의 공지사항 4~6개 중에서 검색어가 포함되지 않은 것은 제거
            for (int i = 0; i < 6; i++) {
                if (!totalList.get(i).title.contains(query)) {
                    totalList.remove(i);
                    i--;
                }
            }

            recyclerAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

    // 리사이클러뷰 어댑터
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewholder> {
        ArrayList<ItemObject> items;

        // 생성자에서 데이터 리스트 객체를 전달 받는다.
        public RecyclerAdapter(ArrayList<ItemObject> list) {
            this.items = list;
        }

        // 아이템 뷰를 저장하는 뷰홀더 클래스
        public class CustomViewholder extends RecyclerView.ViewHolder {
            // 아이템 하나당 포함하고 있는 텍스트 뷰는 총 5가지
            TextView index, title, author, date, count;

            public CustomViewholder(View itemView) {
                super(itemView);

                this.index = itemView.findViewById(R.id.index);
                this.title = itemView.findViewById(R.id.title);
                this.author = itemView.findViewById(R.id.author);
                this.date = itemView.findViewById(R.id.date);
                this.count = itemView.findViewById(R.id.count);
            }
        }

        @NonNull
        @Override // 아이템 뷰를 저장하는 뷰홀더 객체 생성하여 리턴
        public CustomViewholder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_item, viewGroup, false);

            return new CustomViewholder(view);
        }

        @Override // position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
        public void onBindViewHolder(CustomViewholder holder, int position) {
            holder.index.setText(items.get(position).index);
            holder.title.setText(items.get(position).title);
            holder.author.setText(items.get(position).author);
            holder.date.setText(items.get(position).date);
            holder.count.setText(items.get(position).count.toString());

            // 아이템 뷰의 제목 부분을 클릭하면, 링크 타고 웹으로 넘어가기
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = holder.getAdapterPosition();

                    // 암시적 인텐트 (기본 웹브라우저 띄우기)
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(pos).link));
                    startActivity(browserIntent);
                    Toast.makeText(NoticeActivity.this, "click " + holder.title.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return (items != null ? items.size() : 0);
        }
    }
}
