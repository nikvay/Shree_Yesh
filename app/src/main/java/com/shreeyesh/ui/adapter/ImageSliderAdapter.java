package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shreeyesh.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageSliderAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<Integer> arrayList;

    public ImageSliderAdapter(Context mContext, ArrayList<Integer> arrayList) {
        this.mContext = mContext;
        this.layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
     /*   BitmapDrawable bd = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.app_logo);
        int img_height = bd.getBitmap().getHeight();
        int img_width = bd.getBitmap().getWidth();
*/
        View imageLayout = layoutInflater.inflate(R.layout.list_row_item_image_slider, view, false);

        final ImageView iv_imageSlider = imageLayout.findViewById(R.id.iv_imageSlider);
        FrameLayout fl_image_slider = imageLayout.findViewById(R.id.fl_image_slider);

       /* fl_image_slider.getLayoutParams().width = img_width + (img_width / 7);
        fl_image_slider.getLayoutParams().height = img_height;*/


        Picasso.get()
                .load(arrayList.get(position))
                .into(iv_imageSlider);

        view.addView(imageLayout, 0);

        return imageLayout;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
