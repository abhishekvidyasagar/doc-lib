package com.doconline.doconline.diagnostics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.doconline.doconline.Paytm.checksum;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_ERROR;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_PAYMENT_ERROR;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_FRAGMENT_INDEX;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX;

/**
 * Created by admin on 2018-02-26.
 */

public class DiagnosticsActivity extends BaseActivity implements View.OnClickListener,
        PaymentResultListener, OnDialogAction,
        DiagnosticsBookAppointmentFragment.OnDiagnosticsAppointmentBookingStatusListener,
        SearchView.OnQueryTextListener, View.OnFocusChangeListener,
        SearchView.OnCloseListener, TextView.OnEditorActionListener,
        OnLoadingStatusChangedListener,ExternalWalletListener {



    RelativeLayout layout_root_view;


    Toolbar toolbar;


    TextView toolbar_title;

    //@BindView(R.id.layout_refresh)
    //RelativeLayout layout_refresh;


    ConstraintLayout layout_DiagnosticsFooter;


    Button btnDiagnosticsAddToCart;


    Button btnBookAppointment;


    RelativeLayout layout_diagnostics_terms_and_conditions;


    TextView tv_diagnostics_date_of_consultation;


    TextView tv_appointment_date_;


    TextView tv_diagnostics_patient_name;


    TextView tv_diagnostics_phone_number;


    WebView diagnostics_webView;


    MyViewPager mViewPager;


    SearchView search_diagnostics;


    ImageButton imgButton_HowItWorks;


    LinearLayout layout_search;


    RelativeLayout layout_progress;

    private int cartBadgeCount = 0;
    static int iFragmentToLoad = 0;

    private MenuItem cartMenuItem;

    private SectionsPagerAdapter adapter;

    DiagnosticsListingFragment diagnosticsListingFragment;
    DiagnosticsProductDetailsFragment diagnosticsProductDetailsFragment;
    DiagnosticsBookAppointmentFragment diagnosticsBookAppointmentFragment;
    DiagnosticsAppointmentConfirmationFragment diagnosticsAppointmentConfirmationFragment;
    DiagnosticsCartListingFragment diagnosticsCartListingFragment;

    private boolean isNavigatedFromDiagnosticsListing = false;
    private boolean isNavigatedFromDiagnosticsProductDetails = false;
    //ProgressDialog mDialog;

    public static final int PAYTM_PAYMENT_REQUEST_CODE = 11;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);
      //  ButterKnife.bind(this);
         layout_root_view =  findViewById(R.id.layout_root_view);

         toolbar=  findViewById(R.id.toolbar);

         toolbar_title=  findViewById(R.id.toolbar_title);

        //@BindView(R.id.layout_refresh)
        //RelativeLayout layout_refresh;

         layout_DiagnosticsFooter=  findViewById(R.id.layout_diagnostics_footer);

         btnDiagnosticsAddToCart=  findViewById(R.id.btnAddToCart);

         btnBookAppointment=  findViewById(R.id.btnBookAppointment);

         layout_diagnostics_terms_and_conditions=  findViewById(R.id.layout_diagnostics_terms_and_conditions);

         tv_diagnostics_date_of_consultation=  findViewById(R.id.tv_diagnostics_date_of_consultation);

         tv_appointment_date_=  findViewById(R.id.tv_appointment_date_);

        tv_diagnostics_patient_name=  findViewById(R.id.tv_diagnostics_patient_name);

         tv_diagnostics_phone_number=  findViewById(R.id.tv_diagnostics_phone_number);

        diagnostics_webView=  findViewById(R.id.diagnostics_webView);

         mViewPager=  findViewById(R.id.pager_Diagnostics);

         search_diagnostics=  findViewById(R.id.search_diagnostics);

         imgButton_HowItWorks=  findViewById(R.id.imgBtn_HowItWorks);

        layout_search=  findViewById(R.id.layout_search);

        layout_progress=  findViewById(R.id.layout_loading);


        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnDiagnosticsAddToCart.setOnClickListener(this);
        btnBookAppointment.setOnClickListener(this);

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
            return;
        }
        /*Expanding the search view */
        search_diagnostics.setIconified(true);
        search_diagnostics.setIconifiedByDefault(false);


        try {
            int searchTextId = search_diagnostics.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = search_diagnostics.findViewById(searchTextId);
            textView.setTextColor(Color.BLACK);
            textView.setHintTextColor(Color.LTGRAY);

            textView.setOnEditorActionListener(this);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //search_diagnostics.setOnClickListener(this);
        search_diagnostics.setOnQueryTextListener(this);
        search_diagnostics.setOnQueryTextFocusChangeListener(this);
        search_diagnostics.setOnCloseListener(this);

        imgButton_HowItWorks.setOnClickListener(this);

        //Get the fragment index
        iFragmentToLoad = getIntent().getIntExtra(KEY_DIAGNOSTICS_FRAGMENT_INDEX,0);

        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setPagingEnabled(false);

        setupViewPager(mViewPager);

        Checkout.preload(getApplicationContext());

    }

    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            UpdateCartCount();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    private void UpdateCartCount(){

        this.cartBadgeCount = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_CART_COUNT, -1);
        invalidateOptionsMenu();
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {

        getMenuInflater().inflate(R.menu.menu_diagnostics_cart, paramMenu);
        cartMenuItem = paramMenu.findItem(R.id.action_item_cart);

        if(cartBadgeCount > 0){
            ActionItemBadge.update(this,paramMenu.findItem(R.id.action_item_cart), FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED, cartBadgeCount);
        }
        else{
            ActionItemBadge.update(this,paramMenu.findItem(R.id.action_item_cart), FontAwesome.Icon.faw_shopping_cart, Integer.MIN_VALUE);
        }

        if(mViewPager != null) {
            if (iFragmentToLoad == 4) {
                isNavigatedFromDiagnosticsListing = false;
                isNavigatedFromDiagnosticsProductDetails = false;
                mViewPager.setCurrentItem(iFragmentToLoad);
            }
        }

        return super.onCreateOptionsMenu(paramMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_item_cart) {
            if (diagnosticsCartListingFragment != null) {

                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        isNavigatedFromDiagnosticsListing = true;

                        isNavigatedFromDiagnosticsProductDetails = false;
                        break;
                    case 1:
                        isNavigatedFromDiagnosticsListing = false;
                        isNavigatedFromDiagnosticsProductDetails = true;
                        break;
                }

                onPageSelection(4, "Diagnostics Cart");
            }
            return true;
        } else if (id == android.R.id.home) { //Back arrow pressed
            this.onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if(View.VISIBLE == layout_diagnostics_terms_and_conditions.getVisibility()){
            layout_diagnostics_terms_and_conditions.setVisibility(View.GONE);
            layout_DiagnosticsFooter.setVisibility(View.VISIBLE);
            onPageSelection(2, "Book Appointment");

            // return;
        }
        else {

            if (mViewPager.getCurrentItem() == 0) //Diagnostics Listing
            {
                super.onBackPressed();
                this.finish();
            } else if (mViewPager.getCurrentItem() == 1) { //Diagnostics Product Details
                onPageSelection(0, "Diagnostics");
                UpdateCartCount();
            } else if (mViewPager.getCurrentItem() == 2) { //Diagnostics
                if (DiagnosticsBookAppointmentFragment.isBookedFromCartFragment) {
                    DiagnosticsBookAppointmentFragment.isBookedFromCartFragment = false;
                    onPageSelection(4, "Diagnostics Cart");
                } else {
                    onPageSelection(1, "Package Details");
                    UpdateCartCount();
                }
            } else if (mViewPager.getCurrentItem() == 3) {
                onPageSelection(2, "Book Appointment");
            } else if (mViewPager.getCurrentItem() == 4) {
                if (4 == iFragmentToLoad) {
                    this.finish();
                } else {
                    if (isNavigatedFromDiagnosticsListing)
                        onPageSelection(0, "Diagnostics");
                    else if (isNavigatedFromDiagnosticsProductDetails)
                        onPageSelection(1, "Package Details");
                    UpdateCartCount();
                }
            }

        }
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

        switch(requestCode){
            case DiagnosticsCartListingFragment.HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE:
                diagnosticsListingFragment.mPlansAdapter.notifyDataSetChanged();
            break;
            case DiagnosticsListingFragment.HTTP_REQUEST_CODE_DIAGNOSTICS_CART_ADD_ITEM:
            case DiagnosticsProductDetailsFragment.HTTP_REQUEST_CODE_DIAGNOSTICS_PRODUCT_ADD_ITEM_TO_CART:
                if(HttpClient.OK == responseCode){
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            UpdateCartCount();
                        }
                    });
                }
                break;
        }
    }

    private void LoadDiagnosticsTermsAndConditions()
    {
        String date = "";

        try {

            layout_DiagnosticsFooter.setVisibility(View.GONE);
            toolbar_title.setText("Terms and Conditions");


            layout_diagnostics_terms_and_conditions.setVisibility(View.VISIBLE);

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
            date = sdf.format(System.currentTimeMillis());

            tv_diagnostics_patient_name.setText(diagnosticsBookAppointmentFragment.getUserName());

            tv_diagnostics_date_of_consultation.setText(date);


            String dateeee = sdf.format(diagnosticsBookAppointmentFragment.selectedSchedule.getTime());
            String dateOfAppointment = dateeee.substring(0,dateeee.length()-9) + " " +diagnosticsBookAppointmentFragment.tv_BookAppt_Time.getText().toString()+":00";
            tv_appointment_date_.setText(dateOfAppointment);


            tv_diagnostics_phone_number.setText(String.valueOf(diagnosticsBookAppointmentFragment.getUserMobileNumber()));

            diagnostics_webView.loadUrl("file:///android_asset/Diagnostics_Cancellation_Terms_and_Conditions.html");
        }

        catch (Exception e)
        {
            toolbar_title.setText("Book Appointment");
            //toolbar.getMenu().findItem(R.menu.home).setVisible(true);
            btnDiagnosticsAddToCart.setVisibility(View.GONE);
            layout_DiagnosticsFooter.setVisibility(View.VISIBLE);
            layout_diagnostics_terms_and_conditions.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //hideInputMethods(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {


        if (newText.length() >= 3) {
            if (diagnosticsListingFragment != null) {
                diagnosticsListingFragment.currentSearchString(newText);
            }
        }
        else if (newText.length() == 0)
        {
            if (diagnosticsListingFragment != null)
            {
                diagnosticsListingFragment.currentSearchString(newText);
            }
            //hideInputMethods(newText);
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // do something when the focus of the query text field changes

        if (hasFocus)
        {
            showInputMethod(v.findFocus());
        }

    }

    @Override
    public boolean onClose() {

        //hideInputMethods();
        Log.v("onClose","onClose");
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            Log.v("TextView", String.valueOf(v.getText()));
            String str = String.valueOf(v.getText());

            if (str.length() < 3)
            {
                if (diagnosticsListingFragment != null)
                {
                    diagnosticsListingFragment.currentSearchString(str);
                }
            }
            //hideInputMethods(str);
        }
        return false;
    }

    private void showInputMethod(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {imm.showSoftInput(view,0);}
    }

    /*private void hideInputMethods(String str)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {imm.hideSoftInputFromWindow(search_diagnostics.getWindowToken(),0);}
        //search_diagnostics.onActionViewCollapsed();
        clearSearchFocus();
    }*/

    /*private void clearSearchFocus()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                search_diagnostics.clearFocus();
            }
        }, 300);
    }*/

    @Override
    public void onAppointmentConfirmed(String appointmentID, String packageName, String dateOfAppointment, Boolean isFromCart) {
        if(diagnosticsAppointmentConfirmationFragment != null){
            diagnosticsAppointmentConfirmationFragment.fillConfirmationDetails(appointmentID,packageName,dateOfAppointment,isFromCart);
            onPageSelection(3, "Thank you");
        }
    }

    @Override
    public void showLoadingActivity() {
        //layout_progress.setVisibility(View.VISIBLE);
        //layout_block_ui.setVisibility(View.VISIBLE);
        //mDialog.show();
        layout_progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressbarWithSuccess() {
        //layout_progress.setVisibility(View.GONE);
        //layout_block_ui.setVisibility(View.GONE);
        //mDialog.dismiss();
        layout_progress.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressbarWithFailure() {

    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(MyViewPager viewPager)
    {

        final ViewPager vp = findViewById(R.id.pager_Diagnostics);
        final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vp.getLayoutParams();
        lp.setMargins(0,0,0,0);
        vp.setLayoutParams(lp);

        layout_DiagnosticsFooter.setVisibility(View.GONE);
        toolbar_title.setText(getResources().getString(R.string.text__book_diagnostics));
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        diagnosticsListingFragment = new DiagnosticsListingFragment(this,this,this,this);
        diagnosticsProductDetailsFragment = new DiagnosticsProductDetailsFragment(this,this,this);
        diagnosticsCartListingFragment = new DiagnosticsCartListingFragment(this,this,this,this);
        diagnosticsBookAppointmentFragment = new DiagnosticsBookAppointmentFragment(this,this, this, this);
        diagnosticsAppointmentConfirmationFragment = new DiagnosticsAppointmentConfirmationFragment(this,this);

        adapter.addFrag(diagnosticsListingFragment, "Diagnostics");
        adapter.addFrag(diagnosticsProductDetailsFragment,"Diagnostics Details");
        adapter.addFrag(diagnosticsBookAppointmentFragment,"Book Appointment");
        adapter.addFrag(diagnosticsAppointmentConfirmationFragment, "Thank You");
        adapter.addFrag(diagnosticsCartListingFragment,"Diagnostics Cart");

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position)
            {
                diagnosticsListingFragment.getView().setVisibility(View.VISIBLE);
                diagnosticsProductDetailsFragment.getView().setVisibility(View.VISIBLE);
                diagnosticsBookAppointmentFragment.getView().setVisibility(View.VISIBLE);
                diagnosticsAppointmentConfirmationFragment.getView().setVisibility(View.VISIBLE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                switch (position)
                {
                    case 0: //Diagnostics product listing
                        //Show the cart
                        if(cartMenuItem != null)
                            cartMenuItem.setVisible(true);

                    //Set the view pager height
                        lp.setMargins(0,0,0,0);
                        vp.setLayoutParams(lp);

                        layout_DiagnosticsFooter.setVisibility(View.GONE);
                        layout_search.setVisibility(View.VISIBLE);
                        if (diagnosticsListingFragment != null && mController.getDiagnosticsCartItemListCount() > 0)
                        {
                            diagnosticsListingFragment.refreshDiagnosticsList();
                        }
                        break;

                    case 1: //Diagnostics product details
                        //Show the cart
                        if(cartMenuItem != null)
                            cartMenuItem.setVisible(true);

                        //Set the view pager height
                        lp.setMargins(0,0,0,55);
                        vp.setLayoutParams(lp);

                        btnDiagnosticsAddToCart.setVisibility(View.VISIBLE);
                        layout_DiagnosticsFooter.setVisibility(View.VISIBLE);
                        layout_search.setVisibility(View.GONE);
                        if( diagnosticsProductDetailsFragment != null)
                        {
                            diagnosticsProductDetailsFragment.LoadPackageDetails();
                        }
                        break;
                    case 2://Diagnostics Book appointment

                        //Set the view pager height
                        lp.setMargins(0,0,0,55);
                        vp.setLayoutParams(lp);
                        //Hide the cart
                        if(cartMenuItem != null) {
                            ActionItemBadge.hide(cartMenuItem);
                        }

                        btnDiagnosticsAddToCart.setVisibility(View.GONE);
                        layout_DiagnosticsFooter.setVisibility(View.VISIBLE);
                        layout_search.setVisibility(View.GONE);

                        if( diagnosticsBookAppointmentFragment != null && mController.getDiagnosticsPackagesCount() > 0)
                        {
                            diagnosticsBookAppointmentFragment.LoadFormWithDefaultAddress();
                        }
                        break;
                    case 3://Diagnostics Appointment confirmed
                        //Set the view pager height
                        lp.setMargins(0,0,0,0);
                        vp.setLayoutParams(lp);

                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                        layout_search.setVisibility(View.GONE);
                        layout_DiagnosticsFooter.setVisibility(View.INVISIBLE);

                        //Hide the cart
                        if(cartMenuItem != null) {
                            ActionItemBadge.hide(cartMenuItem);
                        }
                        break;
                    case 4://Diagnostics Cart Item listing
                        //Set the view pager height
                        lp.setMargins(0,0,0,0);
                        vp.setLayoutParams(lp);

                        layout_search.setVisibility(View.GONE);
                        layout_DiagnosticsFooter.setVisibility(View.INVISIBLE);
                        diagnosticsListingFragment.getView().setVisibility(View.GONE);
                        diagnosticsProductDetailsFragment.getView().setVisibility(View.GONE);
                        diagnosticsBookAppointmentFragment.getView().setVisibility(View.GONE);
                        diagnosticsAppointmentConfirmationFragment.getView().setVisibility(View.GONE);

                        //Hide the cart
                        if(cartMenuItem != null) {
                            ActionItemBadge.hide(cartMenuItem);
                        }
                        toolbar_title.setText("Diagnostics Cart");

                        if (diagnosticsCartListingFragment != null)
                        {
                            diagnosticsCartListingFragment.syncDiagnosticsCartData();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPageSelection(int position, String title)
    {
        mViewPager.setCurrentItem(position);
        toolbar_title.setText(title);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btn_diagnostics_Accept) {//Book appointment - Launch payment gateway.
            if (diagnosticsBookAppointmentFragment != null) {

                toolbar_title.setText("Book Appointment");
                //toolbar.getMenu().findItem(R.menu.home).setVisible(true);
                btnDiagnosticsAddToCart.setVisibility(View.GONE);
                layout_DiagnosticsFooter.setVisibility(View.VISIBLE);
                layout_diagnostics_terms_and_conditions.setVisibility(View.GONE);
                diagnosticsBookAppointmentFragment.bookAppointment();
            }
        } else if (id == R.id.btn_diagnostics_Decline) {
            toolbar_title.setText("Book Appointment");
            //toolbar.getMenu().findItem(R.menu.home).setVisible(true);
            btnDiagnosticsAddToCart.setVisibility(View.GONE);
            layout_DiagnosticsFooter.setVisibility(View.VISIBLE);
            layout_diagnostics_terms_and_conditions.setVisibility(View.GONE);
        } else if (id == R.id.btnAddToCart) {//Inside product details page of Diagnostics
            if (mViewPager.getCurrentItem() == 1) { //Adding to cart from Product details screen
                //Get the product ID to add it to the cart
                int iProductIndex = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, -1);
                if (iProductIndex != -1) {
                    if (iProductIndex == 994863253) {
                        Toast.makeText(this, "This Item cannot be added to cart", Toast.LENGTH_SHORT).show();
                    } else {
                        diagnosticsProductDetailsFragment.AddProductItemToCart();
                    }
                }
            }
        } else if (id == R.id.btnBookAppointment) {
            if (mViewPager.getCurrentItem() == 1) { //Navigate to Book appointment details screen
                onPageSelection(2, "Book Appointment");
            } else if (mViewPager.getCurrentItem() == 2) {
                if (true == diagnosticsBookAppointmentFragment.validateAppointmentForm()) {
                    LoadDiagnosticsTermsAndConditions();
                }
            }
        } else if (id == R.id.search_diagnostics) {
            search_diagnostics.onActionViewExpanded();
        } else if (id == R.id.imgBtn_HowItWorks) {
            if (mViewPager.getCurrentItem() == 0) {
                diagnosticsListingFragment.showHowItWorksDialog();
            }
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        if( diagnosticsBookAppointmentFragment != null)
        {
            diagnosticsBookAppointmentFragment.OnRazorpayPaymentSuccess(razorpayPaymentID);
        }
    }

    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {

        if (s.equalsIgnoreCase("paytm")) {
            Intent i = new Intent(DiagnosticsActivity.this, checksum.class);
            i.putExtra("orderid",diagnosticsBookAppointmentFragment.appointment_id);
            i.putExtra("url",mController.getCheckSumURL());
            i.putExtra("email",paymentData.getUserEmail());
            i.putExtra("phone",paymentData.getUserContact());
            i.putExtra("amount",diagnosticsBookAppointmentFragment.amt);
            i.putExtra("userid", DiagnosticsBookAppointmentFragment.user_id);

            startActivityForResult(i, PAYTM_PAYMENT_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PAYTM_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle requiredValue = data.getBundleExtra("paytmresponse");
                if (requiredValue == null) {
                    Toast.makeText(this, "something went wrong contact support", Toast.LENGTH_SHORT).show();
                }

                if (requiredValue.get("STATUS").toString().equalsIgnoreCase("TXN_FAILURE")){
                    new CustomAlertDialog(this, DIALOG_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_ERROR, this)
                            .showAlertDialogWithPositiveAction(getResources().getString(R.string.dialog_title_diagnostics_book_appointment_error),
                                    "payment failed",
                                    getResources().getString(R.string.OK), false);
                }
                if (requiredValue.get("STATUS").toString().equalsIgnoreCase("TXN_SUCCESS")){//check this key once later
                    Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show();
                    String razorpay_payment_id = requiredValue.get("TXNID").toString();

                    diagnosticsBookAppointmentFragment.OnRazorpayPaymentSuccess(razorpay_payment_id);

                    String amt = requiredValue.get("TXNAMOUNT").toString();

                    Log.d("AAA","this amount is not tracking frm thyrocare "+amt);


                }

            }
        }
    }

    @Override
    public void onPaymentError(int i, String errorMessage) {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_DIAGNOSTICS_PAYMENT_ERROR, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_diagnostics_payment_error),
                        errorMessage,
                        getResources().getString(R.string.Retry),
                        getResources().getString(R.string.Cancel), false);
    }

    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_PAYMENT_ERROR) // For Retry button clicked.
        {
            LoadDiagnosticsTermsAndConditions();
        }
    }

    @Override
    public void onNegativeAction(int requestCode) {

        if(requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_PAYMENT_ERROR)
        {
            this.onBackPressed();
        }
    }

}
