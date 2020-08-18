package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.diagnostics.Adapters.DiagnosticsCartRecyclerAdapter;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.DiagnosticsCart;
import com.doconline.doconline.model.DiagnosticsCartItem;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_CART_ITEM;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;


public class DiagnosticsCartListingFragment extends BaseFragment implements DiagnosticsCartRecyclerAdapter.OnCartItemSelectedListener {

    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_CART_DATA = 1;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE = 20001;

    DiagnosticsCart cartDiagnostics;

    private Context         context = null;
    private OnHttpResponse response_listener;
    private OnPageSelection pageSelectionListener;


    RecyclerView recyclerView_DiagnosticsCartItems;


    TextView textView_CartCount;


    TextView textView_totalCartPrice;


    TextView textView_noItemsText;

    DiagnosticsCartRecyclerAdapter mCartItemsAdapter;

    OnLoadingStatusChangedListener loadingStatusChangedListener;

    private int index = -1;
    private String currencySymbol;

    public DiagnosticsCartListingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DiagnosticsCartListingFragment(Context context, OnPageSelection pageSelectionListener ,OnHttpResponse response_listener, OnLoadingStatusChangedListener loadingStatusListener) {
        this.context = context;
        this.pageSelectionListener = pageSelectionListener;
        this.response_listener = response_listener;
        this.loadingStatusChangedListener = loadingStatusListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_diagnostics_cart_listing, container, false);
       // ButterKnife.bind(this, view);

         recyclerView_DiagnosticsCartItems = view.findViewById(R.id.recyclerView_DiagnosticsCartItems);

         textView_CartCount= view.findViewById(R.id.tv_cartCount);

         textView_totalCartPrice= view.findViewById(R.id.tv_totalCartPrice);

        textView_noItemsText= view.findViewById(R.id.tv_noItemsText);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        cartDiagnostics = new DiagnosticsCart(4459.0f,2);
        this.initAdapter();
        currencySymbol = context.getResources().getString(R.string.Rupee)  + " ";
        syncDiagnosticsCartData();
    }

    public void syncDiagnosticsCartData()
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_CART_DATA, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getDiagnosticsCartItemsURL());
        }
    }

    private void initAdapter()
    {
        if(mCartItemsAdapter != null)
        {
            return;
        }

        textView_noItemsText.setVisibility(View.VISIBLE);
        recyclerView_DiagnosticsCartItems.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView_DiagnosticsCartItems.setLayoutManager(mLayoutManager);

        mCartItemsAdapter = new DiagnosticsCartRecyclerAdapter(getContext(),  this);
        recyclerView_DiagnosticsCartItems.setAdapter(mCartItemsAdapter);
    }

    public void refreshAdapter()
    {
        try
        {
            if(mController.getDiagnosticsCartItemListCount() == 0)
                textView_noItemsText.setVisibility(View.VISIBLE);
            else
                textView_noItemsText.setVisibility(View.GONE);

            mCartItemsAdapter.notifyDataSetChanged();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();

        if(null != loadingStatusChangedListener)
            loadingStatusChangedListener.showLoadingActivity();
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
            int updatedCartCount = 0;
            int currentSubTotal = 0;
            JSONObject json = new JSONObject(response);

            if (HttpClient.OK == responseCode) {
                try {
                    //String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

                    if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_CART_DATA ) {
                        this.parseDiagnosticsCartData(json);
                    }
                    else if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE) {

                        JSONObject jsonData = json.getJSONObject(KEY_DATA);
                        int packageIDToDeleteFromCart = mController.getCartItemsList().get(index).getProductID();
                        mController.deleteCartItems(packageIDToDeleteFromCart);

                        mController.getIndexOfItemFromDiagnosticsItemList(packageIDToDeleteFromCart).setAddedToCart(false);

                        updatedCartCount = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_COUNT);
                        currentSubTotal = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT);
                        int currentCartCount = mController.getDiagnosticsCartItemListCount();
                        MyApplication.prefs.edit().putInt(KEY_DIAGNOSTICS_CART_COUNT, updatedCartCount).apply();


                        if(currentCartCount == updatedCartCount) {
                            mController.getSession().putDiagnosticsCartCount(updatedCartCount);
                            mController.UpdateCart(updatedCartCount, (float) currentSubTotal);

                            this.showDialog("Information", "Item deleted from cart");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
//                            mController.getDiagnosticsCart().getCartItemsCount();
                            refreshAdapter();
                            textView_CartCount.setText(String.valueOf(mController.getDiagnosticsCartItemListCount()));
                            textView_totalCartPrice.setText(currencySymbol + mController.getDiagnosticsCart().getTotalCartPrice());
                            if (null != loadingStatusChangedListener)
                                loadingStatusChangedListener.hideProgressbarWithSuccess();
                        }
                    });

                    return;
                }
            }
            else if(HttpClient.UNPROCESSABLE_ENTITY == responseCode){
                String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);
                switch (requestCode){
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_CART_DATA:
                        //break;
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE:
                        this.showDialog("Alert", message);
                        break;
                }
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

    private void parseDiagnosticsCartData(JSONObject jsonObject) {

        if(null != mController.diagnosticsCart)
            mController.clearDiagnosticsCart();

            try {
                mController.setDiagnosticsCart(DiagnosticsCart.getDiagnosticsCartDetailsFromJSON(jsonObject.getJSONObject(KEY_DATA)));
                textView_CartCount.setText(String.valueOf(mController.diagnosticsCart.getiCartItemQty()));
                textView_totalCartPrice.setText(currencySymbol + mController.diagnosticsCart.getTotalCartPrice());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                refreshAdapter();
            }
    }

    @Override
    public void OnCartItemDeleteClicked(int position) {

        this.index = position;
        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_CART_ITEM, this)
                .showDialogWithAction("Delete this cart item!",
                        getResources().getString(R.string.dialog_diagnostics_cart_item_delete_warning),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), false);
    }

    @Override
    public void OnBookCartItem(int cartItemPosition) {

        DiagnosticsCartItem item = mController.getCartItemAt(cartItemPosition);

        if(pageSelectionListener != null) {
            MyApplication.prefs.edit().putInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, cartItemPosition).apply();
            DiagnosticsBookAppointmentFragment.isBookedFromCartFragment = true;
            pageSelectionListener.onPageSelection(2, "Book Appointment");
        }
    }

    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_CART_ITEM)
        {
            if(new InternetConnectionDetector(context).isConnected())
            {
                //layout_loading.setVisibility(View.VISIBLE);
                String cartItemDeleteURL = mController.getDiagnosticsRemoveFromCartURL();

                String rawJSON = "{\""+ Constants.KEY_DIAGNOSTICS_PACKAGEID + "\":" + "\""+ mController.getCartItemsList().get(index).getProductID() + "\"}";

                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE, MyApplication.HTTPMethod.BDELETE.getValue(),
                        true,
                        rawJSON,
                            DiagnosticsCartListingFragment.this)
                        .execute( cartItemDeleteURL );
            }

            else
            {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }

    private void showDialog(String title, String content)
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        new CustomAlertDialog(getContext(), this).
                showAlertDialogWithPositiveAction(title, content, context.getResources().getString(R.string.OK),true);
    }
}
