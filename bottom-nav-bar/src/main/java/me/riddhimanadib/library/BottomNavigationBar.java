package me.riddhimanadib.library;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to handle bottom navigation UI and click events
 *
 * Created by Adib on 13-Apr-17.
 */

public class BottomNavigationBar implements View.OnClickListener {

    public static final int MENU_BAR_1 = 0;
    public static final int MENU_BAR_2 = 1;
    public static final int MENU_BAR_3 = 2;
    public static final int MENU_BAR_4 = 3;
    public static final int MENU_BAR_5 = 4;

    private List<NavigationPage> mNavigationPageList = new ArrayList<>();

    private Context mContext;
    private AppCompatActivity mActivity;
    private BottomNavigationMenuClickListener mListener;

    private LinearLayout mLLBar1, mLLBar2, mLLBar3, mLLBar4, mLLBar5;
    private View mViewBar1, mViewBar2, mViewBar3, mViewBar4, mViewBar5;
    private AppCompatImageView mImageViewBar1, mImageViewBar2, mImageViewBar3, mImageViewBar4, mImageViewBar5;
    private AppCompatTextView mTextViewBar1, mTextViewBar2, mTextViewBar3, mTextViewBar4, mTextViewBar5;

    public BottomNavigationBar(AppCompatActivity context, List<NavigationPage> pages, BottomNavigationMenuClickListener listener) {

        // initialize variables
        mContext = context;
        mActivity = (AppCompatActivity) mContext;
        mListener = listener;
        mNavigationPageList = pages;

        // getting reference to bar linear layout viewgroups
        mLLBar1 = (LinearLayout) context.findViewById(R.id.linearLayoutBar1);
        mLLBar2 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar2);
        mLLBar3 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar3);
        mLLBar4 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar4);
        mLLBar5 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar5);

        // getting reference to bar upper highlight
        mViewBar1 = (View) mActivity.findViewById(R.id.viewBar1);
        mViewBar2 = (View) mActivity.findViewById(R.id.viewBar2);
        mViewBar3 = (View) mActivity.findViewById(R.id.viewBar3);
        mViewBar4 = (View) mActivity.findViewById(R.id.viewBar4);
        mViewBar5 = (View) mActivity.findViewById(R.id.viewBar5);

        // getting reference to bar titles
        mTextViewBar1 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar1);
        mTextViewBar2 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar2);
        mTextViewBar3 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar3);
        mTextViewBar4 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar4);
        mTextViewBar5 = (AppCompatTextView) mActivity.findViewById(R.id.textViewBar5);

        NavigationPage temp = mNavigationPageList.get(0);
        String title = temp.getTitle();

        // 2etting the titles
        mTextViewBar1.setText(title);
        mTextViewBar2.setText(mNavigationPageList.get(1).getTitle());
        mTextViewBar3.setText(mNavigationPageList.get(2).getTitle());
        mTextViewBar4.setText(mNavigationPageList.get(3).getTitle());
        mTextViewBar5.setText(mNavigationPageList.get(4).getTitle());

        // getting reference to bar icons
        mImageViewBar1 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar1);
        mImageViewBar2 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar2);
        mImageViewBar3 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar3);
        mImageViewBar4 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar4);
        mImageViewBar5 = (AppCompatImageView) mActivity.findViewById(R.id.imageViewBar5);

        // setting the icons

        mImageViewBar1.setImageDrawable(mNavigationPageList.get(0).getIcon());
        mImageViewBar2.setImageDrawable(mNavigationPageList.get(1).getIcon());
        mImageViewBar3.setImageDrawable(mNavigationPageList.get(2).getIcon());
        mImageViewBar4.setImageDrawable(mNavigationPageList.get(3).getIcon());
        mImageViewBar5.setImageDrawable(mNavigationPageList.get(4).getIcon());

        // setting click listeners
        mLLBar1.setOnClickListener(this);
        mLLBar2.setOnClickListener(this);
        mLLBar3.setOnClickListener(this);
        mLLBar4.setOnClickListener(this);
        mLLBar5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // setting clicked bar as highlighted view
        setView(view);

        // triggering click listeners
        if (view.getId() == R.id.linearLayoutBar1) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_1);
            return;
        } else if (view.getId() == R.id.linearLayoutBar2) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_2);
            return;
        } else if (view.getId() == R.id.linearLayoutBar3) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_3);
            return;
        } else if (view.getId() == R.id.linearLayoutBar4) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_4);
            return;
        } else if (view.getId() == R.id.linearLayoutBar5) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_5);
            return;
        } else {
            return;
        }

    }

    /**
     * sets the clicked view as selected, resets other views
     * @param view clicked view
     */
    private void setView(View view) {

        // seting all highlight bar as invisible
        mViewBar1.setVisibility(View.INVISIBLE);
        mViewBar2.setVisibility(View.INVISIBLE);
        mViewBar3.setVisibility(View.INVISIBLE);
        mViewBar4.setVisibility(View.INVISIBLE);
        mViewBar5.setVisibility(View.INVISIBLE);

        // resetting colors of all icons
        mImageViewBar1.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar2.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar3.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar4.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar5.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));

        // resetting colors of all titles
        mTextViewBar1.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar2.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar3.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar4.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar5.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));

        // selectively colorizing the marked view
        if (view.getId() == R.id.linearLayoutBar1) {
            mViewBar1.setVisibility(View.VISIBLE);
            mImageViewBar1.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar1.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar2) {
            mViewBar2.setVisibility(View.VISIBLE);
            mImageViewBar2.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar2.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar3) {
            mViewBar3.setVisibility(View.VISIBLE);
            mImageViewBar3.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar3.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar4) {
            mViewBar4.setVisibility(View.VISIBLE);
            mImageViewBar4.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar4.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar5) {
            mViewBar5.setVisibility(View.VISIBLE);
            mImageViewBar5.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar5.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else {
            return;
        }

    }

    public interface BottomNavigationMenuClickListener {
        void onClickedOnBottomNavigationMenu(int menuType);
    }

}
