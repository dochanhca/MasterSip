package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 12/8/16.
 */

public class TutorialPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Integer> drawableIds;
    private LayoutInflater inflater;

    public TutorialPagerAdapter(Context context, ArrayList<Integer> drawableIds) {
        this.context = context;
        this.drawableIds = drawableIds;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return drawableIds.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = inflater.inflate(R.layout.item_tutorial_slider, container, false);

        int image = drawableIds.get(position);

        ImageView imgFullScreen = (ImageView) convertView.findViewById(R.id.img_full_screen);
        ImageView imgLogoHeader = (ImageView) convertView.findViewById(R.id.img_logo_header);
        ImageView imgMainLogo = (ImageView) convertView.findViewById(R.id.img_logo_master_sip);
        ImageView imgSecondLogo = (ImageView) convertView.findViewById(R.id.img_second_logo);

        if (position == 0) {
            imgFullScreen.setVisibility(View.GONE);
            imgLogoHeader.setVisibility(View.VISIBLE);
            imgMainLogo.setVisibility(View.VISIBLE);
            imgSecondLogo.setVisibility(View.VISIBLE);
        } else {
            imgFullScreen.setVisibility(View.VISIBLE);
            imgFullScreen.setImageResource(image);
            imgLogoHeader.setVisibility(View.GONE);
            imgMainLogo.setVisibility(View.GONE);
            imgSecondLogo.setVisibility(View.GONE);
        }

        container.addView(convertView);

        return convertView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
