package com.uconnekt.ui.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.uconnekt.R;

public class BaseActivity extends AppCompatActivity {

    public long mLastClickTime = 0;

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void hideKeyboard() {
        View view = BaseActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void replaceFragment(Fragment fragmentHolder) {
        try{
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String fragmentName = fragmentHolder.getClass().getName();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);  //animation
            fragmentTransaction.replace(R.id.framlayout, fragmentHolder,fragmentName).addToBackStack(fragmentName);
            fragmentTransaction.commit();
            hideKeyboard();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Fragment addFragment(Fragment fragmentHolder) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentName = fragmentHolder.getClass().getName();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setEnterTransition(null);
            }
            fragmentTransaction.add(R.id.framlayout, fragmentHolder, fragmentName).addToBackStack(fragmentName);
            fragmentTransaction.commit();
            hideKeyboard();
            return fragmentHolder;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Fragment getCurrentFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

}
