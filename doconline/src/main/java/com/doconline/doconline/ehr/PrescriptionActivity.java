package com.doconline.doconline.ehr;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.model.Appointment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_NEXT_PAGE_URL;


public class PrescriptionActivity extends BaseActivity
{

    ViewPager viewPager;

    TextView toolbar_title;

    Toolbar toolbar;

    RelativeLayout layout_refresh;

    public static final int HTTP_REQUEST_CODE = 0;

    private PagerAdapter mAdapter;

    private static int current_page;
    private String NEXT_PAGE_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
       // ButterKnife.bind(this);
        viewPager =  findViewById(R.id.pager);
        toolbar_title =  findViewById(R.id.toolbar_title);
        toolbar =  findViewById(R.id.toolbar);
        layout_refresh = findViewById(R.id.layout_refresh);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText("Prescriptions");

        current_page = getIntent().getIntExtra("INDEX", 0);
        NEXT_PAGE_URL = getIntent().getStringExtra("NEXT_PAGE_URL");

        this.initAdapter();
        this.addPagerListener();

        toolbar_title.setText("BOOKING ID: " + MyApplication.getInstance().prescriptionList.get(current_page).getPublicAppointmentId());
    }


    private void initAdapter()
    {
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem(current_page);
    }

    // on swipe, load next image
    private void addPagerListener()
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                toolbar_title.setText("BOOKING ID: " + MyApplication.getInstance().prescriptionList.get(position).getPublicAppointmentId());

                if(position == mController.prescriptionList.size() - 1 && !NEXT_PAGE_URL.isEmpty())
                {
                    if(!new InternetConnectionDetector(getApplicationContext()).isConnected())
                    {
                        Toast.makeText(getApplicationContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), PrescriptionActivity.this)
                            .execute(NEXT_PAGE_URL);
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


    private static class PagerAdapter extends FragmentPagerAdapter {

        private PagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public int getCount()
        {
            return MyApplication.getInstance().prescriptionList.size();
        }


        @Override
        public Fragment getItem(int position)
        {
            return SwipeFragment.newInstance(position, MyApplication.getInstance().prescriptionList.get(position).getAppointmentID());
        }
    }


    public static class SwipeFragment extends Fragment implements View.OnClickListener, OnHttpResponse {

        private WebView webView;
        private int position;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.swipe_prescription_fragment, container, false);

            webView = swipeView.findViewById(R.id.webView);

            Bundle bundle = getArguments();

            position = bundle.getInt("position");

            return swipeView;
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
        {
            super.onViewCreated(view, savedInstanceState);

            if(!MyApplication.getInstance().prescriptionList.get(current_page).getPrescription().isEmpty())
            {
                loadPrescription(MyApplication.getInstance().prescriptionList.get(current_page).getPrescription());
            }

            this.sync(current_page);
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser)
        {
            super.setUserVisibleHint(isVisibleToUser);

            if (getActivity() != null && getView() != null && getView().isShown() && isVisibleToUser)
            {
                if(!MyApplication.getInstance().prescriptionList.get(position).getPrescription().isEmpty())
                {
                    loadPrescription(MyApplication.getInstance().prescriptionList.get(position).getPrescription());
                }

                sync(position);
            }
        }


        static SwipeFragment newInstance(int position, int appointment_id) {

            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putInt("appointment_id", appointment_id);
            swipeFragment.setArguments(bundle);

            return swipeFragment;
        }

        @Override
        public void onClick(View v)
        {

        }

        @Override
        public void onPreExecute() {

            ((PrescriptionActivity) getActivity()).onPreExecute();
        }

        @Override
        public void onPostExecute(int requestCode, int responseCode, String response)
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
                if(responseCode == HttpClient.OK)
                {
                    int index = Appointment.getPrescriptionIndex(MyApplication.getInstance().prescriptionList, requestCode);

                    if(index != -1)
                    {
                        MyApplication.getInstance().prescriptionList.get(index).setPrescription(response);
                        loadPrescription(String.valueOf(response));
                    }
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                ((PrescriptionActivity)getActivity()).onPostExecute(requestCode, responseCode, response);
            }
        }


        private void loadPrescription(String html_data)
        {
            try
            {
                if(webView.getTag() != null)
                {
                    return;
                }

                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.loadDataWithBaseURL(null, "<html><body>" + html_data + "</body></html>", "text/html", "utf-8", null);

                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon)
                    {

                    }

                    @Override
                    public void onPageFinished(WebView view, String url)
                    {
                        webView.setTag(url);
                    }
                });
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        private void syncAppointment(int appointment_id, int index)
        {
            if(!MyApplication.getInstance().prescriptionList.get(index).getPrescription().isEmpty())
            {
                return;
            }

            if(!new InternetConnectionDetector(getContext()).isConnected())
            {
                Toast.makeText(getContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i("PRESCRIPTION_ID", MyApplication.getInstance().prescriptionList.get(index).getPublicAppointmentId());

            String URL = MyApplication.getInstance().getPrescriptionURL() + appointment_id;

            new HttpClient(appointment_id, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL);
        }

        private void sync(int current_page)
        {
            if(position != current_page)
            {
                return;
            }

            syncAppointment(MyApplication.getInstance().prescriptionList.get(current_page).getAppointmentID(), current_page);

            if(current_page != 0)
            {
                int index = current_page-1;
                syncAppointment(MyApplication.getInstance().prescriptionList.get(index).getAppointmentID(), index);
            }

            if(current_page+1 <= MyApplication.getInstance().prescriptionList.size()-1)
            {
                int index = current_page+1;
                syncAppointment(MyApplication.getInstance().prescriptionList.get(index).getAppointmentID(), index);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPreExecute() {

        layout_refresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (isFinishing())
        {
            return;
        }

        if(requestCode == HTTP_REQUEST_CODE && responseCode == HttpClient.OK)
        {
            this.appointmentJSON(response);
        }

        layout_refresh.setVisibility(View.INVISIBLE);
    }


    private void appointmentJSON(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);
            json = json.getJSONObject(KEY_DATA);

            NEXT_PAGE_URL = json.isNull(KEY_NEXT_PAGE_URL) ? "" : json.getString(KEY_NEXT_PAGE_URL);

            mController.prescriptionList.addAll(Appointment.getPrescriptionListFromJSON(json.getString(KEY_DATA)));
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            mAdapter.notifyDataSetChanged();
        }
    }
}