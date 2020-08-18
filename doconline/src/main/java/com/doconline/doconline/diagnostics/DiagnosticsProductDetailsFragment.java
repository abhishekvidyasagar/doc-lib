package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.model.DiagnosticsCartItem;
import com.doconline.doconline.model.DiagnosticsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;

public class DiagnosticsProductDetailsFragment extends BaseFragment implements View.OnClickListener, OnDialogAction {

    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_PRODUCT_ADD_ITEM_TO_CART = 11101;

    Context context;

    int iProductID;
    int iProductIndex;


    ImageView imgView_ProductIcon;


    TextView tv_ProductName;


    TextView tv_ProductPrice;

    ImageView imgView_PartnerLogo;


    TextView tv_ListOfTests;


    TextView tv_List;


    CardView  cardView_ListOfTests;



    boolean isProcessing = false;

    private OnHttpResponse response_listener;
    private OnLoadingStatusChangedListener loadingStatusChangedListener;

    public DiagnosticsProductDetailsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DiagnosticsProductDetailsFragment(Context context, OnHttpResponse httpResponseListener, OnLoadingStatusChangedListener loadingStatusListener) {
        this.context = context;
        response_listener = httpResponseListener;
        this.loadingStatusChangedListener = loadingStatusListener;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_diagnostics_product_details, container, false);
       // ButterKnife.bind(this,view);
         imgView_ProductIcon = view.findViewById(R.id.imageView_ProductIcon);

         tv_ProductName= view.findViewById(R.id.tv_ProductName);

         tv_ProductPrice= view.findViewById(R.id.tv_ProductPrice);

        imgView_PartnerLogo= view.findViewById(R.id.imageView_PartnerLogo);

        tv_ListOfTests= view.findViewById(R.id.tv_ListOfTests);

        tv_List= view.findViewById(R.id.tv_static_ProductsList);

