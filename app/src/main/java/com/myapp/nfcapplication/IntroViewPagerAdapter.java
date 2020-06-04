package com.myapp.nfcapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {

    Context mContext;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen1, null);

        LinearLayout linear1 = (LinearLayout)layoutScreen.findViewById(R.id.linear1);
        LinearLayout linear2 = (LinearLayout)layoutScreen.findViewById(R.id.linear2);

        if(position==0){
                linear1.setVisibility(View.VISIBLE);
                linear2.setVisibility(View.GONE);
        }else{
               linear1.setVisibility(View.GONE);
                linear2.setVisibility(View.VISIBLE);
        }

/*        ImageView imgSlide = (ImageView) layoutScreen.findViewById(R.id.intro_img);
        TextView title = (TextView) layoutScreen.findViewById(R.id.intro_title);
        TextView description = (TextView) layoutScreen.findViewById(R.id.intro_description);

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());*/

        container.addView(layoutScreen);

        return layoutScreen;


    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }
}
