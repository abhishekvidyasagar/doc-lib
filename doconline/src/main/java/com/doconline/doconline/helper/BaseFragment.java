package com.doconline.doconline.helper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;

/**
 * Created by chiranjitbardhan on 20/01/18.
 */

public abstract class BaseFragment extends Fragment implements OnHttpResponse,
        OnDialogAction, OnPageSelection, OnTaskCompleted
{
    public static final String TAG = BaseFragment.class.getSimpleName();

    public MyApplication mController;


    public BaseFragment()
    {
        this.mController = MyApplication.getInstance();
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

    }

    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }

    @Override
    public void onPositiveAction(int requestCode) {

    }

    @Override
    public void onPositiveAction(int requestCode, String data) {

    }

    @Override
    public void onNegativeAction(int requestCode) {

    }

    @Override
    public void onPageSelection(int position, String title) {

    }

    @Override
    public void onTaskCompleted(boolean flag, int code, String message) {

    }
}