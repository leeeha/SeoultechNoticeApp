package com.mobile.seoultechnoticeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

// 스레드 동작과 UI 업데이트를 한번에 처리하는 AsyncTask
public class WebCrawlingTask extends AsyncTask<String, Void, ArrayList<ItemObject>> {
    private Context context;
    private ProgressDialog prog;

    public WebCrawlingTask(Context c){
        this.context = c;
    }

    @Override // 스레드 실행되기 전
    protected void onPreExecute() {
        prog = new ProgressDialog(context);
        prog.setMessage("Loading....");
        prog.show();
    }

    @Override // 스레드 실행 중 (웹 크롤링)
    protected ArrayList<ItemObject> doInBackground(String... params) {
        ArrayList<ItemObject> list = new ArrayList<>();

        try {
            // 인자로 전달 받은 URL의 html 코드를 전부 가져오기
            Document doc = Jsoup.connect(params[0]).get();
            Elements rows = doc.select("tr.body_tr");

            // 20개의 행
            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td"); // 6개의 열

                String index;
                if(cols.get(0).children().hasAttr("abs:alt")){
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
