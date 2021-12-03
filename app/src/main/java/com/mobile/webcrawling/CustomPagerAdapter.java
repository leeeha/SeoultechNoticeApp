package com.mobile.webcrawling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

public class CustomPagerAdapter extends PagerAdapter {
    // LayoutInflater 서비스 사용을 위한 Context 참조
    private Context context;

    public CustomPagerAdapter(Context c){
        context = c;
    }

    @NonNull
    @Override // 화면에 표시할 페이지뷰 생성
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View pageView = null;

        if(context != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pageView = inflater.inflate(R.layout.page, container, false);

            TextView textView = pageView.findViewById(R.id.textView);
            textView.setText("Page " + position);
        }

        container.addView(pageView);

        return pageView;
    }

    // 뷰페이저에서 삭제
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return 10; // 전체 페이지수는 10개로 고정
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
}
