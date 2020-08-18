package com.doconline.doconline.medPlus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.doconline.doconline.R;
import com.doconline.doconline.order.OrderActivity;


/**
 * Created by chiranjit on 28/12/16.
 */
public class OrderSuccessFragment extends Fragment implements View.OnClickListener
{

    Button btnMyAccount;

    TextView success_message;

    private String mOrderNo;


    public OrderSuccessFragment()
    {

    }


    @SuppressLint("ValidFragment")
    public OrderSuccessFragment(String mOrderNo)
    {
        this.mOrderNo = mOrderNo;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_order_success, container, false);
       // ButterKnife.bind(this, view);

         btnMyAccount = view.findViewById(R.id.btnMyAccount);
        success_message = view.findViewById(R.id.confirmation_message);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        btnMyAccount.setOnClickListener(this);
        success_message.setText("Your order has been placed successfully with MedPlus Order ID# "
                + this.mOrderNo + "\n Payment Option: Cash on Delivery (COD)");
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnMyAccount) {
            startActivity(new Intent(getActivity(), OrderActivity.class));

            if (getActivity() == null) {
                return;
            }

            getActivity().finish();
        }
    }
}