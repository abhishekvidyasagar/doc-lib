package com.doconline.doconline.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.OrderLineItemsRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.Medicine;
import com.doconline.doconline.model.Orders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.order.OrderActivity.HTTP_REQUEST_CODE_GET_ORDER_DETAILS;

/**
 * Created by cbug on 29/9/17.
 */
public class OrderDetailsFragment extends BaseFragment
{
    private Context mContext = null;
    private OnHttpResponse listener;

    private Orders mOrder = new Orders();


    RecyclerView mRecyclerView ;

    TextView orderId;

    TextView status;

    TextView addline1;

    TextView addline2;

    TextView pincode;

    TextView grand_total;

    LinearLayout order_details_layout;

    private OrderLineItemsRecyclerAdapter mAdapter;


    public OrderDetailsFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public OrderDetailsFragment(Context mContext, OnHttpResponse listener)
    {
        this.mContext = mContext;
        this.listener = listener;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        //ButterKnife.bind(this, view);

         mRecyclerView = view.findViewById(R.id.recycler_list) ;
         orderId = view.findViewById(R.id.orderId);
        status= view.findViewById(R.id.status);
         addline1= view.findViewById(R.id.addline1);
        addline2= view.findViewById(R.id.addline2);
         pincode= view.findViewById(R.id.pincode);
        grand_total= view.findViewById(R.id.grand_total);
        order_details_layout= view.findViewById(R.id.order_details_layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        String orderNo = getArguments().getString("ORDER_ID");

        order_details_layout.setVisibility(View.GONE);

        if(new InternetConnectionDetector(mContext).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_ORDER_DETAILS, MyApplication.HTTPMethod.GET.getValue(), this).execute(this.mController.getOrderListURL() + orderNo);
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }

        this.initAdapter();
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
        mAdapter = new OrderLineItemsRecyclerAdapter(getContext(), mOrder.getMedicineList());
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onPreExecute()
    {
        listener.onPreExecute();
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
            if(requestCode == HTTP_REQUEST_CODE_GET_ORDER_DETAILS && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.get_data(json.getJSONObject(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(getActivity(), this, getView()).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            listener.onPostExecute(requestCode, responseCode, response);
        }
    }


    public void get_data(JSONObject json)
    {
        try
        {
            String order_id = json.getString("order_id");
            String amount = json.getString("order_amount");
            String pincode = json.getString("pincode");
            String address_line1 = json.getString("address1");
            String address_line2 = (json.isNull("address2")) ? "" : json.getString("address2");

            String order_date = json.getString("created_at");

            JSONArray jsonArray = json.getJSONArray("items");

            json = json.getJSONObject("medplus");
            String status = json.getString("currentStatus");

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject json_data = jsonArray.getJSONObject(i);

                String medicine_name = json_data.getString("name");
                String medicine_id = json_data.getString("product_id");

                String price = json_data.getString("price");

                double quantity = Double.valueOf(json_data.getString("qty"));

                json_data = json_data.getJSONObject("extra_data");

                String packsize = json_data.getString("packsize");
                double discount = json_data.getDouble("discountPercentage");
                String medicine_type = json_data.getString("productForm");
                String manufacturer = json_data.getString("manf");
                String mrp = json_data.getString("mrp");

                mOrder.getMedicineList().add(new Medicine(medicine_id, medicine_name, medicine_type, manufacturer, packsize, (int) quantity, mrp, discount, price));
            }

            mOrder.setOrderId(order_id);
            mOrder.setAmount(amount);
            mOrder.setDate(order_date);

            mOrder.setStatus(status);
            mOrder.getAddress().setPincode(pincode);
            mOrder.getAddress().setAddress_1(address_line1);
            mOrder.getAddress().setAddress_2(address_line2);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            bindView();
        }
    }


    private void bindView()
    {
        order_details_layout.setVisibility(View.VISIBLE);

        mAdapter.notifyDataSetChanged();
        orderId.setText(mOrder.getOrderId());
        status.setText(mOrder.getStatus());
        addline1.setText(Helper.toCamelCase(mOrder.getAddress().getAddress_1()));

        if(mOrder.getAddress().getAddress_2().isEmpty())
        {
            addline2.setVisibility(View.GONE);
        }

        else
        {
            addline2.setVisibility(View.VISIBLE);
            addline2.setText(Helper.toCamelCase(mOrder.getAddress().getAddress_2()));
        }

        pincode.setText(mOrder.getAddress().getPincode());
        grand_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(Orders.calculateGrandTotal(mOrder.getMedicineList())));
    }
}