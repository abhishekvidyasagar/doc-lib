package com.doconline.doconline.consultation;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.MultipartFileUploadClient;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.floating.Utils;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.service.AlarmReceiver;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_OVERLAY_PERMISSION;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.consultation.BookConsultationActivity.HTTP_REQUEST_CODE_ATTACH_IMAGES;
import static com.doconline.doconline.consultation.BookConsultationActivity.HTTP_REQUEST_CODE_GET_APPOINTMENT;

/**
 * Created by chiranjit on 25/04/17.
 */
public class AppointmentConfirmationFragment extends BaseFragment
        implements SingleUploadBroadcastReceiver.Delegate, OnItemDeleted
{

    TextView tv_patient_name;
    TextView tv_consultation_date;
    TextView tv_doctor_name;
    ImageView doctor_image;
    TextView tv_specialization;
    TextView tv_doctor_rating;
    RatingBar doctor_rating_star;

    private Appointment mAppointment;
    private Context context = null;
    private OnHttpResponse listener;

    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();


    public AppointmentConfirmationFragment()
    {

    }


    @SuppressLint("ValidFragment")
    public AppointmentConfirmationFragment(Context context, OnHttpResponse listener, Appointment mAppointment)
    {
        this.context = context;
        this.listener = listener;
        this.mAppointment = mAppointment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_appointment_confirmation, container, false);
        //ButterKnife.bind(this, view);

        tv_patient_name = view.findViewById(R.id.tv_patient_name);
        tv_consultation_date = view.findViewById(R.id.tv_consultation_date);
        tv_doctor_name = view.findViewById(R.id.doctor_name);
        doctor_image = view.findViewById(R.id.doctor_image);
        tv_specialization= view.findViewById(R.id.specialization);
        tv_doctor_rating= view.findViewById(R.id.doctor_rating);
        doctor_rating_star =view.findViewById(R.id.doctor_rating_star);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown())
        {
            if(isVisibleToUser)
            {
                if(!mAppointment.getScheduledAt().isEmpty())
                {
                    String date = Helper.UTC_to_Local_TimeZone(mAppointment.getScheduledAt());

                    try
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
                        Date value = sdf.parse(date);
                        sdf = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY_HH_MM_A);
                        tv_consultation_date.setText(sdf.format(value));

                        int time_remaining = Helper.second_remaining(date);

                        if(time_remaining > 0)
                        {
                            appointment_reminder(time_remaining, sdf.format(value));
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                /**
                 * Add to appointment list
                 */
                mController.setAppointment(mAppointment);

                if(mController.bookingSummery.getFiles().size() != 0)
                {
                    String UPLOAD_URL = mController.getAppointmentURL() + mAppointment.getAppointmentID() + "/" + Constants.KEY_ATTACHMENTS;
                    String uploadId = UUID.randomUUID().toString();
                    uploadReceiver.setDelegate(AppointmentConfirmationFragment.this);
                    uploadReceiver.setUploadID(uploadId);

                    new MultipartFileUploadClient(uploadId, AppointmentSummery.composeAttachmentJSON(mController.bookingSummery.getFiles()), AppointmentSummery.composeCaptionJSON(mController.bookingSummery.getFiles()), true)
                            .execute(UPLOAD_URL);
                }


                this.bindView(this.mAppointment);

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O)
                {
                    if (!Utils.canDrawOverlays(MyApplication.getInstance()))
                    {
                        needPermissionDialog();
                    }
                }
            }
        }
    }

    @Override
    public void onItemDeleted(int index, int item_id)
    {

    }


    private void bindView(Appointment appointment)
    {
        try
        {
            tv_patient_name.setText(Helper.toCamelCase(appointment.getPatientName()));

            tv_doctor_name.setText(Helper.toCamelCase(appointment.getDoctor().getFullName()));
            tv_specialization.setText(appointment.getDoctor().getSpecialization());
            tv_doctor_rating.setText(String.valueOf(appointment.getDoctor().getRatings()));
            doctor_rating_star.setRating((float) appointment.getDoctor().getRatings());

            if(new InternetConnectionDetector(context).isConnected())
            {
                new HttpClient(HTTP_REQUEST_CODE_GET_APPOINTMENT, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment.getAppointmentID());
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void appointment_reminder(int time_remaining, String date)
    {
        if(getActivity() == null)
        {
            return;
        }

        try
        {
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.APPOINTMENT_REMINDER_NOTIFICATION");
            notificationIntent.putExtra("APPOINTMENT_TIME", date);
            notificationIntent.putExtra("APPOINTMENT_ID", mAppointment.getAppointmentID());
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                notificationIntent.setClass(context, AlarmReceiver.class);
            }

            PendingIntent broadcast = PendingIntent.getBroadcast(context, mAppointment.getAppointmentID(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, time_remaining);

            if(alarmManager == null)
            {
                return;
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void showDialog()
    {
        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_OVERLAY_PERMISSION, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_overlay_permission),
                        getResources().getString(R.string.dialog_content_overlay_permission),
                        getResources().getString(R.string.Settings),
                        getResources().getString(R.string.NoThanks), false);
    }

    private void needPermissionDialog()
    {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run()
            {
                showDialog();
            }
        }, 1000);
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_OVERLAY_PERMISSION)
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            startActivity(intent);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            uploadReceiver.register(context);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        try
        {
            uploadReceiver.unregister(context);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgress(UploadInfo uploadInfo) {

    }

    @Override
    public void onError(UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

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

        Log.i("uploadInfo", "" + serverResponse.getHttpCode());
        Log.i("uploadInfo", "" + serverResponse.getBodyAsString());

        listener.onPostExecute(HTTP_REQUEST_CODE_ATTACH_IMAGES, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {

    }


    @Override
    public void onPreExecute()
    {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

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
            if(requestCode == HTTP_REQUEST_CODE_GET_APPOINTMENT && responseCode == HttpClient.OK)
            {
                this.json_data(response);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void json_data(String data)
    {
        try
        {
            JSONObject json = new JSONObject(data);

            Appointment appointment = Appointment.getAppointmentFromJSON(json.getString(KEY_DATA));

            if(!appointment.getDoctor().getAvatarURL().isEmpty())
            {
                try
                {
                    ImageLoader.load(context, appointment.getDoctor().getAvatarURL(), doctor_image, R.drawable.doctor, 180, 180);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}