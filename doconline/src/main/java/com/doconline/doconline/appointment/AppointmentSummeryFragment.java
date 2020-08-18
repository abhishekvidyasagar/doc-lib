package com.doconline.doconline.appointment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.FileViewerActivity;
import com.doconline.doconline.MultipleImageViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.adapter.ImagesRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.MultipartFileUploadClient;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.helper.ResizableTextView;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.model.Review;
import com.doconline.doconline.utils.CalendarUtils;
import com.doconline.doconline.utils.FileUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_CANCEL_APPOINTMENT;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DELETE_ATTACHMENTS;
import static com.doconline.doconline.app.Constants.CALL_STATUS_ACTIVE;
import static com.doconline.doconline.app.Constants.CALL_STATUS_CALL_NOT_PLACED;
import static com.doconline.doconline.app.Constants.CALL_STATUS_CANCELLED;
import static com.doconline.doconline.app.Constants.CALL_STATUS_COMPLETED;
import static com.doconline.doconline.app.Constants.CALL_STATUS_NOT_ATTENDED;
import static com.doconline.doconline.app.Constants.CALL_STATUS_NO_PRESCRIPTION;
import static com.doconline.doconline.app.Constants.CALL_TYPE_AUDIO;
import static com.doconline.doconline.app.Constants.CALL_TYPE_VIDEO;
import static com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.EEE_MMM_DD_YYYY_HH_MM_A;
import static com.doconline.doconline.app.Constants.IMAGE_VIEWER_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.KEY_ATTACHMENTS;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_FILE_URL;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_RATING;
import static com.doconline.doconline.app.Constants.KEY_TITLE;
import static com.doconline.doconline.app.Constants.MEDIA_DIRECTORY_NAME;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_ATTACH_IMAGES;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_CANCEL_APPOINTMENT;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_DELETE_ATTACHMENTS;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_GET_APPOINTMENT;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_GET_RATING;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_POST_RATING;

