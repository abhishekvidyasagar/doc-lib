package com.doconline.doconline.consultation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.MultipleImageViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.adapter.ImageWithCaptionRecyclerAdapter;
import com.doconline.doconline.adapter.LanguageRecyclerAdapter;
import com.doconline.doconline.adapter.SpinnerAdapter;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.Patient;
import com.doconline.doconline.model.PreferredLanguage;
import com.doconline.doconline.profile.FamilyMemberActivity;
import com.doconline.doconline.profile.PreferredLanguageActivity;
import com.doconline.doconline.service.WaitngTimeBroadcast;
import com.doconline.doconline.utils.FileUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.doconline.doconline.app.Constants.EEE_MMM_DD_YYYY_HH_MM_A;
import static com.doconline.doconline.app.Constants.FAMILY_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.KEY_AGE;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_EHR_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.LANGUAGE_PREFERENCES_NO;
import static com.doconline.doconline.app.Constants.LANGUAGE_PREFERENCES_YES;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.consultation.BookConsultationActivity.HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS;
import static com.doconline.doconline.ehr.EHRActivity.HTTP_REQUEST_CODE_UPDATE_CONSENT;


public class BookConsultationFragment extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, OnItemDeleted
{
    private static final int PERMISSION_REQUEST_CODE = 3;


    private RadioGroup radio_group_call_medium;
    private Spinner spinner_appointment_for_who;

    private TextView tv_waiting_time;
    private RecyclerView recyclerView;
    private RecyclerView plRecyclerView;
    private LinearLayout layout_attachments;
    private RadioButton radio_call_medium_internet;
    private ImageButton ib_edit;
    private TextView tv_label_pref_language;
    private SwitchCompat switch_allow_default;
    private SwitchCompat switch_consent;
    private  TextView tv_attach_image;
    private  EditText edit_notes;
    private TextView tv_selected_language;
    private LinearLayout layout_selected_language;
    private  TextView tv_selected_time_slot_value;
    private NestedScrollView layout_scroll_view;
    private  TextView tv_hotline_text;
    private  CheckBox cb_tandc;
    private  TextView tv_tandc;

    private ImageWithCaptionRecyclerAdapter mAdapter;
    private LanguageRecyclerAdapter plAdapter;

    private Context context;
    private OnPageSelection listener;
    private OnHttpResponse response_listener;

    private ProgressDialog pDialog;
    RelativeLayout segmentVideoOrAudio,segmentInstantOrSchedule;
    TextView btn_wait,btn_schedule;
    TextView btn_video,btn_audio;
    private SpinnerAdapter mFamilyAdapter;
    private Uri imageUri;
    private List<PreferredLanguage> pLanguages = new ArrayList<>();
    private List<Patient> mFamilyList = new ArrayList<>();

   // private ItemTouchHelper mItemTouchHelper;
    int primaryColor;

     BookConsultationFragment(Context context, OnPageSelection listener, OnHttpResponse response_listener, int primaryColor)
    {
        this.context = context;
        this.listener = listener;
        this.response_listener = response_listener;
        this.primaryColor = primaryColor;
      //  this.pDialog = new CustomAlertDialog(context, this).showLoadingAlertDialog(context, Color.parseColor("#f56234"), "Please Wait");
        context.registerReceiver(mHandleWaitingTimeReceiver, new IntentFilter(WaitngTimeBroadcast.WAITING_TIME_ACTION));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_book_consultation, container, false);
       // ButterKnife.bind(this, view);

        radio_group_call_medium = view.findViewById(R.id.radio_group_call_medium);
        spinner_appointment_for_who= view.findViewById(R.id.spinner_appointment_for_who);
        tv_waiting_time= view.findViewById(R.id.tv_waiting_value);
        recyclerView= view.findViewById(R.id.image_list);
        plRecyclerView= view.findViewById(R.id.language_list);
        layout_attachments= view.findViewById(R.id.layout_attachments);
        radio_call_medium_internet= view.findViewById(R.id.call_medium_internet);
        ib_edit= view.findViewById(R.id.ib_edit);
       tv_label_pref_language= view.findViewById(R.id.tv_label_pref_language);
        switch_allow_default= view.findViewById(R.id.switch_allow_default);
        switch_consent= view.findViewById(R.id.switch_consent);
        tv_attach_image= view.findViewById(R.id.tv_attach_image);
      //  segmentedCallGroup= view.findViewById(R.id.segmentVideoOrAudio);
      //  segmentedConsultationGroup= view.findViewById(R.id.segmentInstantOrSchedule);
        segmentVideoOrAudio = view.findViewById(R.id.segmentVideoOrAudio);
        segmentInstantOrSchedule = view.findViewById(R.id.segmentInstantOrSchedule);

