package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.GifImageView;
import com.doconline.doconline.helper.OnPageSelection;


/**
 * Created by chiranjit on 22/12/16.
 */
public class EmailConfirmationFragment extends Fragment implements View.OnClickListener
{


    Button button_existing_user;

    TextView tvMessage;

    GifImageView ivSuccess;

    private OnPageSelection page_listener;
    private String mMessage;


    public EmailConfirmationFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public EmailConfirmationFragment(OnPageSelection page_listener, String mMessage)
    {
        this.page_listener = page_listener;
        this.mMessage = mMessage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_email_confirmation, container, false);
        //ButterKnife.bind(this, view);

         button_existing_user =  view.findViewById(R.id.btnExistingUser);
        tvMessage=  view.findViewById(R.id.tv_message);
        ivSuccess=  view.findViewById(R.id.success_image);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.addListener();

        ivSuccess.setGifImageResource(R.drawable.small_mail);
        tvMessage.setText(mMessage);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void addListener()
    {
        button_existing_user.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnExistingUser) {
            page_listener.onPageSelection(0, "SIGN IN");
        }
    }
}