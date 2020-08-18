package com.doconline.doconline.appointment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnTaskCompleted;


public class PrescriptionFragment extends BaseFragment
{

    WebView webView;

    private int appointment_id;
    private Context context;

    private OnHttpResponse listener;
    private OnTaskCompleted task_listener;
    private static final int HTTP_REQUEST_CODE = 1;


    public PrescriptionFragment()
    {

    }
    
    @SuppressLint("ValidFragment")
    public PrescriptionFragment(Context context, OnHttpResponse listener, OnTaskCompleted task_listener, int appointment_id)
    {
        this.context = context;
        this.listener = listener;
        this.task_listener = task_listener;
        this.appointment_id = appointment_id;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_prescription, container, false);
       // ButterKnife.bind(this, view);
        webView =  view.findViewById(R.id.webView);
        return  view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(new InternetConnectionDetector(context).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getPrescriptionURL() + appointment_id);
        }
    }


    @Override
    public void onPreExecute()
    {
        listener.onPreExecute();
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
            if(requestCode == HTTP_REQUEST_CODE && responseCode == HttpClient.OK)
            {
                task_listener.onTaskCompleted(true, 0, "");
                loadPrescription(String.valueOf(response));
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
            listener.onPostExecute(requestCode, responseCode, response);
        }
    }


    private void loadPrescription(String html_data)
    {
        try
        {
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL(null, "<html><body>" + html_data + "</body></html>", "text/html", "utf-8", null);

            if (html_data.contains("<div style=\"display: inline-block;width: 100%;margin-bottom: 5px;box-sizing: border-box;-moz-box-sizing: border-box;-webkit-box-sizing: border-box;border: 1px solid #f3f3f3;padding: 10px; padding-bottom:2px;\">")){
                ((AppointmentSummeryActivity) getActivity()).btnProceed.setVisibility(View.VISIBLE);
            }else {
                ((AppointmentSummeryActivity) getActivity()).btnProceed.setVisibility(View.GONE);

            }

            if (html_data.contains("Lab Tests:")){
                ((AppointmentSummeryActivity) getActivity()).btnBookDiag.setVisibility(View.VISIBLE);

            }else {
                ((AppointmentSummeryActivity) getActivity()).btnBookDiag.setVisibility(View.GONE);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPositiveAction()
    {

    }

    @Override
    public void onNegativeAction()
    {

    }
}