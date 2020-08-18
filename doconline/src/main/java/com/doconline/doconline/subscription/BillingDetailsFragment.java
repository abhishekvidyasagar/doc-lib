package com.doconline.doconline.subscription;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.SubscriptionLineItemRecyclerAdapter;
import com.doconline.doconline.api.FileDownloadClient;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.OnDownloadProgress;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.SubscriptionBilling;
import com.doconline.doconline.model.SubscriptionPlan;
import com.doconline.doconline.utils.FileUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DOWNLOAD_URL;
import static com.doconline.doconline.subscription.BillingHistoryActivity.HTTP_REQUEST_CODE_DOWNLOAD_BILL;
import static com.doconline.doconline.subscription.BillingHistoryActivity.HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL_DETAILS;
import static com.doconline.doconline.subscription.BillingHistoryActivity.index;
import static com.doconline.doconline.subscription.BillingHistoryActivity.mBillings;

/**
 * Created by chiranjitbardhan on 07/12/17.
 */

public class BillingDetailsFragment extends BaseFragment implements OnDownloadProgress
{
    public static final int PERMISSION_REQUEST_CODE = 1;


    RecyclerView mRecyclerView;

    TextView invoice_no;

    TextView issued_on;

    TextView billing_period;

    TextView customer_name;

    TextView customer_email;

    LinearLayout layout_billing_period;

    TextView total_amount;

    TextView amount_due;

    TextView amount_paid;

    TextView discount_total;

    TextView status;

    RelativeLayout layout_download_progress;

    ProgressBar circularProgressBar;

    TextView download_percent;

    private SubscriptionLineItemRecyclerAdapter mAdapter;
    private List<SubscriptionPlan> itemList = new ArrayList<>();
    private String download_url = "";