        btn_video = view.findViewById(R.id.btn_video);
        btn_audio = view.findViewById(R.id.btn_audio);


        btn_wait = view.findViewById(R.id.btn_wait);
        btn_schedule = view.findViewById(R.id.btn_schedule);


        edit_notes= view.findViewById(R.id.editNotes);
        tv_selected_language= view.findViewById(R.id.tv_selected_language);
        layout_selected_language= view.findViewById(R.id.layout_selected_language);
        tv_selected_time_slot_value= view.findViewById(R.id.tv_selected_time_slot_value);
        layout_scroll_view= view.findViewById(R.id.layout_scroll_view);
        tv_hotline_text= view.findViewById(R.id.tv_hotline_text);

        cb_tandc= view.findViewById(R.id.tandc_cb);
        tv_tandc= view.findViewById(R.id.tandc_tv);


        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btn_video.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btn_video.setBackgroundColor(getResources().getColor(primaryColor));
                btn_video.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_video),null,null,null);
                btn_video.setTextColor(getResources().getColor(R.color.white));

                btn_audio.setBackgroundColor(getResources().getColor(R.color.grey_background));
                btn_audio.setTextColor(getResources().getColor(R.color.text_dim_grey));
                btn_audio.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_voice_inactive), null, null, null);


                radio_group_call_medium.setVisibility(View.GONE);
                radio_call_medium_internet.setChecked(true);
                mController.getAppointmentBookingSummery().setCallType(com.doconline.doconline.app.Constants.CALL_TYPE_VIDEO);


            }
        });
        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_audio.setBackgroundColor(getResources().getColor(primaryColor));
               // btn_audio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btn_audio.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_voice),null,null,null);
                btn_audio.setTextColor(getResources().getColor(R.color.white));

                btn_video.setBackgroundColor(getResources().getColor(R.color.grey_background));
                btn_video.setTextColor(getResources().getColor(R.color.text_dim_grey));
                btn_video.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_video_inactive), null, null, null);


                radio_group_call_medium.setVisibility(View.VISIBLE);
                mController.getAppointmentBookingSummery().setCallType(com.doconline.doconline.app.Constants.CALL_TYPE_AUDIO);
            }
        });


        btn_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btn_wait.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btn_wait.setBackgroundColor(getResources().getColor(primaryColor));
                btn_wait.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_clock_white),null,null,null);
                btn_wait.setTextColor(getResources().getColor(R.color.white));

                btn_schedule.setBackgroundColor(getResources().getColor(R.color.grey_background));
                btn_schedule.setTextColor(getResources().getColor(R.color.text_dim_grey));
                btn_schedule.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_calender), null, null, null);

                mController.getAppointmentBookingSummery().setBookingType(com.doconline.doconline.app.Constants.BOOKING_TYPE_INSTANCE);
                mController.getAppointmentBookingSummery().setAppointmentTime("");
                tv_selected_time_slot_value.setVisibility(View.GONE);




            }
        });

        btn_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // btn_schedule.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btn_schedule.setBackgroundColor(getResources().getColor(primaryColor));
                btn_schedule.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_calender_white),null,null,null);
                btn_schedule.setTextColor(getResources().getColor(R.color.white));

                btn_wait.setBackgroundColor(getResources().getColor(R.color.grey_background));
                btn_wait.setTextColor(getResources().getColor(R.color.text_dim_grey));
                btn_wait.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_clock_inactive), null, null, null);

                mController.getAppointmentBookingSummery().setBookingType(com.doconline.doconline.app.Constants.BOOKING_TYPE_SLOT);
                listener.onPageSelection(1, "Appointment Slots");
            }
        });
        return  view;
    }


    @Override
    public void onViewCreated( View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        addListener();

        // to get family members to add in spinner
        if(new InternetConnectionDetector(context).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyMemberURL());
        }

        // for uploading files.
        initImageAdapter();
        // not in use.
      //  this.initLanguageAdapter();

        tv_attach_image.setPaintFlags(tv_attach_image.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        tv_waiting_time.setText("5 Minutes");
        tv_label_pref_language.setText("Use my preferred language");
        tv_selected_language.setText(MyApplication.getInstance().getSession().getLanguagePreferenceValue());
        tv_selected_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PreferredLanguageActivity.class));
            }
        });

        // Hotline number for particular user type.
        if(mController.getSession().getIsHotline() == 1)
        {
            tv_hotline_text.setVisibility(View.VISIBLE);
        }

        else
        {
            tv_hotline_text.setVisibility(View.GONE);
        }

        //for accepting terms and conditions by doconline internal team
        cb_tandc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mController.getAppointmentBookingSummery().setTermsandConditionsStatus(b);
            }
        });

        tv_tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BookConsultationActivity)getActivity()).load_terms_and_conditions();
            }
        });

        //to get family members
       initFamilyAdapter();
        // set switch to share document with doctor
        checkConsent(mController.getSession().getDocumentConsent());
        // call api while documents switch's status is changed
        switchConsentListener();
    }

    // for documents sharing switch settings
    private void checkConsent(int consent)
    {
        if(consent == 0)
        {
            switch_consent.setChecked(false);
        }

        else
        {
            switch_consent.setChecked(true);
        }
    }

    private void initProgressAlert()
    {
        /*pDialog.getProgressHelper().setBarColor(Color.parseColor("#f56234"));
        pDialog.setTitleText("Please Wait ...");
        pDialog.setCancelable(true);*/
        pDialog.show();
    }

    private void addListener()
    {
        tv_attach_image.setOnClickListener(this);
        spinner_appointment_for_who.setOnItemSelectedListener(this);
        ib_edit.setOnClickListener(this);

        // selected language is saved
        switchLanguageListener();
        // consultation type(audio/video) is saved and also for slots
       // addConsultationTypeListener();
        // audio consultaion type is saved(voice/internet call)
        addCallMediumListener();
        // add notes is saved.
        onNoteChangeListener();
    }

    private void initFamilyAdapter()
    {
        this.mFamilyList.add(new Patient(0, Helper.toCamelCase(mController.getSession().getFullName()) + " (Myself)", mController.getSession().getAvatarLink()));

        this.mFamilyAdapter = new SpinnerAdapter(getContext(), this.mFamilyList, 1, "");
        spinner_appointment_for_who.setAdapter(this.mFamilyAdapter);
    }


    private void switchConsentListener()
    {
        switch_consent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(!new InternetConnectionDetector(getContext()).isConnected())
                {
                    Toast.makeText(getContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                try
                {
                    JSONObject json = new JSONObject();
                    json.put(KEY_DOCUMENT_CONSENT, isChecked ? 1 : 0);

                    new HttpClient(HTTP_REQUEST_CODE_UPDATE_CONSENT, MyApplication.HTTPMethod.PATCH.getValue(), true, json.toString(), BookConsultationFragment.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getEhrDocumentConsentURL());
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void switchLanguageListener()
    {
        switch_allow_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    mController.getAppointmentBookingSummery().setLanguagePreferences(LANGUAGE_PREFERENCES_YES);
                    layout_selected_language.setVisibility(View.VISIBLE);
                }

                else
                {
                    mController.getAppointmentBookingSummery().setLanguagePreferences(LANGUAGE_PREFERENCES_NO);
                    layout_selected_language.setVisibility(View.GONE);
                }

                for(int i = 0; i< pLanguages.size(); i++)
                {
                    pLanguages.get(i).setIsEnabled(isChecked);
                }

               // plAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addCallMediumListener()
    {
        radio_group_call_medium.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                View radioButton = radio_group_call_medium.findViewById(checkedId);
                int index = radio_group_call_medium.indexOfChild(radioButton);

                switch (index)
                {
                    case 0:

                        mController.getAppointmentBookingSummery().setCallMedium(com.doconline.doconline.app.Constants.CALL_MEDIUM_INTERNET);
                        break;

                    case 1:

                        if(mController.getAppointmentBookingSummery().getCallType() == com.doconline.doconline.app.Constants.CALL_TYPE_VIDEO)
                        {
                            Toast.makeText(context, "Regular call is only possible with audio", Toast.LENGTH_SHORT).show();
                            radio_call_medium_internet.setChecked(true);
                            return;
                        }

                        mController.getAppointmentBookingSummery().setCallMedium(com.doconline.doconline.app.Constants.CALL_MEDIUM_REGULAR);
                        break;
                }
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();
        notify_attachment_changes();
        tv_selected_language.setText(mController.getSession().getLanguagePreferenceValue());

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Date and time of consultation is set when visible.
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown())
        {
            if(isVisibleToUser)
            {
               // this.initLanguageAdapter();

                if(!mController.getAppointmentBookingSummery().getAppointmentTime().isEmpty())
                {
                    tv_selected_time_slot_value.setVisibility(View.VISIBLE);

                    String date = Helper.UTC_to_Local_TimeZone(mController.getAppointmentBookingSummery().getAppointmentTime());

                    try
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                        Date value = sdf.parse(date);
                        sdf = new SimpleDateFormat(EEE_MMM_DD_YYYY_HH_MM_A);
                        tv_selected_time_slot_value.setText(sdf.format(value));
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                else
                {
                    tv_selected_time_slot_value.setVisibility(View.GONE);
                }
            }
        }
    }


    private void initLanguageAdapter()
    {
        plRecyclerView.setVisibility(View.GONE);

        if(plAdapter != null)
        {
            return;
        }

        plRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        plRecyclerView.setLayoutManager(mLayoutManager);

        plAdapter = new LanguageRecyclerAdapter(context, pLanguages);

        //ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(plAdapter);
        //mItemTouchHelper = new ItemTouchHelper(callback);
        //mItemTouchHelper.attachToRecyclerView(plRecyclerView);

        plRecyclerView.setAdapter(plAdapter);


        plAdapter.SetOnItemClickListener(new LanguageRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {

            }
        });


        plAdapter.notifyDataSetChanged();
    }


   /* private void addConsultationTypeListener()
    {
        // for slot preference
        segmentedConsultationGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {

            @Override
            public void onClickedButtonPosition(int position)
            {
                switch (position)
                {
                    case 0:

                        mController.getAppointmentBookingSummery().setBookingType(com.doconline.doconline.app.Constants.BOOKING_TYPE_INSTANCE);
                        mController.getAppointmentBookingSummery().setAppointmentTime("");
                        tv_selected_time_slot_value.setVisibility(View.GONE);
                        break;

                    case 1:

                        mController.getAppointmentBookingSummery().setBookingType(com.doconline.doconline.app.Constants.BOOKING_TYPE_SLOT);
                        listener.onPageSelection(position, "Appointment Slots");
                        break;
                }
            }
        });

        // for call preference
        segmentedCallGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position)
            {
                switch(position)
                {
                    case 0:

                        radio_group_call_medium.setVisibility(View.GONE);
                        radio_call_medium_internet.setChecked(true);
                        mController.getAppointmentBookingSummery().setCallType(com.doconline.doconline.app.Constants.CALL_TYPE_VIDEO);
                        break;

                    case 1:

                        radio_group_call_medium.setVisibility(View.VISIBLE);
                        mController.getAppointmentBookingSummery().setCallType(com.doconline.doconline.app.Constants.CALL_TYPE_AUDIO);
                        break;
                }
            }
        });
    }*/

    //Spinner selected listener.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        try
        {
            //self
            if(position == 0)
            {
                mController.getAppointmentBookingSummery().setForWhom(com.doconline.doconline.app.Constants.CONSULTATION_SELF);
                mController.getAppointmentBookingSummery().setBookedForUserId(0);
                mController.getAppointmentBookingSummery().setPatientName(mFamilyList.get(position).getFullName());
            }

            //add family member
            else if(position == (mFamilyList.size() - 1) && mFamilyAdapter.getItem(position) == null)
            {
                spinner_appointment_for_who.setSelection(0);

                Intent intent = new Intent(getActivity(), FamilyMemberActivity.class);
                startActivityForResult(intent, FAMILY_REQUEST_CODE);
            }

            //consultation for family member.
            else
            {
                mController.getAppointmentBookingSummery().setForWhom(com.doconline.doconline.app.Constants.CONSULTATION_FAMILY);
                mController.getAppointmentBookingSummery().setBookedForUserId(mFamilyList.get(position).getUserId());
                mController.getAppointmentBookingSummery().setPatientName(mFamilyList.get(position).getFullName());
                mController.getAppointmentBookingSummery().setPatientAge(mFamilyList.get(position).getAge());
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0)
    {

    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.tv_attach_image) {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (mController.getAppointmentBookingSummery().getFiles().size() < mController.getAttachmentLimit()) {
                    image_picker_dialog();
                } else {
                    Toast.makeText(context, "Maximum " + mController.getAttachmentLimit() + " files allowed", Toast.LENGTH_SHORT).show();
                }
            } else {
                RequestMultiplePermission();
            }
        } else if (id == R.id.ib_edit) {
            listener.onPageSelection(2, "Languages");
        }
    }

    // Upload Files
    private void image_picker_dialog()
    {
        new MaterialDialog.Builder(context)
                .title("Options (Max " + mController.getSession().getMaxFileSize() + " MB / File)")
                .items(R.array.file_picker_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which)
                        {
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

    // uploads files from camera
    private void openCamera()
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), com.doconline.doconline.app.Constants.MEDIA_DIRECTORY_NAME);

        if(!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdir();
        }

        /**
         * Check if we're running on Android 5.0 or higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            imageUri = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        else
        {
            imageUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        try
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE);
        }

        catch (Exception e)
        {
            Toast.makeText(context, "Failed to Open Camera", Toast.LENGTH_LONG).show();
        }
    }

    // Upload files from gallery
    private void  viewMultipleImageChooser()
    {
        int limit = (int) mController.getAttachmentLimit() - mController.getAppointmentBookingSummery().getFiles().size();

        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, limit);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    // Upload files from File Explorer
    private void viewMultipleFileChooser()
    {
        int limit = (int) mController.getAttachmentLimit() - mController.getAppointmentBookingSummery().getFiles().size();

        Intent intent4 = new Intent(getActivity(), NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, limit);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, com.doconline.doconline.app.Constants.FILE_EXTENSIONS);
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        // add gallery images in recyclerview.
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

            if((mController.getAppointmentBookingSummery().getFiles().size() + images.size()) <= mController.getAttachmentLimit())
            {
                for(Image img: images)
                {
                    File file = new File(img.path);
                    long length = file.length() / 1024; // Size in KB

                    if(length <= 1024 * mController.getSession().getMaxFileSize())
                    {
                        mController.getAppointmentBookingSummery().addFile(new FileUtils(img.path, ""));
                    }

                    else
                    {
                        Toast.makeText(context, "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                    }
                }
                notify_attachment_changes();
                scrollToBottom();
            }

            else
            {
                Toast.makeText(context, "Max " + mController.getAttachmentLimit() + " files allowed.", Toast.LENGTH_LONG).show();
            }
        }

        // add camera images in recyclerview
        if(resultCode == RESULT_OK && requestCode == com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE) {

            if((mController.getAppointmentBookingSummery().getFiles().size() <= mController.getAttachmentLimit()))
            {
                if(Helper.fileExist(imageUri.getPath()))
                {
                    File file = new File(imageUri.getPath());
                    long length = file.length() / 1024; // Size in KB

                    if(length <= 1024 * mController.getSession().getMaxFileSize())
                    {
                        mController.getAppointmentBookingSummery().addFile(new FileUtils(imageUri.getPath(), ""));

                        notify_attachment_changes();
                        scrollToBottom();
                    }

                    else
                    {
                        Toast.makeText(context, "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    Toast.makeText(context, "File Not Found", Toast.LENGTH_LONG).show();
                }
            }

            else
            {
                Toast.makeText(context, "Max " + mController.getAttachmentLimit() + " files allowed.", Toast.LENGTH_LONG).show();
            }
        }

        // add files in recyclerview
        if(requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null)
        {
            ArrayList<NormalFile> files = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);

            for(NormalFile file: files)
            {
                File tempFile = new File(file.getPath());
                long length = tempFile.length() / 1024; // Size in KB

                if(length <= 1024 * mController.getSession().getMaxFileSize())
                {
                    mController.getAppointmentBookingSummery().addFile(new FileUtils(file.getPath(), ""));
                }

                else
                {
                    Toast.makeText(context, "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                }
            }

            notify_attachment_changes();
            scrollToBottom();
        }

        //add family member in spinner
        if(requestCode == FAMILY_REQUEST_CODE && resultCode == FAMILY_REQUEST_CODE)
        {
            if(new InternetConnectionDetector(context).isConnected())
            {
                //this.initProgressAlert();

                new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyMemberURL());
            }
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
        requestPermissions(new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0)
                {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadStoragePermission && WriteStoragePermission)
                    {
                        image_picker_dialog();
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(context, "Permission Denied",Toast.LENGTH_LONG).show();
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
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int WriteStoragePermissionResult = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int ReadStoragePermissionResult = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteStoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ReadStoragePermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Receiving waiting time
     */
    // add waititng time to textview
    private final BroadcastReceiver mHandleWaitingTimeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {
                int waiting_minutes = intent.getExtras().getInt(WaitngTimeBroadcast.WAITING_MINUTES);

                if(waiting_minutes < 6 && waiting_minutes > -1)
                {
                    tv_waiting_time.setText((waiting_minutes + mController.getSession().getAppointmentCallbackTimeLimit()) + "-" + (waiting_minutes + (2 * mController.getSession().getAppointmentCallbackTimeLimit())) + " Minutes");
                    BookConsultationActivity.is_instant_booking_available = true;
                }

                else if(waiting_minutes == -1)
                {
                    tv_waiting_time.setText("N/A");
                    BookConsultationActivity.is_instant_booking_available = false;
                }

                else
                {
                    tv_waiting_time.setText(waiting_minutes + "-" + (waiting_minutes + mController.getSession().getAppointmentCallbackTimeLimit()) + " Minutes");
                    BookConsultationActivity.is_instant_booking_available = true;
                }

                //WaitingDownTimer timer = new WaitingDownTimer((waiting_minutes * 1000 * 60), (1000 * 60));
                //timer.start();
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };


    private void initImageAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ImageWithCaptionRecyclerAdapter(context, this, mController.getAppointmentBookingSummery().getFiles(), true, false);
        recyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new ImageWithCaptionRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                /*if (Helper.fileExist(mController.getAppointmentBookingSummery().getFilePath(i)))
                {
                    Intent intent = new Intent(context, PinchZoomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("URL", mController.getAppointmentBookingSummery().getFilePath(i));
                    context.startActivity(intent);
                }*/

                Intent intent = new Intent(context, MultipleImageViewerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("POSITION", i);
                context.startActivity(intent);
            }
        });
    }

    // document removed.
    @Override
    public void onItemDeleted(int index, int item_id)
    {
        mController.getAppointmentBookingSummery().getFiles().remove(index);
        Toast.makeText(context, "Image Removed", Toast.LENGTH_SHORT).show();
        notify_attachment_changes();
    }

    private void notify_attachment_changes()
    {
        if(mController.getAppointmentBookingSummery().getFiles().size() != 0)
        {
            layout_attachments.setVisibility(View.VISIBLE);
        }

        else
        {
            layout_attachments.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }

    // not used
  /*  @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
      //  mItemTouchHelper.startDrag(viewHolder);
    }*/


    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
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
            if(requestCode == HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.familyMemberSpinner(json.getJSONArray(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_UPDATE_CONSENT && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    json = json.getJSONObject(KEY_DATA);
                    int ehr_document_consent = json.getInt(KEY_EHR_DOCUMENT_CONSENT);
                    mController.getSession().putDocumentConsent(ehr_document_consent);
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
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
           /* if(pDialog != null && pDialog.isShowing())
            {
                pDialog.dismiss();
            }*/

            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }

    // adding data to spinner
    private void familyMemberSpinner(JSONArray array)
    {
        int count = 1;

        try
        {
            this.mFamilyList.clear();
            this.mFamilyList.add(new Patient(0, Helper.toCamelCase(mController.getSession().getFullName()) + " (Myself)", mController.getSession().getAvatarLink()));

            for(int i=0; i<array.length(); i++)
            {
                count++;

                JSONObject json = array.getJSONObject(i);

                int user_id = json.getInt(com.doconline.doconline.app.Constants.KEY_USER_ID);
                String full_name = json.getString(com.doconline.doconline.app.Constants.KEY_FULL_NAME);
                String age = json.isNull(KEY_AGE) ? "" : json.getString(KEY_AGE);

                /*boolean bookable = json.getInt(KEY_BOOKABLE) != 0;

                if(bookable)
                {
                    this.mFamilyList.add(new Patient(user_id, full_name, age, ""));
                }*/

                this.mFamilyList.add(new Patient(user_id, full_name, age, ""));
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(mFamilyAdapter != null)
            {
                this.mFamilyAdapter.setItems(count);
            }
        }
    }


    private void onNoteChangeListener() {

        edit_notes.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mController.getAppointmentBookingSummery().setNotes(edit_notes.getText().toString());
            }
        });
    }


    private void scrollToBottom()
    {
        recyclerView.post(new Runnable() {

            @Override
            public void run()
            {
                layout_scroll_view.fullScroll(NestedScrollView.FOCUS_DOWN);
            }
        });
    }
}