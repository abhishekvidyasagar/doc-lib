package com.doconline.doconline.diagnostics;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.doconline.doconline.diagnostics.Adapters.DiagnosticsPlansRecyclerAdapter;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.helper.dragable.OnStartDragListener;
import com.doconline.doconline.model.DiagnosticsCartItem;
import com.doconline.doconline.model.DiagnosticsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_ADD_CART_ITEM;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;

/**
 * Created by admin on 2018-02-26.
 */

public class DiagnosticsListingFragment extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,OnItemDeleted, OnStartDragListener,
        DiagnosticsPlansRecyclerAdapter.OnPlanSelectedListener, OnDialogAction {

    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_GET_PACKAGES = 1;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_SEARCH_PACKAGES= 2;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_CART_ADD_ITEM = 3;

    private int iProductIndex = -1;
    private Context context;
    private OnPageSelection listener;
    private OnHttpResponse response_listener;
    private OnLoadingStatusChangedListener loadingStatusChangedListener;

    private boolean isLoading;
    private int CURRENT_PAGE, LAST_PAGE;
    private ArrayList<DiagnosticsItem> diagnosticsItemList = new ArrayList<>();
    public  String searchString = "";


    TextView empty_message;


    RecyclerView recyclerView_DiagnosticsPlans;


            LinearLayout mBtoBOffer;

    DiagnosticsPlansRecyclerAdapter mPlansAdapter;

    public DiagnosticsListingFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public DiagnosticsListingFragment(Context context, OnPageSelection listener, OnHttpResponse response_listener, OnLoadingStatusChangedListener loadingStatusListener)
    {
        this.context = context;
        this.listener = listener;
        this.response_listener = response_listener;
        this.loadingStatusChangedListener = loadingStatusListener;

        //this.diagnosticsCartActionListener = cartListener;
//        this.pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        this.context.registerReceiver(mHandleWaitingTimeReceiver, new IntentFilter(WaitngTimeBroadcast.WAITING_TIME_ACTION));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_diagnostics_listing, container, false);
       // ButterKnife.bind(this, view);

         empty_message = view.findViewById(R.id.empty_message);

         recyclerView_DiagnosticsPlans= view.findViewById(R.id.recyclerView_DiagnosticsPlans);

        mBtoBOffer= view.findViewById(R.id.btoboffer);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initAdapter();
        LoadDiagnosticsPackages();

    }

    private void LoadDiagnosticsPackages() {
        if (new InternetConnectionDetector(context).isConnected()) {

            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_GET_PACKAGES, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getDiagnosticsPlansURL());
        }
        else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    private void initAdapter()
    {
        if(mPlansAdapter != null)
        {
            return;
        }

        recyclerView_DiagnosticsPlans.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView_DiagnosticsPlans.setLayoutManager(mLayoutManager);

        mPlansAdapter = new DiagnosticsPlansRecyclerAdapter(context,  this);
        recyclerView_DiagnosticsPlans.setAdapter(mPlansAdapter);


        mPlansAdapter.SetOnItemClickListener(new DiagnosticsPlansRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {

            }
        });

        mPlansAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore()
            {
                Log.v("onLoadMore", "Load More");

                if(!new InternetConnectionDetector(context).isConnected())
                {
                    Toast.makeText(context, "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                isLoading = true;


                Handler handler = new Handler();
                final Runnable r = new Runnable()
                {
                    public void run()
                    {
                        diagnosticsItemList.add(null);
                        mPlansAdapter.notifyItemInserted(diagnosticsItemList.size() - 1);
                    }
                };
                handler.post(r);


                if (searchString.length() >= 3)
                {
                    new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_SEARCH_PACKAGES, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsListingFragment.this)
                            .execute(mController.getDiagnosticsSearchURL() + searchString + "/" + (CURRENT_PAGE +  1));
                }
                else {
                    new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_GET_PACKAGES, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsListingFragment.this)
                            .execute(mController.getDiagnosticsPlansURL() + (CURRENT_PAGE + 1));
                }
            }
        });
        this.load_more_on_scroll();
        adapter_refresh();
    }

    private void flipit(View flipit){
        ObjectAnimator flip = ObjectAnimator.ofFloat(flipit,"rotationX",0f, 360f);
        flip.setDuration(2000);
        flip.start();
    }

    private void load_more_on_scroll()
    {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView_DiagnosticsPlans.getLayoutManager();

        recyclerView_DiagnosticsPlans.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                {
                    Log.v("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount >= totalItemCount)
                    {
                        if(!isLoading && CURRENT_PAGE != LAST_PAGE)
                        {
                            if (mPlansAdapter.mOnLoadMoreListener != null)
                            {
                                mPlansAdapter.mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    private void adapter_refresh()
    {
        if(diagnosticsItemList.size() == 0)
        {
            //layout_empty.setVisibility(View.VISIBLE);
            empty_message.setText("No packages are listed here!");
        }

        else
        {
            //layout_empty.setVisibility(View.GONE);
            if(mPlansAdapter != null)
                mPlansAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
        loadingStatusChangedListener.showLoadingActivity();
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getView() == null) {
            return;
        }
        try {
            JSONObject json = new JSONObject(response);
            String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

            if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_GET_PACKAGES && responseCode == HttpClient.OK) {
                this.parseDiagnosticsPlans(json.getJSONObject(KEY_DATA));
            }
            else if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_SEARCH_PACKAGES && responseCode == HttpClient.OK) {
                //CURRENT_PAGE = 0;
                //LAST_PAGE = 0;
                this.parseDiagnosticsPlans(json.getJSONObject(KEY_DATA));
            }
            else if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_CART_ADD_ITEM) {

                switch (responseCode){
                    case HttpClient.OK:
                        JSONObject jsonData = json.getJSONObject(KEY_DATA);
                        if(jsonData != null){
                            int updatedCartCount = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_COUNT);
                            int amount = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT);
                            MyApplication.prefs.edit().putInt(KEY_DIAGNOSTICS_CART_COUNT, updatedCartCount).apply();

                            DiagnosticsItem cartItemToAdd = mController.getPackageDetails(this.iProductIndex);

                            //mController.addNewItemToCart(new DiagnosticsCartItem(cartItemToAdd.getPackageID(),
                            //        1,
                            //        cartItemToAdd.getStrPackage_name(),
                            //        cartItemToAdd.getPrice()));
                            //final int currentCartCount = mController.getDiagnosticsCartItemListCount();

                            //if(currentCartCount == updatedCartCount) {
                                mController.getSession().putDiagnosticsCartCount(updatedCartCount);
                                mController.UpdateCart(updatedCartCount,(float)amount);
                                this.showDialog("Information", "Diagnostics package is added to your cart!");
                            //}
                        }
                        //mController.getSession().putDiagnosticsCartCount(mController.getDiagnosticsCart().getCartItemsCount());
                        break;
                    case HttpClient.UNPROCESSABLE_ENTITY:
                        showDialog("Information",message);
                        break;
                }

                return;
            }
            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    //layout_loading.setVisibility(View.GONE);
                    loadingStatusChangedListener.hideProgressbarWithSuccess();
                }
            });
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }

    private void parseDiagnosticsPlans(JSONObject jsonObject) {

        try
        {
            if(isLoading)
            {
                diagnosticsItemList.remove(diagnosticsItemList.size() - 1);
                mPlansAdapter.notifyItemRemoved(diagnosticsItemList.size());
                isLoading = false;
            }

            CURRENT_PAGE = jsonObject.getInt(Constants.KEY_CURRENT_PAGE);
            LAST_PAGE = jsonObject.getInt(Constants.KEY_TOTAL_PAGE);

            diagnosticsItemList.addAll(DiagnosticsItem.getDiagnosticsItemListFromJSON(jsonObject));

            if(null != mController.list_DiagnosticsItems)
                mController.clearDiagnosticsPackageList();

            mController.setDiagnosticsPackageList(diagnosticsItemList);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            this.adapter_refresh();
        }
    }

    public void refreshDiagnosticsList()
    {
        if (mController.getDiagnosticsCartItemListCount() > 0)
        {
            for (int i = 0 ; i < diagnosticsItemList.size() ; i++)
            {

                DiagnosticsItem itemList = mController.getPackageDetails(i);
                Boolean itemInCart = false;
                for (int j = 0 ; j < mController.getDiagnosticsCartItemListCount() ; j++)
                {
                    DiagnosticsCartItem item = mController.getCartItemAt(j);
                    if (item.getProductID() == itemList.getPackageID())
                    {
                        Log.v("item Product Name",itemList.getStrPackage_name());
                        itemInCart = true;
                        break;
                    }
                }
                if (itemInCart == true)
                {
                    mController.getPackageDetails(i).setAddedToCart(true);

                }
                else
                {
                    mController.getPackageDetails(i).setAddedToCart(false);
                }
            }
            diagnosticsItemList.clear();
            diagnosticsItemList.addAll(mController.list_DiagnosticsItems);
            this.adapter_refresh();

        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mDiagnosticsItemList.clear();
    }

    @Override
    public void onResume(){
        super.onResume();
        //LoadDiagnosticsPackages();
    }

    @Override
    public void OnCheckDetailsClicked(int postition) {

        MyApplication.prefs.edit().putInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, postition).apply();

        listener.onPageSelection(1,"Package Details");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemDeleted(int index, int item_id) {

        listener.onPageSelection(1,"Package Details");
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

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

        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DIAGNOSTICS_ADD_CART_ITEM, this).
                showAlertDialogWithPositiveAction(title, content, context.getResources().getString(R.string.OK),false);
    }

    public void currentSearchString(String str) {
        //getDiagnosticsSearchURL
        //if (searchString)
        searchString = str;
        if (str.length() >= 3)
        {
            if (new InternetConnectionDetector(context).isConnected()) {
                diagnosticsItemList.clear();
                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_SEARCH_PACKAGES, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getDiagnosticsSearchURL() + str);
            }
            adapter_refresh();
        }
        else
        {
            if (new InternetConnectionDetector(context).isConnected()) {
                diagnosticsItemList.clear();
                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_GET_PACKAGES, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getDiagnosticsPlansURL());
            }
            adapter_refresh();
        }
    }

    public void showHowItWorksDialog(){

        DiagnosticsHowItWorksDialogFragment dialog = new DiagnosticsHowItWorksDialogFragment();
        dialog.setRetainInstance(true);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void onPositiveAction(int requestCode) {

        if (requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_ADD_CART_ITEM) {
            mController.setDiagnosticPackageAddedToCart(this.iProductIndex,true);
            mPlansAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnAddToCartClicked(int itemAtPosition) {

        this.iProductIndex = itemAtPosition;
        if (new InternetConnectionDetector(context).isConnected()) {

            DiagnosticsItem addToCart = mController.getPackageDetails(itemAtPosition);
            String cartItemAddURL = mController.getDiagnosticsAddToCartURL();

            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_CART_ADD_ITEM,
                    MyApplication.HTTPMethod.POST.getValue(),
                    DiagnosticsItem.composeUserJSON(addToCart),
                    DiagnosticsListingFragment.this).execute( cartItemAddURL );
        }
        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }
}