@SuppressWarnings("unchecked")
public class AppointmentSummeryFragment extends BaseFragment implements View.OnClickListener,
        OnHttpResponse, OnItemDeleted, SingleUploadBroadcastReceiver.Delegate/*,
        RatingFragment.AddReviewFragmentListener*/ {
    private static final String TAG = AppointmentSummeryFragment.class.getSimpleName();

    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 2;


    LinearLayout layout_main;
    LinearLayout layout_appointment_details;
    TextView tv_call_type;
    TextView tv_booking_id;
    TextView tv_consultation_date;
    TextView tv_notes;
    TextView tv_upload_image;
    TextView tv_edit_note;
    TextView tv_appointment_status;
    LinearLayout layout_note;
    RelativeLayout layout_rate_doctor;
    ImageView doctor_image;
    CircleImageView patient_image;
    TextView tv_doctor_name;
    TextView tv_specialization;
    TextView doctor_remarks;
    TextView tv_patient_name;
    TextView tv_patient_gender;
    TextView tv_patient_age;
    RecyclerView recyclerView;
    LinearLayout layout_attachments;
    TextView noAttachmentsTextView;

    TextView tv_rate_doctor;

    TextView tv_rating;

    Button btn_cancel_appointment;

    Button btn_add_to_calendar;

    TextView tv_doctor_rating;

    RatingBar doctor_rating_star;

    //rating Doctor

    RelativeLayout doctorRatingLayout;

    CircleImageView doctorImageCiv;

    TextView nameTextView;

    TextView doctorTypeTextView;

    TextView callDurationTextView;
    Button submitButton;
    Button notNowButton;
    TextView tvSymptoms;
    TextView tv_symptoms_severity;
    TextView tvSymptomsHeader;

    //for new rating style
    TextView veryPoor;
    TextView poor;
    TextView good;
    TextView satisfied;
    TextView awesome;
    LinearLayout userFeedBackLayout;
    LinearLayout rateDoctor;
    TextView veryPoorSum;
    TextView poorSum;
    TextView goodSum;
    TextView satisfiedSum;
    TextView awesomeSum;
    TextView symtomsViewMore;
    TextView provisional_diagnosis;
    TextView followupdatebydoctor;

    //ffor new rating style

    private Context context;
    private OnHttpResponse listener;
    private OnTaskCompleted task_listener;
    private int appointment_id, appointment_status;
    private ImagesRecyclerAdapter mAdapter;

    private String scheduled_at;
    private List<FileUtils> attachments = new ArrayList<>();
    private Uri imageUri;

    private boolean is_dialog_visible = false;
    private boolean is_rated = false;
    private int index;
    private SimpleDateFormat sdf;
    private Appointment appointment;

    private double userFeedBack = 0.0;

    public AppointmentSummeryFragment() {

    }

    @SuppressLint("ValidFragment")
    public AppointmentSummeryFragment(Context context, OnHttpResponse listener, OnTaskCompleted task_listener, int appointment_id) {
        this.context = context;
        this.listener = listener;
        this.task_listener = task_listener;
        this.appointment_id = appointment_id;
        this.sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_summery, container, false);
       // ButterKnife.bind(this, view);
         layout_main =  view.findViewById(R.id.layout_main);
         layout_appointment_details=  view.findViewById(R.id.layout_appointment_details);
        tv_call_type=  view.findViewById(R.id.tv_call_type);
        tv_booking_id=  view.findViewById(R.id.tv_booking_id);
        tv_consultation_date=  view.findViewById(R.id.tv_consultation_date);
        tv_notes=  view.findViewById(R.id.tv_notes);
        tv_upload_image=  view.findViewById(R.id.upload_image);
        tv_edit_note=  view.findViewById(R.id.edit_note);
        tv_appointment_status=  view.findViewById(R.id.tv_appointment_status);
        layout_note=  view.findViewById(R.id.layout_note);
        layout_rate_doctor=  view.findViewById(R.id.layout_rate_doctor);
        provisional_diagnosis = view.findViewById(R.id.provisional_diagnosis);
        followupdatebydoctor = view.findViewById(R.id.followupdatebydoctor);

        rateDoctor=  view.findViewById(R.id.ratedoctor);

        //layout_rate_doctor.setOnClickListener(new View.OnClickListener() {
        layout_rate_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_rated) {
                    Toast.makeText(context, "you have already rated this Call", Toast.LENGTH_SHORT).show();
                } else {
                    showRatingAlert(0.0f);
                }
            }
        });
         doctor_image=  view.findViewById(R.id.doctor_image);
       patient_image=  view.findViewById(R.id.patient_image);
        tv_doctor_name=  view.findViewById(R.id.doctor_name);
        tv_specialization=  view.findViewById(R.id.specialization);
        doctor_remarks=  view.findViewById(R.id.doctor_remarks);
        tv_patient_name=  view.findViewById(R.id.patient_name);
        tv_patient_gender=  view.findViewById(R.id.patient_gender);
        tv_patient_age=  view.findViewById(R.id.patient_age);
         recyclerView=  view.findViewById(R.id.image_list);
         layout_attachments=  view.findViewById(R.id.layout_attachments);
        noAttachmentsTextView=  view.findViewById(R.id.tv_no_attachments);

        tv_rate_doctor=  view.findViewById(R.id.label_rate_doctor);
         tv_rating=  view.findViewById(R.id.tv_rating);
         btn_cancel_appointment=  view.findViewById(R.id.btnCancelAppointment);
         btn_add_to_calendar=  view.findViewById(R.id.btnAddAppointmentToCalendar);
        tv_doctor_rating=  view.findViewById(R.id.doctor_rating);
         doctor_rating_star=  view.findViewById(R.id.doctor_rating_star);

        //rating Doctor
         doctorRatingLayout=  view.findViewById(R.id.layout_doctor_rating);
        doctorImageCiv=  view.findViewById(R.id.civ_doctor_image);
         nameTextView=  view.findViewById(R.id.tv_name);
        doctorTypeTextView=  view.findViewById(R.id.tv_doctor_type);
        callDurationTextView=  view.findViewById(R.id.tv_call_duration);

        submitButton=  view.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFeedBack == 0.0) {
                    Toast.makeText(getActivity(), "Please provide your rating", Toast.LENGTH_LONG).show();
                } else {
                    if (new InternetConnectionDetector(context).isConnected()) {
                        if (appointment_id > 0) {
                            new HttpClient(HTTP_REQUEST_CODE_POST_RATING, MyApplication.HTTPMethod.POST.getValue(), true, Review.getRatingJSON(userFeedBack), AppointmentSummeryFragment.this)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id + "/" + KEY_RATING);
                        }
                    } else {
                        new CustomAlertDialog(getContext(), v).snackbarForInternetConnectivity();
                    }
                }
            }
        });
        notNowButton=  view.findViewById(R.id.btn_not_now);
        notNowButton . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 0.0;
                Animation exitToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_to_top);
                doctorRatingLayout.startAnimation(exitToTop);
                doctorRatingLayout.setVisibility(GONE);
            }
        });

        tvSymptoms=  view.findViewById(R.id.tv_symptoms);

        tv_symptoms_severity=  view.findViewById(R.id.tv_symptoms_severity);

        tvSymptomsHeader=  view.findViewById(R.id.tv_symptoms_header);

        //for new rating style
        veryPoor=  view.findViewById(R.id.tv_verypoor);
        poor=  view.findViewById(R.id.tv_poor);
        good=  view.findViewById(R.id.tv_good);
        satisfied=  view.findViewById(R.id.tv_satisfied);
        awesome=  view.findViewById(R.id.tv_awesome);
        userFeedBackLayout=  view.findViewById(R.id.rating_layout);

        veryPoorSum=  view.findViewById(R.id.tv_verypoor_sum);
        veryPoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 1.0;
                veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor), null, null);

                poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
            }
        });
        poorSum=  view.findViewById(R.id.tv_poor_sum);

        poor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 2.0;
                veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);

                poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor), null, null);

                good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
            }
        });
         goodSum=  view.findViewById(R.id.tv_good_sum);

         good.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 userFeedBack = 3.0;
                 veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                 poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);

                 good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good), null, null);

                 satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                 awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
             }
         });
        satisfiedSum=  view.findViewById(R.id.tv_satisfied_sum);

        satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 4.0;
                veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);

                satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied), null, null);

                awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
            }
        });

         awesomeSum=  view.findViewById(R.id.tv_awesome_sum);
         awesome.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 userFeedBack = 5.0;
                 veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                 poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                 good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                 satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);

                 awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied), null, null);
             }
         });

        symtomsViewMore=  view.findViewById(R.id.symptoms_viewmore);
        symtomsViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray arrayObject = new JSONArray(appointment.getSymptoms());
                    if (arrayObject.length() > 1) {
                        //table logic here
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.alert_symptoms);
                        LinearLayout symptomslayout = dialog.findViewById(R.id.symptoms_ll);

                        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        symptomslayout.findViewById(R.id.close_alert).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        for (int i = 0; i < arrayObject.length(); i++) {
                            final JSONObject eachObject = arrayObject.getJSONObject(i);
                            final LinearLayout linearLayout = (LinearLayout) View.inflate(context, R.layout.inflater_symtoms_table, null);
                            JSONObject symptomObject = eachObject.getJSONObject("symptom");
                            ((TextView) linearLayout.findViewById(R.id.tv_symptoms)).setText(symptomObject.getString("name"));

                            if (eachObject.get("severity") instanceof JSONObject) {
                                JSONObject severityObject = eachObject.getJSONObject("severity");
                                ((TextView) linearLayout.findViewById(R.id.tv_symptoms_severity)).setText(severityObject.getString("name"));
                            } else {
                                ((TextView) linearLayout.findViewById(R.id.tv_symptoms_severity)).setText("--");
                            }
                            symptomslayout.addView(linearLayout);
                        }
                        dialog.show();
                    } else {
                        Toast.makeText(context, "no data to show", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.addListener();

        if (getActivity() == null) {
            return;
        }

        if (new InternetConnectionDetector(context).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_GET_APPOINTMENT, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id);
        }

        layout_main.setVisibility(GONE);
        add_image_list();
    }

    private void addListener() {
        tv_upload_image.setOnClickListener(this);
        tv_edit_note.setOnClickListener(this);
        btn_cancel_appointment.setOnClickListener(this);
        btn_add_to_calendar.setOnClickListener(this);
    }


    @Override
    public void onPreExecute() {
        listener.onPreExecute();
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
            if (requestCode == HTTP_REQUEST_CODE_GET_APPOINTMENT && responseCode == HttpClient.OK) {
                json_data(response);

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_RATING) {
                rating_json_data(responseCode, response);
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_POST_RATING) {
                if (responseCode == HttpClient.NO_RESPONSE) {
                    rated_successfully();
                    return;
                } else {
                    Animation exitToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_to_top);
                    doctorRatingLayout.startAnimation(exitToTop);
                    doctorRatingLayout.setVisibility(GONE);
                }
            }

            if (requestCode == HTTP_REQUEST_CODE_DELETE_ATTACHMENTS && responseCode == HttpClient.NO_RESPONSE) {
                attachments.remove(index);

                if (attachments.size() != 0) {
                    noAttachmentsTextView.setVisibility(GONE);
                    layout_attachments.setVisibility(View.VISIBLE);
                } else {
                    layout_attachments.setVisibility(GONE);
                    noAttachmentsTextView.setVisibility(View.VISIBLE);
                }

                mAdapter.notifyDataSetChanged();
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_ATTACH_IMAGES && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray(KEY_DATA);

                    attachments.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        json = jsonArray.getJSONObject(i);

                        int id = json.getInt(KEY_ID);
                        String file_path = json.getString(KEY_FILE_URL);
                        String title = json.isNull(KEY_TITLE) ? "" : json.getString(KEY_TITLE);
                        attachments.add(new FileUtils(id, file_path, title));
                    }

                    Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (attachments.size() != 0) {
                        noAttachmentsTextView.setVisibility(GONE);
                        layout_attachments.setVisibility(View.VISIBLE);
                        //Collections.reverse(attachments);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(attachments.size() - 1);
                    } else {
                        layout_attachments.setVisibility(View.GONE);
                        noAttachmentsTextView.setVisibility(View.VISIBLE);
                    }
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_CANCEL_APPOINTMENT && responseCode == HttpClient.NO_RESPONSE) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btn_cancel_appointment.setVisibility(GONE);
                        btn_add_to_calendar.setVisibility(GONE);
                        tv_upload_image.setVisibility(GONE);
                        tv_edit_note.setVisibility(GONE);
                        tv_appointment_status.setText("Cancelled");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.light_red));

                        mAdapter.isRemovable = false;
                        mAdapter.notifyDataSetChanged();
                        success_alert("Successful", "Appointment cancelled successfully!");

                        //removeCalendarEvent();
                        cancelAlarm();
                    }
                });

                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            listener.onPostExecute(requestCode, responseCode, response);
        }
    }

    private void removeCalendarEvent() {
        if (!CheckingCalendarPermissionIsEnabledOrNot()) {
            RequestCalendarPermission();
        } else {
            new CalendarUtils(context, appointment).deleteCalendarEvent();
        }
    }


    @Override
    public void onClick(View view) {
        if (!new InternetConnectionDetector(context).isConnected()) {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            return;
        }

        int id = view.getId();
        if (id == R.id.upload_image) {
            if (CheckingPermissionIsEnabledOrNot()) {
                image_picker_dialog();
            } else {
                RequestMultiplePermission();
            }
        } else if (id == R.id.btnCancelAppointment) {
            this.cancel_appointment("Cancel Appointment", "Are you sure want to cancel this appointment ?");
        } else if (id == R.id.btnAddAppointmentToCalendar) {
            if (CheckingCalendarPermissionIsEnabledOrNot()) {
                Log.d("AAA", "Calender permission enabled");
                new CalendarUtils(context, appointment).addCalendarEvent();
            } else {
                Log.d("AAA", "Calender permission not enabled");
                RequestCalendarPermission();
            }
        }
    }










    private void image_picker_dialog() {
        if (!is_cancelable(scheduled_at)) {
            Toast.makeText(context, "File upload not allowed before 5 minutes on your schedule", Toast.LENGTH_LONG).show();
            return;
        }

        int limit = (int) mController.getAttachmentLimit() - attachments.size();

        if (limit <= 0) {
            Toast.makeText(context, "File Upload Limit Exceed", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialDialog.Builder(context)
                .title("Options (Max " + mController.getSession().getMaxFileSize() + " MB / File)")
                .items(R.array.file_picker_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0:

                                viewMultipleImageChooser();
                                break;

                            case 1:

                                openCamera();
                                break;

                            case 2:

                                viewMultipleFileChooser();
                                break;
                        }
                    }
                })
                .show();
    }

    private void viewMultipleFileChooser() {
        int limit = (int) mController.getAttachmentLimit() - attachments.size();

        Intent intent4 = new Intent(getActivity(), NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, limit);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, com.doconline.doconline.app.Constants.FILE_EXTENSIONS);
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }

    private void openCamera() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), MEDIA_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        /***
         * Check if we're running on Android 5.0 or higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageUri = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        } else {
            imageUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to Open Camera", Toast.LENGTH_LONG).show();
        }
    }

    private void viewMultipleImageChooser() {
        int limit = (int) mController.getAttachmentLimit() - attachments.size();

        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, limit);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (new InternetConnectionDetector(context).isConnected()) {
            if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                List<FileUtils> file_paths = new ArrayList<>();

                if ((images.size()) <= mController.getAttachmentLimit()) {
                    for (Image img : images) {
                        File file = new File(img.path);
                        long length = file.length() / 1024; // Size in KB

                        if (length <= 1024 * mController.getSession().getMaxFileSize()) {
                            file_paths.add(new FileUtils(img.path, ""));
                        } else {
                            Toast.makeText(context, "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (file_paths.size() > 0) {
                        startMultipleImageViewerActivity(file_paths);
                    }
                } else {
                    Toast.makeText(context, "Max " + mController.getAttachmentLimit() + " files allowed.", Toast.LENGTH_LONG).show();
                }
            }

            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
                if (Helper.fileExist(imageUri.getPath())) {
                    List<FileUtils> file_paths = new ArrayList<>();

                    File file = new File(imageUri.getPath());
                    long length = file.length() / 1024; // Size in KB

                    if (length <= 1024 * mController.getSession().getMaxFileSize()) {
                        file_paths.add(new FileUtils(imageUri.getPath(), ""));
                        startMultipleImageViewerActivity(file_paths);
                    } else {
                        Toast.makeText(context, "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "File Not Found", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
                ArrayList<NormalFile> files = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                List<FileUtils> file_paths = new ArrayList<>();

                for (NormalFile file : files) {
                    File tempFile = new File(file.getPath());
                    long length = tempFile.length() / 1024; // Size in KB

                    if (length <= 1024 * mController.getSession().getMaxFileSize()) {
                        file_paths.add(new FileUtils(file.getPath(), ""));
                    } else {
                        Toast.makeText(context, "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                    }
                }

                if (file_paths.size() > 0) {
                    startMultipleImageViewerActivity(file_paths);
                }
            }

            if (requestCode == IMAGE_VIEWER_REQUEST_CODE && resultCode == IMAGE_VIEWER_REQUEST_CODE && data != null) {
                Bundle args = data.getBundleExtra("BUNDLE_ATTACHMENTS");
                List<FileUtils> file_paths = (ArrayList<FileUtils>) args.getSerializable("ATTACHMENTS");

                if (file_paths == null) {
                    return;
                }

                String UPLOAD_URL = mController.getAppointmentURL() + appointment_id + "/" + KEY_ATTACHMENTS;
                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(AppointmentSummeryFragment.this);
                uploadReceiver.setUploadID(uploadId);

                listener.onPreExecute();
                new MultipartFileUploadClient(uploadId, AppointmentSummery.composeAttachmentJSON(file_paths), AppointmentSummery.composeCaptionJSON(file_paths), true)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UPLOAD_URL);
            }
        } else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }


    private void startMultipleImageViewerActivity(List<FileUtils> file_paths) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("ATTACHMENTS", (Serializable) file_paths);

        Intent intent = new Intent(getActivity(), MultipleImageViewerActivity.class);
        intent.putExtra("BUNDLE_ATTACHMENTS", bundle);
        intent.putExtra("FROM_APPOINTMENT_SUMMARY", true);
        startActivityForResult(intent, IMAGE_VIEWER_REQUEST_CODE);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    private void success_alert(String title, String content) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            /*new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(title)
                    .setContentText(content)
                    .show();*/
            new CustomAlertDialog(context, this)
                    .showAlertDialogWithPositiveAction(title, content,
                            getResources().getString(R.string.OK), true);
        }
    }

    private void bindView(final Appointment appointment) {
        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                layout_main.setVisibility(View.VISIBLE);

                scheduled_at = appointment.getScheduledAt();

                tv_booking_id.setText(appointment.getPublicAppointmentId());
                tv_doctor_name.setText(Helper.toCamelCase(appointment.getDoctor().getFullName()));
                tv_specialization.setText(Helper.toCamelCase(appointment.getDoctor().getSpecialization()));
                tv_doctor_rating.setText(String.valueOf(appointment.getDoctor().getRatings()));
                doctor_rating_star.setRating((float) appointment.getDoctor().getRatings());

                if (appointment.getDoctorNotes().isEmpty()) {
                    doctor_remarks.setText("No remarks yet");
                } else {
                    doctor_remarks.setText(appointment.getDoctorNotes());

                    if (appointment.getDoctorNotes().length() > 200) {
                        ResizableTextView.doResizeTextView(doctor_remarks, 8, "Show More", true);
                    }
                }

                if (appointment.getProvisionalDiagnosis().isEmpty()) {
                    provisional_diagnosis.setText("No provisional diagnosis found");
                } else {
                    provisional_diagnosis.setText(appointment.getProvisionalDiagnosis());

                    if (appointment.getProvisionalDiagnosis().length() > 200) {
                        ResizableTextView.doResizeTextView(provisional_diagnosis, 1, "Show More", true);
                    }
                }

                if (appointment.getFollowUpDateByDoctor().isEmpty() || appointment.getFollowUpDateByDoctor().equalsIgnoreCase("-NA-")) {
                    //followupdatebydoctor.setText("No Followup date found");
                    followupdatebydoctor.setText("-NA-");
                } else {
                    String formattedDate = getRequiredDateFormatFromAvailableFormat(appointment.getFollowUpDateByDoctor());
                    followupdatebydoctor.setText(""+formattedDate);
                }

                if (appointment.getCallType() == CALL_TYPE_AUDIO) {
                    if (appointment.getCallChannel() == 1) {
                        int imgResource = R.drawable.ic_audio;
                        tv_call_type.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                        tv_call_type.setCompoundDrawablePadding(10);
                        tv_call_type.setText("Audio");
                    } else {
                        int imgResource = R.drawable.phone;
                        tv_call_type.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                        tv_call_type.setCompoundDrawablePadding(10);
                        tv_call_type.setText("Audio");
                    }
                }

                if (appointment.getCallType() == CALL_TYPE_VIDEO) {
                    int imgResource = R.drawable.ic_video_small;
                    tv_call_type.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    tv_call_type.setCompoundDrawablePadding(10);
                    tv_call_type.setText("Video");
                }

                if (!appointment.getNotes().isEmpty()) {
                    layout_note.setVisibility(View.VISIBLE);
                    tv_notes.setVisibility(View.VISIBLE);
                    tv_notes.setText(String.valueOf(appointment.getNotes()));
                } else {
                    layout_note.setVisibility(View.VISIBLE);
                    tv_notes.setVisibility(View.VISIBLE);
                    tv_notes.setText("No Notes added");
                }


                try {
                    JSONArray arrayObject = new JSONArray(appointment.getSymptoms());
                    Log.e("AAA", "symptoms : " + arrayObject);
                    if (arrayObject.length() > 0) {
                        if (arrayObject.length() == 1) {
                            symtomsViewMore.setVisibility(GONE);
                        } else {
                            symtomsViewMore.setVisibility(View.VISIBLE);
                        }
                        JSONObject eachObject = arrayObject.getJSONObject(0);
                        Log.e("AAA", "first symptoms : " + eachObject);
                        JSONObject symptomObject = eachObject.getJSONObject("symptom");
                        tvSymptoms.setText("" + symptomObject.getString("name"));
                        if (eachObject.get("severity") instanceof JSONObject) {
                            JSONObject severityObject = eachObject.getJSONObject("severity");
                            tv_symptoms_severity.setText("" + severityObject.getString("name"));
                        } else {
                            tv_symptoms_severity.setText("No symptom severity found");
                        }

                    } else {
                        tvSymptoms.setText("No symptom found");
                        tv_symptoms_severity.setText("No symptom severity found");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (!appointment.getScheduledAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(appointment.getScheduledAt());

                    try {
                        Date value = sdf.parse(date);
                        SimpleDateFormat sdf = new SimpleDateFormat(EEE_MMM_DD_YYYY_HH_MM_A);
                        tv_consultation_date.setText(sdf.format(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                tv_patient_name.setText(Helper.toCamelCase(appointment.getPatient().getFirstName()));

                if (appointment.getPatient().getGender().length() > 0) {
                    if (!appointment.getPatient().getDateOfBirth().isEmpty()) {
                        tv_patient_gender.setText(appointment.getPatient().getGender().substring(0, 1) + ", ");
                    } else {
                        tv_patient_gender.setText(appointment.getPatient().getGender().substring(0, 1));
                    }
                } else {
                    tv_patient_gender.setText(String.valueOf(appointment.getPatient().getGender()));
                }

                if (!appointment.getPatient().getDateOfBirth().isEmpty()) {
                    tv_patient_age.setText(String.valueOf(Helper.getYearDiff(appointment.getPatient().getDateOfBirth())));
                } else {
                    tv_patient_age.setText(appointment.getPatient().getDateOfBirth());
                }


                if (appointment.getAttachments().size() != 0) {
                    noAttachmentsTextView.setVisibility(GONE);
                    layout_attachments.setVisibility(View.VISIBLE);

                    //Collections.reverse(attachments);
                    mAdapter.notifyDataSetChanged();
                } else {
                    layout_attachments.setVisibility(GONE);
                    noAttachmentsTextView.setVisibility(View.VISIBLE);
                }

                btn_cancel_appointment.setVisibility(GONE);
                btn_add_to_calendar.setVisibility(GONE);
                tv_edit_note.setVisibility(GONE);
                tv_upload_image.setVisibility(GONE);
                layout_note.setVisibility(GONE);
                tv_notes.setVisibility(GONE);

                switch (appointment.getAppointmentStatus()) {
                    case CALL_STATUS_CANCELLED:

                        tv_appointment_status.setText("Cancelled");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.light_red));
                        break;

                    case CALL_STATUS_ACTIVE:

                        tv_appointment_status.setText("Active");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.light_green));
                        break;

                    case CALL_STATUS_COMPLETED:

                        tv_appointment_status.setText("Finished");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.skyblue));
                        task_listener.onTaskCompleted(true, 0, "CALL_STATUS_COMPLETED");
                        break;

                    case CALL_STATUS_CALL_NOT_PLACED:

                        tv_appointment_status.setText("Call Not Placed By Doctor");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.light_red));
                        break;

                    case CALL_STATUS_NOT_ATTENDED:

                        tv_appointment_status.setText("Call Not Attended");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.light_red));
                        break;

                    case CALL_STATUS_NO_PRESCRIPTION:

                        tv_appointment_status.setText("Call Placed No Prescription");
                        tv_appointment_status.setTextColor(ContextCompat.getColor(context, R.color.skyblue));
                        break;
                }


                if (appointment.getAppointmentStatus() == CALL_STATUS_ACTIVE && is_cancelable(scheduled_at)) {
                    tv_upload_image.setVisibility(View.VISIBLE);
                    btn_cancel_appointment.setVisibility(View.VISIBLE);
                    btn_add_to_calendar.setVisibility(GONE);

                    /*layout_note.setVisibility(View.VISIBLE);
                    tv_notes.setVisibility(View.VISIBLE);*/
                    //tv_edit_note.setVisibility(View.VISIBLE);

                    mAdapter.isRemovable = true;
                    mAdapter.notifyDataSetChanged();
                }
                layout_note.setVisibility(View.VISIBLE);
                tv_notes.setVisibility(View.VISIBLE);


                /*if((appointment.getAppointmentStatus() == CALL_STATUS_COMPLETED
                        || appointment.getAppointmentStatus() == CALL_STATUS_NO_PRESCRIPTION) && !is_rated)
                {*/

                /*new HttpClient(HTTP_REQUEST_CODE_GET_RATING, MyApplication.HTTPMethod.GET.getValue(), AppointmentSummeryFragment.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id + "/" + KEY_RATING);*/
                /*}*/

                load_doctor_image(appointment.getDoctor().getAvatarURL());
                load_patient_image(appointment.getPatient().getProfilePic());
            }
        });
    }


    private boolean is_cancelable(String scheduled_at) {
        String current = sdf.format(System.currentTimeMillis());

        String scheduled_time = Helper.UTC_to_Local_TimeZone(scheduled_at);
        return Helper.time_diff(scheduled_time, current);
    }

    private String getRequiredDateFormatFromAvailableFormat(String availabledate) {
        Date date = null;
        String str = null;

        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "EEE MMM dd, yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        try {
            date = inputFormat.parse(availabledate);
            str = outputFormat.format(date);
        } catch (android.net.ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void load_doctor_image(final String avatar_url) {
        if (!avatar_url.isEmpty()) {
            try {
                ImageLoader.load(context, avatar_url, doctor_image, R.drawable.doctor, 150, 150);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void load_patient_image(final String avatar_url) {
        if (!avatar_url.isEmpty()) {
            try {
                ImageLoader.load(context, avatar_url, patient_image, R.drawable.ic_avatar, 60, 60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void add_image_list() {
        if (mAdapter == null) {
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new ImagesRecyclerAdapter(context, this, attachments, false, true);
            recyclerView.setAdapter(mAdapter);

            mAdapter.SetOnItemClickListener(new ImagesRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int i) {
                    if (!new InternetConnectionDetector(context).isConnected()) {
                        new CustomAlertDialog(getContext(), AppointmentSummeryFragment.this, getView()).snackbarForInternetConnectivity();
                        return;
                    }

                    Intent intent = new Intent(getActivity(), FileViewerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FILE", attachments.get(i));
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public void onItemDeleted(int index, int item_id) {
        this.index = index;

        if (!is_cancelable(scheduled_at)) {
            Toast.makeText(context, "File can't be remove before 5 minutes on your schedule", Toast.LENGTH_LONG).show();
            return;
        }

        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DELETE_ATTACHMENTS, this).
                showDialogWithAction("Delete Attachment", "Are you sure ?", getResources().getString(R.string.Yes),
                        getResources().getString(R.string.No), true);
    }


    private void json_data(String data) {
        try {
            JSONObject json = new JSONObject(data);

            /*if(json.has(KEY_SESSION_ID) && json.has(KEY_TOKEN_ID))
            {
                Intent intent = new Intent();
                intent.setAction("cb.doconline.VOIP");
                intent.putExtra("json_data", data);
                intent.putExtra("is_outgoing_call", true);
                context.sendBroadcast(intent);
                return;
            }*/

            appointment = Appointment.getAppointmentFromJSON(json.getString(KEY_DATA));

            this.attachments.clear();
            this.attachments.addAll(appointment.getAttachments());
            this.appointment_status = appointment.getAppointmentStatus();

            this.bindView(appointment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void cancel_appointment(String title, final String content) {
        if (!is_cancelable(scheduled_at)) {
            Toast.makeText(context, "Appointment can't be cancel before 5 minutes on your schedule", Toast.LENGTH_LONG).show();
            return;
        }

        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_CANCEL_APPOINTMENT, this).
                showDialogWithAction(title, content, getResources().getString(R.string.Yes),
                        getResources().getString(R.string.No), true);
    }

    private void cancelAlarm() {
        if (getActivity() == null) {
            return;
        }

        Log.wtf("ALARM_STATUS", "OFF");

        Intent intent = new Intent("android.media.action.APPOINTMENT_REMINDER_NOTIFICATION");
        intent.addCategory("android.intent.category.DEFAULT");

        //PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        //Intent intent = new Intent(context, AlarmReceiver.class);
        //alarmManager.cancel(PendingIntent.getService(context, 100, intent, 0));

        PendingIntent pIntent = PendingIntent.getBroadcast(context, appointment_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        alarmManager.cancel(pIntent);
        pIntent.cancel();

        mController.cancelAppointment(appointment_id);
    }


    @Override
    public void onResume() {
        super.onResume();
        getRatingStatus();

        try {
            uploadReceiver.register(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRatingStatus() {
        if (new InternetConnectionDetector(getActivity()).isConnected()) {
            if (!is_rated) {
                new HttpClient(HTTP_REQUEST_CODE_GET_RATING, MyApplication.HTTPMethod.GET.getValue(), AppointmentSummeryFragment.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id + "/" + KEY_RATING);
            }
        } else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            uploadReceiver.unregister(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onProgress(final UploadInfo uploadInfo) {
        Log.i("PROGRESS", "progress = " + uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(final UploadInfo uploadInfo, final ServerResponse serverResponse, final Exception exception) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getView() == null) {
            return;
        }

        Log.i("uploadInfo", "" + serverResponse.getHttpCode());
        Log.i("uploadInfo", "" + serverResponse.getBodyAsString());

        listener.onPostExecute(HTTP_REQUEST_CODE_ATTACH_IMAGES, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
        onPostExecute(HTTP_REQUEST_CODE_ATTACH_IMAGES, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
    }

    @Override
    public void onCompleted(final UploadInfo uploadInfo, final ServerResponse serverResponse) {
        try {
            Log.i("uploadInfo", "" + serverResponse.getHttpCode());
            Log.i("uploadInfo", "" + serverResponse.getBodyAsString());
            onPostExecute(HTTP_REQUEST_CODE_ATTACH_IMAGES, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(final UploadInfo uploadInfo) {
        listener.onPostExecute(0, 0, "Cancelled");
    }


    @Override
    public void onPositiveAction(int requestCode) {

        if (new InternetConnectionDetector(context).isConnected()) {
            if (requestCode == DIALOG_REQUEST_CODE_CANCEL_APPOINTMENT) {
                new HttpClient(HTTP_REQUEST_CODE_CANCEL_APPOINTMENT, MyApplication.HTTPMethod.PATCH.getValue(), AppointmentSummeryFragment.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id + "/cancel");
            }

            if (requestCode == DIALOG_REQUEST_CODE_DELETE_ATTACHMENTS) {
                String URL = mController.getAppointmentURL() + appointment_id + "/" + KEY_ATTACHMENTS + "/" + attachments.get(index).getId();
                new HttpClient(HTTP_REQUEST_CODE_DELETE_ATTACHMENTS, MyApplication.HTTPMethod.DELETE.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL);
            }
        } else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    @Override
    public void onNegativeAction() {

    }


    private void rating_json_data(int responseCode, String json_data) {
        double rating = 0;
        try {
            if (responseCode == HttpClient.OK) {
                JSONObject json = new JSONObject(json_data);
                rating = json.getDouble(KEY_DATA);

                rateDoctor.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.network_image_view);
                rateDoctor.startAnimation(anim);

                if (rating != 0) {
                    this.is_rated = true;

                    if (rating == 1.0) {
                        veryPoorSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor), null, null);
                    } else if (rating == 2.0) {
                        poorSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor), null, null);
                    } else if (rating == 3.0) {
                        goodSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good), null, null);
                    } else if (rating == 4.0) {
                        satisfiedSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied), null, null);
                    } else if (rating == 5.0) {
                        awesomeSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied), null, null);
                    }
                    tv_rate_doctor.setText("Rated");
                    tv_rating.setText((int) rating);

                } else {
                    veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                    poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                    good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                    satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                    awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
                }
            } else {
                layout_rate_doctor.setVisibility(GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*finally
        {
            if(appointment_status == CALL_STATUS_COMPLETED || appointment_status == CALL_STATUS_NO_PRESCRIPTION)
            {
                layout_rate_doctor.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.network_image_view);
                rating_bar.startAnimation(anim);

                if(rating != 0)
                {
                    rating_bar.setRating((float) rating);
                    rating_bar.setIsIndicator(true);
                    tv_rate_doctor.setText(String.valueOf("Rated " + (int) rating_bar.getRating()));
                    this.is_rated = true;
                }

                else
                {
                    add_rating_listener();
                }
            }

            else
            {
                layout_rate_doctor.setVisibility(View.GONE);
            }
        }*/
    }

    private void rated_successfully() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            Animation exitToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_to_top);
            doctorRatingLayout.startAnimation(exitToTop);
            doctorRatingLayout.setVisibility(GONE);
            Toast.makeText(context, "Thank you for your feedback", Toast.LENGTH_LONG).show();

            this.is_rated = true;
            //rating_bar.setEnabled(false);
            tv_rate_doctor.setText("Rated");
            //tv_rating.setText(ratin);
            if (new InternetConnectionDetector(context).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_GET_APPOINTMENT, MyApplication.HTTPMethod.GET.getValue(), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id);
            }

            if (userFeedBack == 1.0) {
                veryPoorSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor), null, null);
            } else if (userFeedBack == 2.0) {
                poorSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor), null, null);
            } else if (userFeedBack == 3.0) {
                goodSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good), null, null);
            } else if (userFeedBack == 4.0) {
                satisfiedSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied), null, null);
            } else if (userFeedBack == 5.0) {
                awesomeSum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied), null, null);
            }
        }
    }

    private void showRatingAlert(float rating) {
        if (doctorRatingLayout.getVisibility() == GONE) {
            Animation enterFrombottom = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_bottom);
            doctorRatingLayout.startAnimation(enterFrombottom);
            doctorRatingLayout.setVisibility(View.VISIBLE);
        }
        ImageLoader.load(getActivity(), appointment.getDoctor().getAvatarURL(), doctorImageCiv, R.drawable.ic_avatar, 80, 80);
        nameTextView.setText(Helper.toCamelCase(appointment.getDoctor().getFullName()));
        doctorTypeTextView.setText(appointment.getDoctor().getSpecialization());
        callDurationTextView.setVisibility(GONE);
    }
/*
    @OnClick(R.id.btn_submit)
    public void submit() {
        if (userFeedBack == 0.0) {
            Toast.makeText(getActivity(), "Please provide your rating", Toast.LENGTH_LONG).show();
        } else {
            if (new InternetConnectionDetector(context).isConnected()) {
                if (appointment_id > 0) {
                    new HttpClient(HTTP_REQUEST_CODE_POST_RATING, MyApplication.HTTPMethod.POST.getValue(), true, Review.getRatingJSON(userFeedBack), AppointmentSummeryFragment.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id + "/" + KEY_RATING);
                }
            } else {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }*/



    /*@Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, Review _review) {
        if (new InternetConnectionDetector(context).isConnected()) {
            if (appointment_id > 0) {
                new HttpClient(HTTP_REQUEST_CODE_POST_RATING, MyApplication.HTTPMethod.POST.getValue(), true, Review.getRatingJSON(_review.rating), AppointmentSummeryFragment.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + appointment_id + "/" + KEY_RATING);
            }
        } else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        is_dialog_visible = false;
        rating_bar.setRating(0);
    }


    @Override
    public void onDialogRatingChange(android.support.v4.app.DialogFragment dialog, Review _review) {
        rating_bar.setRating((float) _review.rating);
    }*/


    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission() {
        /**
         * Creating String Array with Permissions.
         */
        requestPermissions(new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, CAMERA_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadStoragePermission && WriteStoragePermission) {
                        image_picker_dialog();
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;

            case CALENDAR_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean ReadCalendarPermissionResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteCalendarPermissionResult = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (ReadCalendarPermissionResult && WriteCalendarPermissionResult) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show();

                        if (new CalendarUtils(context, appointment).isEventAlreadyExists()) {
                            removeCalendarEvent();
                        } else {
                            new CalendarUtils(context, appointment).addCalendarEvent();
                        }
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    /**
     * Checking permission is enabled or not
     */
    public boolean CheckingPermissionIsEnabledOrNot() {
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int WriteStoragePermissionResult = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int ReadStoragePermissionResult = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteStoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ReadStoragePermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    public boolean CheckingCalendarPermissionIsEnabledOrNot() {
        int ReadCalendarPermissionResult = ContextCompat.checkSelfPermission(getActivity(), READ_CALENDAR);
        int WriteCalendarPermissionResult = ContextCompat.checkSelfPermission(getActivity(), WRITE_CALENDAR);

        return ReadCalendarPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteCalendarPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestCalendarPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        READ_CALENDAR,
                        WRITE_CALENDAR
                }, CALENDAR_PERMISSION_REQUEST_CODE);

    }
}
