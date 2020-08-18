package com.doconline.doconline.medPlus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnProcureMedicineListener;
import com.doconline.doconline.model.Address;


/**
 * Created by cbug on 25/9/17.
 */

public class AddressUpdateFragment extends BaseFragment implements View.OnClickListener
{


    Button button_go;

    TextView tv_step_one;

    TextView tv_step_two;

    TextView tv_step_three;

    TextView edit_address_1;

    TextView edit_address_2;

    private Context context;
    private OnProcureMedicineListener medicine_listener;
    private Address mShippingAddress;


    public AddressUpdateFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public AddressUpdateFragment(Context context, OnProcureMedicineListener medicine_listener, Address mShippingAddress)
    {
        this.context = context;
        this.medicine_listener = medicine_listener;
        this.mShippingAddress = mShippingAddress;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_enter_address, container, false);
        //ButterKnife.bind(this, view);

         button_go = view.findViewById(R.id.btnGo);
         tv_step_one = view.findViewById(R.id.tv_step_one);
         tv_step_two= view.findViewById(R.id.tv_step_two);
         tv_step_three= view.findViewById(R.id.tv_step_three);
         edit_address_1= view.findViewById(R.id.editAddress_1);
        edit_address_2= view.findViewById(R.id.editAddress_2);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_two.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_three.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        button_go.setText(R.string.Next);

        edit_address_1.setText(mShippingAddress.getAddress_1());
        edit_address_2.setText(mShippingAddress.getAddress_2());

        this.addListener();
    }

    private void addListener()
    {
        button_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnGo) {
            if (!isValidAddress(edit_address_1.getText().toString())) {
                return;
            }

            this.mShippingAddress.setAddress_1(edit_address_1.getText().toString());
            this.mShippingAddress.setAddress_2(edit_address_2.getText().toString());

            medicine_listener.onDeliveryAddress(mShippingAddress);
            medicine_listener.onPageSelection(5, "Order Confirmation");
        }
    }


    private boolean isValidAddress(String address)
    {
        if(address == null || TextUtils.isEmpty(address))
        {
            edit_address_1.setError("Address required");
            edit_address_1.requestFocus();
            return false;
        }

        if(address.length() < 10)
        {
            edit_address_1.setError("Minimum 10 characters");
            edit_address_1.requestFocus();
            return false;
        }

        return true;
    }
}