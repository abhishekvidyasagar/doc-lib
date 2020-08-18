package com.doconline.doconline.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.diagnostics.DiagnosticsActivity;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnProcureMedicineListener;
import com.doconline.doconline.medPlus.AddressUpdateFragment;
import com.doconline.doconline.medPlus.MedicineFragment;
import com.doconline.doconline.medPlus.OrderReviewFragment;
import com.doconline.doconline.medPlus.OrderSuccessFragment;
import com.doconline.doconline.medPlus.PincodeFragment;
import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.Medicine;

import java.util.ArrayList;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;


public class AppointmentSummeryActivity extends BaseActivity implements
        OnProcureMedicineListener, View.OnClickListener
{
    public static final int HTTP_REQUEST_CODE_GET_APPOINTMENT = 1;
    public static final int HTTP_REQUEST_CODE_CANCEL_APPOINTMENT = 2;
    public static final int HTTP_REQUEST_CODE_GET_RATING = 3;
    public static final int HTTP_REQUEST_CODE_POST_RATING = 4;
    public static final int HTTP_REQUEST_CODE_CHECK_MEDICINE_AVAILABILITY = 5;
    public static final int HTTP_REQUEST_CODE_ORDER_MEDICINE = 6;
    public static final int HTTP_REQUEST_CODE_ATTACH_IMAGES = 7;
    public static final int HTTP_REQUEST_CODE_DELETE_ATTACHMENTS = 8;
    public static final int HTTP_REQUEST_CODE_GET_ORDER_STATUS = 9;


    private static boolean isActive = false;

    private int position = 0;
    private int appointment_id;
    private ArrayList<Medicine> mList;
    private Address mShippingAddress = new Address();
    private String mOrderId;



    Button btnProceed;
    Button btnBookDiag;
    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    TextView toolbar_title;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_summery);
      //  ButterKnife.bind(this);

       btnProceed =  findViewById(R.id.btnDone);
       btnBookDiag=  findViewById(R.id.btnBookDiag);

        layout_refresh=  findViewById(R.id.layout_refresh);
        layout_block_ui=  findViewById(R.id.layout_block_ui);
        toolbar_title=  findViewById(R.id.toolbar_title);
        toolbar=  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(!MyApplication.getInstance().getSession().checkLogin())
        {
            finish();
            return;
        }

        if(isActive)
        {
            finish();
            return;
        }

        this.appointment_id = getIntent().getIntExtra("ID", 0);
        hide_button();

        this.position = getIntent().getIntExtra("PAGE", 0);

        onPageSelection(this.position, this.getAppTitle(this.position));

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        isActive = false;
    }


    private void navigate_fragment(int index, String title)
    {
        toolbar_title.setText(title);
        this.position = index;
        Fragment fragment = null;

        switch (index)
        {
            case 0:

                btnProceed.setText("View Prescription");
                btnBookDiag.setVisibility(View.GONE);

                hide_button();
                fragment = new AppointmentSummeryFragment(this, this, this, this.appointment_id );
                break;

            case 1:

                btnProceed.setText("Procure Medicine");
                hide_button();
                fragment = new PrescriptionFragment(this, this, this, this.appointment_id);
                break;

            case 2:

                btnProceed.setVisibility(View.GONE);
                btnBookDiag.setVisibility(View.GONE);

                hide_button();
                fragment = new PincodeFragment(this, this, this.appointment_id);
                break;

            case 3:

                btnProceed.setVisibility(View.GONE);
                btnBookDiag.setVisibility(View.GONE);

                hide_button();
                fragment = new MedicineFragment(this, this, this.mList);
                break;

            case 4:

                btnProceed.setVisibility(View.GONE);
                btnBookDiag.setVisibility(View.GONE);

                hide_button();
                fragment = new AddressUpdateFragment(this, this, mShippingAddress);
                break;

            case 5:

                btnProceed.setVisibility(View.GONE);
                btnBookDiag.setVisibility(View.GONE);

                hide_button();
                fragment = new OrderReviewFragment(this, this, this, this.mList, this.appointment_id, this.mShippingAddress);
                break;

            case 6:

                btnProceed.setVisibility(View.GONE);
                btnBookDiag.setVisibility(View.GONE);

                hide_button();
                fragment = new OrderSuccessFragment(this.mOrderId);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, AppointmentSummeryActivity.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                onBackButtonPress();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void onBackButtonPress()
    {
        if(position == 6 || (getIntent().getIntExtra("PAGE", 0) == 2 && position == 2))
        {
            finish();
        }

        else
        {
            this.position--;

            if(this.position == -1)
            {
                finish();
            }

            else
            {
                onPageSelection(this.position, this.getAppTitle(position));
            }
        }
    }


    @Override
    public void onBackPressed()
    {
        onBackButtonPress();
    }


    private String getAppTitle(int index)
    {
        switch (index)
        {
            case 0:

                return "Appointment Summary";

            case 1:

                return "Prescription";

            case 2:

                return "Enter Pincode";

            case 3:

                return "Medicines";

            case 4:

                return "Enter Address";

            case 5:

                return "Order Confirmation";

            case  6:

                return "Order Success";

            default:

                return "";
        }
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnDone) {
            this.position++;
            onPageSelection(this.position, this.getAppTitle(this.position));
        } else if (id == R.id.btnBookDiag) {
            startActivity(new Intent(AppointmentSummeryActivity.this, DiagnosticsActivity.class));
        }
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_INTERNET)
        {
            finish();
        }
    }

    @Override
    public void onPositiveAction()
    {
        finish();
    }

    @Override
    public void onNegativeAction()
    {
        finish();
    }

    @Override
    public void onPageSelection(int position, String title)
    {
        navigate_fragment(position, title);
    }


    @Override
    public void onPreExecute()
    {
        if(isFinishing())
        {
           return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_refresh.setVisibility(View.VISIBLE);
                layout_block_ui.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if(isFinishing())
        {
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_refresh.setVisibility(View.INVISIBLE);
                layout_block_ui.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onMedicineFound(ArrayList<Medicine> medicines)
    {
        this.mList = medicines;
    }

    @Override
    public void onDeliveryAddress(Address mShippingAddress)
    {
        this.mShippingAddress.setPincode(mShippingAddress.getPincode());
        this.mShippingAddress.setAddress_1(mShippingAddress.getAddress_1());
        this.mShippingAddress.setAddress_2(mShippingAddress.getAddress_2());
        this.mShippingAddress.setPhoneNo(mController.getSession().getMobileNumber());
    }

    @Override
    public void onOrderId(String mOrderId)
    {
        this.mOrderId = mOrderId;
    }


    @Override
    public void onTaskCompleted(boolean flag, int index, String message)
    {
        if(flag)
        {
            show_button();
        }

        else
        {
            hide_button();
        }
    }


    public void show_button()
    {
        btnProceed.setVisibility(View.VISIBLE);
        //btnProceed.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public void hide_button()
    {
        btnProceed.setVisibility(View.GONE);
        //btnProceed.animate().translationY(btnProceed.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
    }

}