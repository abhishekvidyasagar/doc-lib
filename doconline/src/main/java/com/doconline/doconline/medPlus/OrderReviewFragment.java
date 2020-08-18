package com.doconline.doconline.medPlus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.MedicationsAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnProcureMedicineListener;
import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.Medicine;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.MMM_DD_YYYY;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_ORDER_MEDICINE;

/**
 * Created by cbug on 25/9/17.
 */

public class OrderReviewFragment extends BaseFragment implements View.OnClickListener
{

    Button button_go;

    TextView tv_step_two;

    TextView tv_step_one;

    TextView tv_step_three;

    TextView tv_step_four;

    RecyclerView mRecyclerView;

    TextView tv_address_1;

    TextView tv_address_2;

    TextView tv_date;

    TextView tv_mobile_no;

    TextView tv_pincode;

    TextView tv_sub_total;


    private MedicationsAdapter mAdapter;
    private OnProcureMedicineListener medicine_listener;
    private OnHttpResponse response_listener;
    private ArrayList<Medicine> mList;
    private Context context;
    private int appointment_id;
    private Address mShippingAddress;


    public OrderReviewFragment()
    {

    }


    @SuppressLint("ValidFragment")
    public OrderReviewFragment(Context context, OnProcureMedicineListener medicine_listener, OnHttpResponse response_listener, ArrayList<Medicine> mList, int appointment_id, Address mShippingAddress)
    {
        this.context = context;
        this.medicine_listener = medicine_listener;
        this.response_listener = response_listener;
        this.mList = mList;
        this.appointment_id = appointment_id;
        this.mShippingAddress = mShippingAddress;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        //ButterKnife.bind(this, view);

        button_go = view.findViewById(R.id.btnGo);
        tv_step_two= view.findViewById(R.id.tv_step_two);
        tv_step_one= view.findViewById(R.id.tv_step_one);
        tv_step_three= view.findViewById(R.id.tv_step_three);
        tv_step_four= view.findViewById(R.id.tv_step_four);
        mRecyclerView= view.findViewById(R.id.recycler_list);
        tv_address_1= view.findViewById(R.id.address_1);
        tv_address_2= view.findViewById(R.id.address_2);
        tv_date= view.findViewById(R.id.tv_date);
        tv_mobile_no= view.findViewById(R.id.mobile_no);
        tv_pincode= view.findViewById(R.id.pincode);
        tv_sub_total= view.findViewById(R.id.tv_sub_total);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_two.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_three.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_four.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        button_go.setText(R.string.place_order);

        this.addListener();
        this.initAdapter();
        this.setValues();

        tv_sub_total.setText("Sub Total : " + context.getResources().getString(R.string.Rs) + " " + Constants.df.format(this.getSubTotal()));
    }

    private void addListener()
    {
        button_go.setOnClickListener(this);
    }


    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new MedicationsAdapter(context, this.mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private double getSubTotal()
    {
        double sub_total = 0;

        for (Medicine e : this.mList)
        {
            double price = Double.parseDouble(e.getPrice());
            int quantity = e.getRequired_pack_size();

            sub_total += (price * quantity * 100) / 100;
        }

        return sub_total;
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnGo) {
            if (new InternetConnectionDetector(getActivity()).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_ORDER_MEDICINE, MyApplication.HTTPMethod.POST.getValue(), true,
                        Medicine.getMedicineJSON(this.mList, mShippingAddress), this).
                        execute(mController.getPlaceOrderURL() + appointment_id);
            } else {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }


    public void setValues()
    {
        tv_mobile_no.setText(mShippingAddress.getPhoneNo());
        tv_address_1.setText(mShippingAddress.getAddress_1());
        tv_address_2.setText(mShippingAddress.getAddress_2());
        tv_pincode.setText(mShippingAddress.getPincode());

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(MMM_DD_YYYY);
            tv_date.setText(sdf.format(System.currentTimeMillis()));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }


    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
    }


    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (getActivity() == null)
        {
            return;
        }

        if (getActivity().isFinishing())
        {
            return;
        }

        if (getView() == null)
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_ORDER_MEDICINE && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String orderId = jsonObject.getJSONObject(KEY_DATA).getString("orderId");

                    medicine_listener.onOrderId(orderId);
                    medicine_listener.onPageSelection(6, "Order Success");
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(responseCode == HttpClient.UNPROCESSABLE_ENTITY)
            {
                JSONObject json = new JSONObject(response);
                new CustomAlertDialog(getContext(), this, getView()).showSnackbar(json.getString(KEY_MESSAGE), CustomAlertDialog.LENGTH_LONG);
                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }
}