package com.doconline.doconline.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by chiranjit on 21/12/16.
 */
public class MyViewPager extends ViewPager
{
    private boolean enabled;

    public MyViewPager(Context context)
    {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return this.enabled;// && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return this.enabled;// && super.onTouchEvent(event);
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setTabEnabled(LinearLayout tabStrip, final boolean enabled)
    {
        tabStrip.setEnabled(enabled);

        for(int i = 0; i < tabStrip.getChildCount(); i++)
        {
            tabStrip.getChildAt(i).setClickable(enabled);
        }

        /*for(int i = 0; i < tabStrip.getChildCount(); i++)
        {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return enabled;
                }
            });
        }*/
    }
}