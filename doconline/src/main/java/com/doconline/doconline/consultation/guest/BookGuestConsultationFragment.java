package com.doconline.doconline.consultation.guest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.doconline.doconline.adapter.SpinnerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.Patient;
import com.doconline.doconline.service.WaitngTimeBroadcast;
import com.doconline.doconline.utils.FileUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

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
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;


public class BookGuestConsultationFragment extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, OnItemDeleted
{
    public static final int PERMISSION_REQUEST_CODE = 1;


    RadioGroup radio_group_call_medium;
    Spinner spinner_appointment_for_who;
    TextView tv_waiting_time;
    RecyclerView recyclerView;
    LinearLayout layout_attachments;
    RadioButton radio_call_medium_internet;
    TextView tv_attach_image;
  //  SegmentedButtonGroup segmentedCallGroup;
   // SegmentedButtonGroup segmentedConsultationGroup;
    EditText edit_notes;
    TextView tv_selected_time_slot_value;
    NestedScrollView layout_scroll_view;

    private ImageWithCaptionRecyclerAdapter mAdapter;

    TextView btn_video,btn_audio , btn_wait,btn_schedule;
    private Context context;
    private OnPageSelection listener;

    private SpinnerAdapter mFamilyAdapter;
    private Uri imageUri;
    private List<Patient> mFamilyList = new ArrayList<>();

    CheckBox cb_tandc;
    TextView tv_tandc;


    public BookGuestConsultationFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public BookGuestConsultationFragment(Context context, OnPageSelection listener)
    {
        this.context = context;
        this.listener = listener;
        this.context.registerReceiver(mHandleWaitingTimeReceiver, new IntentFilter(WaitngTimeBroadcast.WAITING_TIME_ACTION));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_book_guest_consultation, container, false);
        radio_group_call_medium =view.findViewById(R.id.radio_group_call_medium);
        spinner_appointment_for_who=view.findViewById(R.id.spinner_appointment_for_who);
        tv_waiting_time=view.findViewById(R.id.tv_waiting_value);
        recyclerView=view.findViewById(R.id.image_list);
        layout_attachments=view.findViewById(R.id.layout_attachments);
        radio_call_medium_internet=view.findViewById(R.id.call_medium_internet);
        tv_attach_image=view.findViewById(R.id.tv_attach_image);
        //segmentedCallGroup=view.findViewById(R.id.segmentVideoOrAudio);
        //segmentedConsultationGroup=view.findViewById(R.id.segmentInstantOrSchedule);
        edit_notes=view.findViewById(R.id.editNotes);
        tv_selected_time_slot_value=view.findViewById(R.id.tv_selected_time_slot_value);
        layout_scroll_view=view.findViewById(R.id.layout_scroll_view);

       cb_tandc=view.findViewById(R.id.tandc_cb);
        tv_tandc=view.findViewById(R.id.tandc_tv);

        btn_video = view.findViewById(R.id.btn_video);
        btn_audio = view.findViewById(R.id.btn_audio);


        btn_wait = view.findViewById(R.id.btn_wait);
        btn_schedule = view.findViewById(R.id.btn_schedule);


        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_video.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
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
                btn_audio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
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
                btn_wait.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
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
                btn_schedule.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btn_schedule.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_calender_white),null,null,null);
                btn_schedule.setTextColor(getResources().getColor(R.color.white));

                btn_wait.setBackgroundColor(getResources().getColor(R.color.grey_background));
                btn_wait.setTextColor(getResources().getColor(R.color.text_dim_grey));
                btn_wait.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_icon_clock_inactive), null, null, null);

                btn_wait.setBackgroundColor(Color.parseColor("#f3f3f3"));
                mController.getAppointmentBookingSummery().setBookingType(com.doconline.doconline.app.Constants.BOOKING_TYPE_SLOT);
                listener.onPageSelection(1, "Appointment Slots");
            }
        });

      //  ButterKnife.bind(this, view);
        return  view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.addListener();
        this.initImageAdapter();

        tv_attach_image.setPaintFlags(tv_attach_image.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        tv_waiting_time.setText("5 Minutes");

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
                ((BookGuestConsultationActivity)getActivity()).load_terms_and_conditions();
            }
        });

        this.initFamilyAdapter();
    }


    private void addListener()
    {
        tv_attach_image.setOnClickListener(this);
        spinner_appointment_for_who.setOnItemSelectedListener(this);

        //this.addConsultationTypeListener();
        addCallMediumListener();
        onNoteChangeListener();
    }


    private void initFamilyAdapter()
    {
        this.mFamilyList.add(new Patient("My Self"));

        this.mFamilyAdapter = new SpinnerAdapter(getContext(), this.mFamilyList,1, "");
        spinner_appointment_for_who.setAdapter(this.mFamilyAdapter);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown())
        {
            if(isVisibleToUser)
            {
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


   /* private void addConsultationTypeListener()
    {
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
    }
*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        try
        {
            if(position == 0)
            {
                mController.getAppointmentBookingSummery().setForWhom(com.doconline.doconline.app.Constants.CONSULTATION_SELF);
                mController.getAppointmentBookingSummery().setBookedForUserId(0);
                mController.getAppointmentBookingSummery().setPatientName(mFamilyList.get(position).getFullName());
            }

            else if(position == (mFamilyList.size() - 1) && mFamilyAdapter.getItem(position) == null)
            {
                spinner_appointment_for_who.setSelection(0);

                new CustomAlertDialog(getContext(), this)
                        .showAlertDialogWithPositiveAction("Sorry!", "You need to be registered to use this feature (Family member)",
                                getResources().getString(R.string.OK), true);
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
    public void onNegativeAction()
    {

    }

    @Override
    public void onPositiveAction(int requestCode)
    {

    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.tv_attach_image) {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (mController.getAppointmentBookingSummery().getFiles().size() < mController.getAttachmentLimit()) {
                    image_picker_dialog();
                } else {
                    Toast.makeText(context, "Maximum " + mController.getAttachmentLimit() + " files allowed", Toast.LENGTH_SHORT).show();
                }
            } else {
                RequestMultiplePermission();
            }
        }
    }


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

    private void viewMultipleImageChooser()
    {
        int limit = (int) mController.getAttachmentLimit() - mController.getAppointmentBookingSummery().getFiles().size();

        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, limit);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
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
    private final BroadcastReceiver mHandleWaitingTimeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {
                int waiting_minutes = intent.getExtras().getInt(WaitngTimeBroadcast.WAITING_MINUTES);

                if(waiting_minutes < 6 && waiting_minutes > -1)
                {
                    tv_waiting_time.setText((waiting_minutes + 15) + "-" + (waiting_minutes + 30) + " Minutes");
                    BookGuestConsultationActivity.is_instant_booking_available = true;
                }

                else if(waiting_minutes == -1)
                {
                    tv_waiting_time.setText("N/A");
                    BookGuestConsultationActivity.is_instant_booking_available = false;
                }

                else
                {
                    tv_waiting_time.setText(waiting_minutes + "-" + (waiting_minutes + 15) + " Minutes");
                    BookGuestConsultationActivity.is_instant_booking_available = true;
                }
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
                /*if (Helper.fileExist(mController.getAppointmentBookingSummery().getFile(i).getPath()))
                {
                    Intent intent = new Intent(context, PinchZoomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("URL", mController.getAppointmentBookingSummery().getFile(i).getPath());
                    context.startActivity(intent);
                }*/

                Intent intent = new Intent(context, MultipleImageViewerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("POSITION", i);
                context.startActivity(intent);
            }
        });
    }


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