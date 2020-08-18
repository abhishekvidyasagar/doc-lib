package com.doconline.doconline.subscription;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.doconline.doconline.consultation.BookConsultationActivity;

import static com.doconline.doconline.app.Constants.KEY_RECURRING_TYPE;

/**
 * Created by chiranjit on 28/12/16.
 */
public class SubscriptionSuccessFragment extends Fragment implements View.OnClickListener
{
    private Context context;


    Button btnMyAccount;

    Button btnBilling ;

    TextView success_message;

    private String payment_id, razorpay_id, payment_type, plan_name;
    private boolean is_upgrade;


    public SubscriptionSuccessFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public SubscriptionSuccessFragment(Context context, String plan_name, String razorpay_id, String payment_id, String payment_type, boolean is_upgrade)
    {
        this.context = context;
        this.plan_name = plan_name;
        this.razorpay_id = razorpay_id;
        this.payment_id = payment_id;
        this.payment_type = payment_type;
        this.is_upgrade = is_upgrade;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_subscription_success, container, false);
     //   ButterKnife.bind(this, view);

       btnMyAccount = view.findViewById(R.id.btnMyAccount);
        btnBilling= view.findViewById(R.id.btnBilling) ;
        success_message= view.findViewById(R.id.confirmation_message);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.addListener();

        if(is_upgrade && payment_type.equalsIgnoreCase(KEY_RECURRING_TYPE))
        {
            String message = "We have received your request for upgradation of your membership to "
                    + plan_name.toLowerCase() + " (Membership ID: " + razorpay_id + "). " +
                    "It will be effective as soon as current billing cycle ends.";

            success_message.setText(message);
            btnMyAccount.setVisibility(View.INVISIBLE);
        }

        else if(payment_type.equalsIgnoreCase(KEY_RECURRING_TYPE))
        {
            String message = "We have received your payment for "
                    + plan_name.toLowerCase() + " (Membership ID: " + razorpay_id + ").";

            success_message.setText(message);
            btnMyAccount.setVisibility(View.VISIBLE);
        }

        else
        {
            String message = "We have received your payment for "
                    + plan_name.toLowerCase() + " (Order ID: " + razorpay_id + ").";

            success_message.setText(message);
            btnMyAccount.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void addListener()
    {
        btnMyAccount.setOnClickListener(this);
        btnBilling.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        if(getActivity() == null)
        {
            return;
        }

        int id = view.getId();
        if (id == R.id.btnMyAccount) {
            startActivity(new Intent(getActivity(), BookConsultationActivity.class));
            getActivity().finish();
        } else if (id == R.id.btnBilling) {
            startActivity(new Intent(getActivity(), BillingHistoryActivity.class));
            getActivity().finish();
        }
    }
}