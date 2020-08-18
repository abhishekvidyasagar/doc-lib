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

//import com.adgyde.android.PAgent;
/*import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;*/


/**
 * Created by chiranjit on 22/12/16.
 */
public class RegistrationEmailConfirmationFragment extends Fragment implements View.OnClickListener
{


    Button button_existing_user;

    TextView tv_step_two;

    TextView tv_step_one;

    GifImageView ivSuccess;

    private OnPageSelection page_listener;


    public RegistrationEmailConfirmationFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public RegistrationEmailConfirmationFragment(OnPageSelection page_listener)
    {
        this.page_listener = page_listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_registation_email_confirmation, container, false);
       /// ButterKnife.bind(this, view);

        button_existing_user = view.findViewById(R.id.btnExistingUser);
         tv_step_two= view.findViewById(R.id.tv_step_two);
        tv_step_one= view.findViewById(R.id.tv_step_one);
        ivSuccess= view.findViewById(R.id.success_image);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_two.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        this.addListener();

        ivSuccess.setGifImageResource(R.drawable.small_mail);

        //if(!BuildConfig.DEBUG) {
            //For event tracking
            //Send AppsFlyer Analytics registered tracking event
            trackAppsFlyerUserRegistrationCompletedEvent();
        //}
    }

    public void trackAppsFlyerUserRegistrationCompletedEvent(){
 /*       Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD, "Sign_Direct_Registration");
        AppsFlyerLib.getInstance().trackEvent(getContext(), AFInAppEventType.COMPLETE_REGISTRATION, eventValue);*/

        //for adgyde
  /*      HashMap<String, String> params = new HashMap<String, String>();
        params.put("Registrations", "Email");//patrametre name,value
        PAgent.onEvent("Registrations", params);//eventid
        PAgent.flush();*/
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