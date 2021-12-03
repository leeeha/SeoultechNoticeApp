package com.mobile.webcrawling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class SecondActivity extends Activity {
    final String mainURL = "https://www.seoultech.ac.kr/service/info/notice/";
    EditText query;
    RecyclerView recyclerView;
    ArrayList<ItemObject> list;
    RecyclerAdapter adapter;
    Button sortButton;
    Boolean isSorted = false;

//    private ViewPager viewPager;
//    private CustomPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        query = findViewById(R.id.query);
        recyclerView = findViewById(R.id.recyclerview_main_list);
        sortButton = findViewById(R.id.sortButton);

//        viewPager = findViewById(R.id.viewPager);
//        pagerAdapter = new CustomPagerAdapter(this);
//        viewPager.setAdapter(pagerAdapter);

        // 크롤링 데이터를 저장할 리스트 생성
        list = new ArrayList<>();

        try {
            // 크롤링 데이터로 리스트 초기화
            BackgroundTask task = new BackgroundTask();
            list = task.execute(mainURL).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // 리사이클러뷰의 레이아웃 매니저, 어댑터 지정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(list);
        recyclerView.setAdapter(adapter);

        // 조회수 기준으로 내림차순 정렬해서 보여주기
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSorted){
                    // list의 모든 아이템을 temp로 복사하면,
                    // temp를 정렬해도 원본 list는 바뀌지 않는다.
                    ArrayList<ItemObject> temp = new ArrayList<>(list);
                    Collections.sort(temp);
                    RecyclerAdapter tempAdapter = new RecyclerAdapter(temp);
                    recyclerView.setAdapter(tempAdapter);
                    isSorted = true;
                    sortButton.setText("되돌리기");
                }
                else{
                    // 정렬되지 않은 원본 list를 데이터로 갖고 있는 adapter로 다시 설정
                    recyclerView.setAdapter(adapter);
                    isSorted = false;
                    sortButton.setText("조회수 높은 순");
                }
            }
        });

        // 검색어를 URL 뒤에 붙여서 그 결과를 크롤링으로 다시 가져오기
        query.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    // 제목을 기준으로 첫번째 페이지만 검색 (page=1, searchtype=1)
                    final String searchURL = "https://www.seoultech.ac.kr/service/info/notice/?bidx=4691&bnum=4691&allboard=false&" +
                            "page=1&size=&searchtype=1&searchtext=" + query.getText();
                    try {
                        list = new BackgroundTask().execute(searchURL).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    recyclerView.setAdapter(adapter);
                }
                return false;
            }
        });


    }

    // 스레드 동작과 UI 업데이트를 한번에 처리하는 AsyncTask
    public class BackgroundTask extends AsyncTask<String, Void, ArrayList<ItemObject>> {
        private ProgressDialog prog;

        @Override // 스레드 실행되기 전
        protected void onPreExecute() {
            prog = new ProgressDialog(SecondActivity.this);
            prog.setMessage("Loading....");
            prog.show();
        }

        @Override // 스레드 실행 중 (웹 크롤링)
        protected ArrayList<ItemObject> doInBackground(String... params) {
            list.clear(); // 새로 크롤링 해올 때마다 리스트 초기화

            try {
                Document doc = Jsoup.connect(params[0]).get();

                // 20개의 행
                Elements rows = doc.select("tr.body_tr");
                for (int i = 0; i < rows.size(); i++) {
                    Element row = rows.get(i);

                    // 6개의 열
                    Elements cols = row.select("td");

                    String index;
                    if(i < 6){
                        index = "공지";
                    }else{
                        index = cols.get(0).text();
                    }

                    String title = cols.get(1).text();
                    Element href = cols.get(1).select("a[href]").get(0);
                    String link = href.attr("abs:href");

                    String author = cols.get(3).text();
                    String date = cols.get(4).text();
                    Integer count = Integer.parseInt(cols.get(5).text());

                    list.add(new ItemObject(index, title, link, author, date, count));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemObject> result) {
            super.onPostExecute(result);

            // 스레드에서 웹 데이터 정상적으로 불러오면,
            // 로딩 상태를 나타내는 다이얼로그 종료
            prog.dismiss();
        }
    }

    // 리사이클러뷰 어댑터
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewholder> {
        ArrayList<ItemObject> items;

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

        // 생성자에서 데이터 리스트 객체를 전달 받는다.
        public RecyclerAdapter(ArrayList<ItemObject> list) {
            this.items = list;
        }

        @NonNull
        @Override // 아이템 뷰를 저장하는 뷰홀더 객체 생성하여 리턴
        public CustomViewholder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        Context context = viewGroup.getContext();
//        LayoutInflater inflater =
//                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.recyclerview_item, viewGroup, false);

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
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(pos).link));
                    startActivity(browserIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (items != null ? items.size() : 0);
        }
    }
}