    public static BillingDetailsFragment newInstance()
    {
        return new BillingDetailsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_billing_details, container, false);
       // ButterKnife.bind(this, view);
        mRecyclerView = view.findViewById(R.id.list);
         invoice_no= view.findViewById(R.id.invoice_no);
        issued_on= view.findViewById(R.id.issued_on);
         billing_period= view.findViewById(R.id.billing_period);
        customer_name= view.findViewById(R.id.customer_name);
         customer_email= view.findViewById(R.id.customer_email);
         layout_billing_period= view.findViewById(R.id.layout_billing_period);
         total_amount= view.findViewById(R.id.amount_total);
         amount_due= view.findViewById(R.id.amount_due);
         amount_paid= view.findViewById(R.id.amount_paid);
        discount_total= view.findViewById(R.id.discount_total);
         status= view.findViewById(R.id.status);
        layout_download_progress= view.findViewById(R.id.layout_download_progress);
       circularProgressBar= view.findViewById(R.id.progressBar2);
        download_percent= view.findViewById(R.id.download_percent);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.setHasOptionsMenu(true);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if(getView() != null && isVisibleToUser && index != -1)
        {
            this.itemList.clear();
            this.itemList.addAll(mBillings.get(index).getItemList());
            this.display_details(mBillings.get(index));
            download_url = "";
        }
    }


    private void display_details(SubscriptionBilling billing)
    {
        if(getContext() == null)
        {
            return;
        }

        invoice_no.setText("Invoice No. " + billing.getInvoiceId());
        total_amount.setText("Total Amount  " + getContext().getResources().getString(R.string.Rs) + " " + Constants.df.format(billing.getAmount()));
        amount_due.setText("Amount Due  " + getContext().getResources().getString(R.string.Rs) + " " + Constants.df.format(billing.getAmountDue()));
        amount_paid.setText("Amount Paid  " + getContext().getResources().getString(R.string.Rs) + " " + Constants.df.format(billing.getAmountPaid()));

        if(billing.getDiscount() > 0)
        {
            discount_total.setVisibility(View.VISIBLE);
            discount_total.setText("Discount  " + getContext().getResources().getString(R.string.Rs) + " " + Constants.df.format(billing.getDiscount()));
        }

        else
        {
            discount_total.setVisibility(View.GONE);
        }

        status.setText(billing.getStatus().toUpperCase());
        customer_name.setText(Helper.toCamelCase(billing.getCustomerName()));
        customer_email.setText(billing.getCustomerEmail());

        if(billing.getStatus().equalsIgnoreCase("paid"))
        {
            status.setTextColor(ContextCompat.getColor(getContext(), R.color.light_green));
        }

        else
        {
            status.setTextColor(ContextCompat.getColor(getContext(), R.color.myTextSecondaryColor));
        }

        if(!billing.getIssuedAt().isEmpty())
        {
            issued_on.setVisibility(View.VISIBLE);

            try
            {
                Date date = new Date((Long.valueOf(billing.getIssuedAt()) * 1000L));
                issued_on.setText(new SimpleDateFormat(Constants.MMM_DD_YYYY).format(date));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            issued_on.setVisibility(View.GONE);
        }

        if(!billing.getBillingStartAt().isEmpty() && !billing.getBillingEndAt().isEmpty())
        {
            layout_billing_period.setVisibility(View.VISIBLE);

            try
            {
                Date start = new Date((Long.valueOf(billing.getBillingStartAt()) * 1000L));
                Date end = new Date((Long.valueOf(billing.getBillingEndAt()) * 1000L));
                SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MM_YY);

                String invoice_billing_period = formatter.format(start) + " - " + formatter.format(end);
                billing_period.setText(invoice_billing_period);
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            layout_billing_period.setVisibility(View.GONE);
        }

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(mAdapter == null)
        {
            mAdapter = new SubscriptionLineItemRecyclerAdapter(getActivity(), itemList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setNestedScrollingEnabled(false);
        }

        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_file_download, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_save) {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (new InternetConnectionDetector(getContext()).isConnected()) {
                    new HttpClient(HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL_DETAILS, MyApplication.HTTPMethod.GET.getValue(), this)
                            .execute(mController.getBillingURL() + mBillings.get(index).getInvoiceId());
                } else {
                    Toast.makeText(getContext(), "Internet Not Connected", Toast.LENGTH_LONG).show();
                }
            } else {
                RequestMultiplePermission();
            }
        }

        return super.onOptionsItemSelected(item);
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

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_download_progress.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onProgressUpdate(final String... progress)
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

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                circularProgressBar.setProgress(Integer.parseInt(progress[0]));
                download_percent.setText(progress[0] + "%");
            }
        });
    }


    @Override
    public void onPostExecute(final int requestCode, int responseCode, String response)
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

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL_DETAILS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);
                    download_url = (json.isNull(KEY_DOWNLOAD_URL)) ? "" : json.getString(KEY_DOWNLOAD_URL);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    onTaskCompleted(true, 0, mBillings.get(index).getInvoiceId() + ".pdf");
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_DOWNLOAD_BILL && responseCode == HttpClient.OK)
            {
                circularProgressBar.setProgress(0);
                download_percent.setText(0 + "%");

                String file_name = mBillings.get(index).getInvoiceId() + ".pdf";
                FileUtils.openFileBrowser(getActivity(), file_name);
                return;
            }

            Toast.makeText(getContext(), "Failed to Download", Toast.LENGTH_LONG).show();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    if(requestCode != HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL_DETAILS)
                    {
                        layout_download_progress.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String file_name)
    {
        download(download_url, file_name);
    }


    public void download(String URL, String file_name)
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            if(!URL.isEmpty())
            {
                FileDownloadClient client = new FileDownloadClient(HTTP_REQUEST_CODE_DOWNLOAD_BILL, this);
                client.execute(URL, file_name);
            }

            else
            {
                Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_LONG).show();
            }
        }

        else
        {
            Toast.makeText(getContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission()
    {
        /**
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0)
                {
                    boolean ReadStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (ReadStoragePermission && WriteStoragePermission)
                    {
                        Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(getContext(), "Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    /**
     * Checking permission is enabled or not
     */
    public boolean CheckingPermissionIsEnabledOrNot()
    {
        int WriteStoragePermissionResult = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int ReadStoragePermissionResult = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);

        return WriteStoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ReadStoragePermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}