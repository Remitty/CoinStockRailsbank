package me.riddhimanadib.library;

import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;


/**
 * A base class that holds the title, icon and an instance of the fragment to be shown as each
 * Navigation page
 *
 * Created by Adib on 15-Apr-17.
 */

public class NavigationPage {

    private String mtitle;
    private Drawable micon;
    private Fragment mfragment;

    public NavigationPage(String title, Drawable icon, Fragment fragment) {
        mtitle = title;
        micon = icon;
        mfragment = fragment;
    }

    public String getTitle() {
        return mtitle;
    }

    public Drawable getIcon() {
        return micon;
    }

    public Fragment getFragment() {
        return mfragment;
    }
}
