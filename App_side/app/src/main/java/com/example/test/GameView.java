package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class GameView extends AppCompatActivity {
    private ViewPager viewPager;

    //三個view
    private View view1;
    private View view2;
    private View view3;
    private View view0;

    //用來存放view並傳遞給viewPager的介面卡。
    private ArrayList<View> pageview;

    private ImageView[] tips = new ImageView[4];

    private ImageView imageView;
    private ViewGroup group;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);


        viewPager = (ViewPager)findViewById(R.id.viewPager);
        view0 = getLayoutInflater().inflate(R.layout.game_view0,null);
        view1 = getLayoutInflater().inflate(R.layout.game_view1,null);
        view2 = getLayoutInflater().inflate(R.layout.game_view2,null);
        view3 = getLayoutInflater().inflate(R.layout.game_view3,null);
        pageview = new ArrayList<View>();
        pageview.add(view0);
        pageview.add(view1);
        pageview.add(view2);
        pageview.add(view3);
        group = (ViewGroup)findViewById(R.id.viewGroup);
        tips = new ImageView[pageview.size()];
        for(int i =0;i<pageview.size();i++){
            imageView = new ImageView(GameView.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(20,20));
            imageView.setPadding(20, 0, 20, 0);
            tips[i] = imageView;

            //預設第一張圖顯示為選中狀態
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }

            group.addView(tips[i]);
        }
        //這裡的mypagerAdapter是第三步定義好的。
        viewPager.setAdapter(new mypagerAdapter(pageview));
        //這裡的GuiPageChangeListener是第四步定義好的。
        viewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{
//        private ArrayList<View> pageview;
//        private ImageView[] tips = new ImageView[3];
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            tips[position].setBackgroundResource(R.mipmap.page_indicator_focused);
            //這個圖片就是選中的view的圓點
            for(int i=0;i<pageview.size();i++){
                if (position != i) {
                    tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                    //這個圖片是未選中view的圓點
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class mypagerAdapter extends PagerAdapter {

        private ArrayList<View> pageview1;

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            Log.d("MainActivityDestroy",position+"");
            if (pageview1.get(position)!=null) {
                container.removeView(pageview1.get(position));
            }
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageview1.get(position));
            Log.d("MainActivityInstanti",position+"");
            return pageview1.get(position);
        }
        public mypagerAdapter(ArrayList<View> pageview1){
            this.pageview1 = pageview1;
        }
        @Override
        public int getCount() {
            return pageview1.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object==view;
        }
    }
}





//class GuidePageChangeListener implements ViewPager.OnPageChangeListener{
//    private ArrayList<View> pageview;
//    private ImageView[] tips = new ImageView[3];
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        tips[position].setBackgroundResource(R.mipmap.page_indicator_focused);
//        //這個圖片就是選中的view的圓點
//        for(int i=0;i<pageview.size();i++){
//            if (position != i) {
//                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
//                //這個圖片是未選中view的圓點
//            }
//        }
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//}