         cardView_ListOfTests= view.findViewById(R.id.cardView_ListOfTests);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View view) {

    }

    public void LoadPackageDetails()
    {
        iProductIndex = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX,-1);
        if (iProductIndex != -1) {
                if (iProductIndex == 994863253){
                    String btobobject = MyApplication.prefs.getString("object","NA");
                    if (!btobobject.equalsIgnoreCase("NA")) {
                        try {
                            JSONObject jsonObj = new JSONObject(btobobject);
                            tv_ProductName.setText(""+jsonObj.get("package_name").toString());
                            tv_ProductPrice.setText(context.getResources().getString(R.string.Rupee) + " FREE ");
                            Glide.with(getActivity())
                                    .load(jsonObj.getString("partner_img_url")) // image url
                                    .override(240, 80).fitCenter().into(imgView_PartnerLogo);

                            JSONArray listitems = jsonObj.getJSONArray("package_tests");
                            if (null == listitems)
                            {
                                cardView_ListOfTests.setVisibility(View.GONE);
                                tv_List.setVisibility(View.GONE);
                            }
                            else {
                                cardView_ListOfTests.setVisibility(View.VISIBLE);
                                tv_List.setVisibility(View.VISIBLE);

                                SpannableStringBuilder listOfTests = null;

                                if (listitems.length() > 0) {
                                    //JSONArray testsList = json.getJSONArray(KEY_DIAGNOSTICS_PACKAGE_TESTS);
                                    if (listitems != null) {
                                        listOfTests = new SpannableStringBuilder("");
                                        for (int j = 0; j < listitems.length(); j++) {
                                            SpannableString str_Item = new SpannableString(listitems.get(j) + "\n\n");
                                            str_Item.setSpan(new BulletSpan(30, Color.GREEN), 0, str_Item.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            //Concatenate into one string
                                            listOfTests.append(str_Item);
                                        }
                                    }
                                }
                                //Toast.makeText(context, "Still working on this", Toast.LENGTH_SHORT).show();
                                tv_ListOfTests.setText("" + listOfTests);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    DiagnosticsItem item = mController.getPackageDetails(iProductIndex);

                    if (item != null) {
                        tv_ProductName.setText(item.getStrPackage_name());
                        tv_ProductPrice.setText(context.getResources().getString(R.string.Rupee) + " " + item.getPrice());
                        if (null == item.getListOfTests())
                        {
                            cardView_ListOfTests.setVisibility(View.GONE);
                            tv_List.setVisibility(View.GONE);
                        }
                        else
                        {
                            cardView_ListOfTests.setVisibility(View.VISIBLE);
                            tv_List.setVisibility(View.VISIBLE);
                            tv_ListOfTests.setText(item.getListOfTests());
                        }
                    }
                }
        }
    }

    public void AddProductItemToCart() {

        if (new InternetConnectionDetector(context).isConnected()) {

            if (!isProcessing) {
                DiagnosticsItem addToCart = mController.getPackageDetails(iProductIndex);
                String cartItemAddURL = mController.getDiagnosticsAddToCartURL();

                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_PRODUCT_ADD_ITEM_TO_CART,
                        MyApplication.HTTPMethod.POST.getValue(),
                        DiagnosticsItem.composeUserJSON(addToCart),
                        DiagnosticsProductDetailsFragment.this).execute(cartItemAddURL);
            }
        }
        else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    @Override
    public void onPreExecute()
    {
        isProcessing = true;
        response_listener.onPreExecute();
        if(loadingStatusChangedListener != null)
            loadingStatusChangedListener.showLoadingActivity();
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (getActivity() == null)
        {
            if (getActivity().isFinishing())
            {
                return;
            }
        }

        if (getView() == null)
        {
            return;
        }
        try
        {
            if(requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_PRODUCT_ADD_ITEM_TO_CART) {
                try {
                    JSONObject json = new JSONObject(response);
                    String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);
                    if (HttpClient.OK == responseCode) {

                        //"data":{"updated_cart_count":5,"updated_cart_amount":3240}}
                        JSONObject jsonData = json.getJSONObject(KEY_DATA);
                        if(jsonData != null) {
                            int updatedCartCount = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_COUNT);
                            int amount = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT);
                            MyApplication.prefs.edit().putInt(KEY_DIAGNOSTICS_CART_COUNT, updatedCartCount).apply();

                            DiagnosticsItem cartItemToAdd = mController.getPackageDetails(iProductIndex);
                            mController.addNewItemToCart(new DiagnosticsCartItem(cartItemToAdd.getPackageID(),
                                    1,
                                    cartItemToAdd.getStrPackage_name(),
                                    cartItemToAdd.getPrice(),
                                    cartItemToAdd.getPartnerId()));
                            final int currentCartCount = mController.getDiagnosticsCartItemListCount();

                            if (currentCartCount == updatedCartCount) {
                                mController.getSession().putDiagnosticsCartCount(updatedCartCount);
                                mController.UpdateCart(updatedCartCount,(float)amount);
                            }

                            this.showDialog("Information", "Diagnostics package is added to your cart");
                        }
                    }
                    else if(HttpClient.UNPROCESSABLE_ENTITY == responseCode){
                        showDialog("Information", message);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                finally {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run()
                        {
                            if(null != loadingStatusChangedListener)
                                loadingStatusChangedListener.hideProgressbarWithSuccess();
                        }
                    });
                }

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
            isProcessing = false;
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }

    private void showDialog(String title, String content)
    {
        if(getActivity() == null)
        {
            if(getActivity().isFinishing())
            {
                return;
            }
        }
        if(getView() == null)
        {
            return;
        }

        new CustomAlertDialog(getContext(), this).
                showAlertDialogWithPositiveAction(title, content, context.getResources().getString(R.string.OK),true);
    }
    @Override
    public void onPositiveAction(){

        isProcessing = false;
    }
}
