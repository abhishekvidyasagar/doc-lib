package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.Paytm.checksum;
import com.doconline.doconline.R;
import com.doconline.doconline.adapter.SpinnerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.diagnostics.Adapters.DiagnosticsAddressListRecyclerAdapter;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.DatePickerFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.helper.TimePickerFragment;
import com.doconline.doconline.model.DiagnosticsCartItem;
import com.doconline.doconline.model.DiagnosticsItem;
import com.doconline.doconline.model.DiagnosticsUserAddress;
import com.doconline.doconline.model.Patient;
import com.doconline.doconline.profile.FamilyMemberActivity;
import com.doconline.doconline.profile.ProfileActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_ERROR;
import static com.doconline.doconline.app.Constants.FAMILY_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.KEY_ACTION_TYPE;
import static com.doconline.doconline.app.Constants.KEY_ADDRESS;
import static com.doconline.doconline.app.Constants.KEY_AGE;
import static com.doconline.doconline.app.Constants.KEY_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_BOOKABLE;
import static com.doconline.doconline.app.Constants.KEY_CONTACT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DETAILS;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_FULL;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_APPOINTMENT_DATE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_B2BAVAILABLE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_BENEFICIARY_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_BENEFICIARY_ID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_MOBILE_NUMBER;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ORDER_BY;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGEID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGENAME;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PARTNERIMAGE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PRINT_OUT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_TRANSACTION_ID;
import static com.doconline.doconline.app.Constants.KEY_DOCONLINE_USER_ID;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_IMAGE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.app.Constants.KEY_NOTES;
import static com.doconline.doconline.app.Constants.KEY_PIN_CODE;
import static com.doconline.doconline.app.Constants.KEY_PLATFORM;
import static com.doconline.doconline.app.Constants.KEY_PREFILL;
import static com.doconline.doconline.app.Constants.KEY_SUCCESS;
import static com.doconline.doconline.app.Constants.KEY_USER;
import static com.doconline.doconline.app.Constants.KEY_USER_ID;

//import com.doconline.doconline.helper.BaseValidator;


public class DiagnosticsBookAppointmentFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemSelectedListener,
        ExternalWalletListener {



    ImageView imgViewPackageIcon;


    TextInputLayout txtInputLayout_UserName;


    TextInputLayout txtInputLayoutEmail;


    TextInputLayout txtInputLayout_MobileNumber;


    TextInputLayout txtInputLayout_AddressLane1;


    TextInputLayout txtInputLayout_AddressLane2;


    TextInputLayout txtInputLayout_City;


    TextInputLayout txtInputLayout_State;


    TextInputLayout txtInputLayout_PinCode;

//    @BindView(R.id.input_layout_bookappointment_Date)
//    TextInputLayout txtInputLayout_Date;


    TextView tv_BookAppt_PackageName;


    ImageView imgView_BookAppt_PartnerLogo;


    Button btn_BookAppt_ChooseAddress;

//    @BindView(R.id.spinner_choose_address)
//    Spinner spinner_choose_address;


    EditText        editText_BookAppt_UserName;


    EditText        editText_BookAppt_UserEmail;


    EditText        editText_BookAppt_UserMobile;


    EditText        editText_BookAppt_UserAddress1;


    EditText        editText_BookAppt_UserAddress2;


    EditText        editText_BookAppt_City;


    EditText        editText_BookAppt_State;


    EditText        editText_BookAppt_Pincode;

//    @BindView(R.id.edit_Date)
//    EditText        editText_BookAppt_Date;
//
//    @BindView(R.id.edit_Time)
//    EditText        editText_BookAppt_Time;


    TextView tv_BookAppt_SelectDate;


    TextView tv_BookAppt_Date;


    TextView tv_BookAppt_Time;


    AppCompatCheckBox chkBox_PrintOutRequired;


    Spinner spinner_appointment_for_who_diag;

    boolean addressClickStatus = false;

    int partnerid =1;

    private Context         context = null;
    private OnHttpResponse response_listener;
    private OnDiagnosticsAppointmentBookingStatusListener listenerAppointmentBookingStatus;
    int iProductIndex;
    private String userProfileAddress1, userProfileAddress2,userProfileCity, userProfileState, userProfilePincode;


    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT = 1;
    public static final int HTTP_REQUEST_CODE_GET_PROFILE_STATE = 2;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_CONFIRM = 3;
    public static final int HTTP_REQUEST_CODE_GET_PINCODE_VERIFICATION = 4;
    public static final int HTTP_REQUEST_CODE_GET_AVAILABLE_SLOTS = 5;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST = 6;
    public static final int HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS = 7;

    public static final int PAYTM_PAYMENT_REQUEST_CODE = 11;

    public static int user_id;
    public static String address = "";
    public String appointment_id = "";

    Calendar selectedSchedule, mCalendarOpeningTime, mCalendarClosingTime;

    private String userName ="";
    private String userMobileNumber = "";

    public String getUserName() {
        return userName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public String getBookedAppointmentSchedule(){
        return new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A).format(selectedSchedule.getTime());
    }

    ArrayList<String> list_AddressTitles = new ArrayList<>();
    private Object thyrocarePartnerID = 1;

    static boolean isBookedFromCartFragment = false;

    OnLoadingStatusChangedListener loadingStatusChangedListener;

    private int minHour = -1;
    private int minMinute = -1;

    private int maxHour = 25;
    private int maxMinute = 25;
    DiagnosticsAddressListRecyclerAdapter mAddressAdapter;
    private int selectedPostion = 0;


    private List<Patient> mFamilyList = new ArrayList<>();
    private SpinnerAdapter mFamilyAdapter;

    private ProgressDialog pDialog;

    float amt;

    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        if (s.equalsIgnoreCase("paytm")) {
            Intent i = new Intent(context, checksum.class);
            i.putExtra("orderid",appointment_id);
            i.putExtra("url",mController.getCheckSumURL());
            i.putExtra("email",paymentData.getUserEmail());
            i.putExtra("phone",paymentData.getUserContact());
            i.putExtra("amount","1");
            i.putExtra("userid",user_id);

            startActivityForResult(i, PAYTM_PAYMENT_REQUEST_CODE);
        }
    }

//    @BindView(R.id.layout_spinner)
//    RelativeLayout layout_spinner;

//    @BindView(R.id.recyclerView_spinner)
//    RecyclerView recyclerView_spinner;
    public interface OnDiagnosticsAppointmentBookingStatusListener {
        void onAppointmentConfirmed(String appointmentID, String packageName, String dateOfAppointment, Boolean isFromCart);
    }

    public DiagnosticsBookAppointmentFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DiagnosticsBookAppointmentFragment(Context context, OnHttpResponse response_listener,
                                              OnDiagnosticsAppointmentBookingStatusListener appointmentListner,
                                              OnLoadingStatusChangedListener loadStatusListener)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.listenerAppointmentBookingStatus = appointmentListner;
        this.loadingStatusChangedListener = loadStatusListener;

        this.pDialog = new CustomAlertDialog(context, this).showLoadingAlertDialog(context, Color.parseColor("#f56234"), "Please Wait");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagnostics_book_appointment, container, false);
       // ButterKnife.bind(this,view);
       imgViewPackageIcon = view.findViewById(R.id.imgView_PackageIcon);

       txtInputLayout_UserName= view.findViewById(R.id.input_layout_bookappointment_username);

       txtInputLayoutEmail= view.findViewById(R.id.input_layout_bookappointment_email);

        txtInputLayout_MobileNumber= view.findViewById(R.id.input_layout_bookappointment_Mobile);

        txtInputLayout_AddressLane1= view.findViewById(R.id.input_layout_bookappointment_Addresslane1);

         txtInputLayout_AddressLane2= view.findViewById(R.id.input_layout_bookappointment_Addresslane2);

         txtInputLayout_City= view.findViewById(R.id.input_layout_bookappointment_City);

         txtInputLayout_State= view.findViewById(R.id.input_layout_bookappointment_State);

         txtInputLayout_PinCode= view.findViewById(R.id.input_layout_bookappointment_Pincode);

//    @BindView(R.id.input_layout_bookappointment_Date)
//    TextInputLayout txtInputLayout_Date;

      tv_BookAppt_PackageName= view.findViewById(R.id.tv_package_name);

         imgView_BookAppt_PartnerLogo= view.findViewById(R.id.imgView_partnerLogo);

         btn_BookAppt_ChooseAddress= view.findViewById(R.id.btn_choose_address);

//    @BindView(R.id.spinner_choose_address)
//    Spinner spinner_choose_address;

               editText_BookAppt_UserName= view.findViewById(R.id.edit_UserName);

               editText_BookAppt_UserEmail= view.findViewById(R.id.edit_UserEmail);

               editText_BookAppt_UserMobile= view.findViewById(R.id.edit_UserMobile);

              editText_BookAppt_UserAddress1= view.findViewById(R.id.edit_UserAddress1);

             editText_BookAppt_UserAddress2= view.findViewById(R.id.edit_UserAddress2);

             editText_BookAppt_City= view.findViewById(R.id.edit_City);

              editText_BookAppt_State= view.findViewById(R.id.edit_State);

            editText_BookAppt_Pincode= view.findViewById(R.id.edit_Pincode);

//    @BindView(R.id.edit_Date)
//    EditText        editText_BookAppt_Date;
//
//    @BindView(R.id.edit_Time)
//    EditText        editText_BookAppt_Time;

         tv_BookAppt_SelectDate= view.findViewById(R.id.tv_diagnostcs_bookappointment_selectDate);

         tv_BookAppt_Date= view.findViewById(R.id.txtView_Date);

        tv_BookAppt_Time= view.findViewById(R.id.txtView_Time);

         chkBox_PrintOutRequired= view.findViewById(R.id.checkBox_PrintOutRequired);

        spinner_appointment_for_who_diag= view.findViewById(R.id.spinner_appointment_for_who_diag);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initUIElements();

        if (new InternetConnectionDetector(context).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsBookAppointmentFragment.this)
                    .execute(mController.getProfileStateURL());
        }

        if(new InternetConnectionDetector(context).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyMemberURL());
        }

        editText_BookAppt_Pincode.addTextChangedListener(new TextWatcher() {

            boolean editing=false;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.v("onTextChanged","onTextChanged");
                if (new InternetConnectionDetector(context).isConnected() && s.length() == 6) {
                    new HttpClient(HTTP_REQUEST_CODE_GET_PINCODE_VERIFICATION, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsBookAppointmentFragment.this)
                            .execute(mController.getDiagnosticsPinCodeVerification()+ partnerid +"/"+s);//partner id here
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {

                Log.v("beforeTextChanged","beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editing){
                    //editing=true;
                    //editing=false;
                }
                Log.v("afterTextChanged","afterTextChanged");
            }
        });

        this.addListener();

        this.initFamilyAdapter();

    }

    private void addListener()
    {
        spinner_appointment_for_who_diag.setOnItemSelectedListener(this);
    }

    private void initFamilyAdapter() {

        this.mFamilyList.add(new Patient(0, Helper.toCamelCase(mController.getSession().getFullName()) + " (Myself)", mController.getSession().getAvatarLink()));

        this.mFamilyAdapter = new SpinnerAdapter(getContext(), this.mFamilyList, 1, "");
        spinner_appointment_for_who_diag.setAdapter(this.mFamilyAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        try {
            if (position == 0) {
                mController.getAppointmentBookingSummery().setForWhom(com.doconline.doconline.app.Constants.CONSULTATION_SELF);
                mController.getAppointmentBookingSummery().setBookedForUserId(0);
                mController.getAppointmentBookingSummery().setPatientName(mFamilyList.get(position).getFullName());
            } else if (position == (mFamilyList.size() - 1) && mFamilyAdapter.getItem(position) == null) {
                spinner_appointment_for_who_diag.setSelection(0);

                Intent intent = new Intent(getActivity(), FamilyMemberActivity.class);
                startActivityForResult(intent, FAMILY_REQUEST_CODE);
            } else {
                mController.getAppointmentBookingSummery().setForWhom(com.doconline.doconline.app.Constants.CONSULTATION_FAMILY);
                mController.getAppointmentBookingSummery().setBookedForUserId(mFamilyList.get(position).getUserId());
                mController.getAppointmentBookingSummery().setPatientName(mFamilyList.get(position).getFullName());
                mController.getAppointmentBookingSummery().setPatientAge(mFamilyList.get(position).getAge());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void initUIElements() {

        btn_BookAppt_ChooseAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addressClickStatus = false;
                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST,
                        MyApplication.HTTPMethod.GET.getValue(), DiagnosticsBookAppointmentFragment.this).
                        execute(mController.getDiagnosticsUserAddressListURL());

            }
        });

        if(mController.diagnosticsUserAddressList != null){
            int addressCount = mController.diagnosticsUserAddressList.size();
            for(DiagnosticsUserAddress addr : mController.diagnosticsUserAddressList){
                list_AddressTitles.add(addr.getStrAddressTitle());
            }


        }
        tv_BookAppt_Date.setOnClickListener(this);
        tv_BookAppt_Time.setOnClickListener(this);

        //txtInputLayout_UserName.    getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_UserName, 0, 40));
        //txtInputLayout_MobileNumber.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_MobileNumber, 0, 10));
        //txtInputLayoutEmail.        getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayoutEmail, 0, 30));
        //txtInputLayout_AddressLane1.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_AddressLane1, 10, 100));
        //txtInputLayout_AddressLane2.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_AddressLane2, 10, 100));
        //txtInputLayout_City.        getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_City, 0, 25));
        //txtInputLayout_State.       getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_State, 0, 20));
        //txtInputLayout_PinCode.     getEditText().addTextChangedListener(new CharacterCountErrorWatcher(txtInputLayout_PinCode, 5, 6));

        setBookAppointmentTimeLimits(9,0,18,0);
    }

    private void setBookAppointmentTimeLimits(int openingHour, int openingMinutes, int closingHour, int closingMinutes) {

        minHour = openingHour;
        minMinute = openingMinutes;

        maxHour = closingHour;
        maxMinute = closingMinutes;

//        mCalendarOpeningTime = Calendar.getInstance();
//        mCalendarOpeningTime.set(Calendar.HOUR, openingHour);
//        mCalendarOpeningTime.set(Calendar.MINUTE, openingMinutes);
//        mCalendarOpeningTime.set(Calendar.AM_PM, Calendar.AM);
//
//        mCalendarClosingTime = Calendar.getInstance();
//        mCalendarClosingTime.set(Calendar.HOUR, closingHour);
//        mCalendarClosingTime.set(Calendar.MINUTE, closingMinutes);
//        mCalendarClosingTime.set(Calendar.AM_PM, Calendar.PM);
//
//        Log.d("Opening Time : ", new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a").format(mCalendarOpeningTime.getTime()));
//        Log.d("Closing Time : ", new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a").format(mCalendarClosingTime.getTime()));
    }

    private boolean CheckIfScheduleTimeIsWithinTimeLimits(int hourOfDay, int minute) {

        Log.d("Schedule Time : ", new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a").format(selectedSchedule.getTime()));

        boolean validTime = true;
        if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)){
            validTime = false;
        }

        if (hourOfDay  > maxHour || (hourOfDay == maxHour && minute > maxMinute)){
            validTime = false;
        }

//        if (validTime) {
//            currentHour = hourOfDay;
//            currentMinute = minute;
//        }

        return validTime;

    }

    private void showDatePicker()
    {
        try
        {
            DatePickerFragment date = new DatePickerFragment();

            Calendar calender = Calendar.getInstance();

            Bundle args = new Bundle();
            args.putInt("min_year", calender.get(Calendar.YEAR));
            args.putInt("max_year", calender.get(Calendar.YEAR) + 1);

            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));

            date.setArguments(args);

            date.setCallBack(ondate);
            date.show(getFragmentManager(), "Select Date of appointment");
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showTimePicker()
    {
        TimePickerFragment time = new TimePickerFragment();

        final Calendar calender = Calendar.getInstance();

        Bundle args = new Bundle();

        args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);

        time.setCallBack(onTime);
        time.show(getFragmentManager(), "Select time of appointment");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar now = Calendar.getInstance();

            Calendar choosen = Calendar.getInstance();
            choosen.set(year, monthOfYear, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            if (choosen.compareTo(now) < 0)
            {
                Toast.makeText(context, "Cannot book an appointment with past date!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                if(selectedSchedule == null)
                    selectedSchedule = Calendar.getInstance();

                selectedSchedule.set(year,monthOfYear,dayOfMonth);
                tv_BookAppt_Date.setText(dateFormat.format(choosen.getTime()));
            }
        }
    };

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {

            if(selectedSchedule != null) {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

                Calendar currentTime = Calendar.getInstance();

                //Calendar dateTime = Calendar.getInstance();
                selectedSchedule.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedSchedule.set(Calendar.MINUTE, minute);

                if(CheckIfScheduleTimeIsWithinTimeLimits(selectedSchedule.get(Calendar.HOUR_OF_DAY), selectedSchedule.get(Calendar.MINUTE)))
                {
                    if (selectedSchedule.compareTo(currentTime) > 0) {
//            it's after current
                        selectedSchedule.set(Calendar.HOUR, hourOfDay);
                        selectedSchedule.set(Calendar.MINUTE, minute);
                        String time = timeFormatter.format(selectedSchedule.getTime());
                        tv_BookAppt_Time.setText(time);
                    } else {
//            it's before current'
                        Toast.makeText(context, "Cannot book an appointment with past time!", Toast.LENGTH_SHORT).show();
                        showTimePicker();
                    }
                }
                else {
                    Toast.makeText(context, "Please book an appointment time within working hours!", Toast.LENGTH_SHORT).show();
                    showTimePicker();
                }
            }
            else {
                Toast.makeText(context, "Please select the date of appointment first!", Toast.LENGTH_SHORT).show();
                showDatePicker();
            }
        }
    };

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.txtView_Date) {//                SimpleDateFormat todaysDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//                String todaysDate = todaysDateFormat.format(Calendar.getInstance().getTime());

            showDatePicker();
        } else if (id == R.id.txtView_Time) {
            if (!tv_BookAppt_Date.getText().toString().equalsIgnoreCase("")) {
                String date = getRequiredDateFormatFromAvailableFormat(tv_BookAppt_Date.getText().toString());

                new HttpClient(HTTP_REQUEST_CODE_GET_AVAILABLE_SLOTS, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsBookAppointmentFragment.this)
                        .execute(mController.getDiagnosticsAvailableSlots() + partnerid + "/" + editText_BookAppt_Pincode.getText().toString() + "/" + date);///diagnostics/appointment/time-slots/{partner_id}/{pincode}/{apt_date}

            } else {
                Toast.makeText(context, "Please Select Date", Toast.LENGTH_SHORT).show();
            }
            //showTimePicker();
        }
    }

    private String getRequiredDateFormatFromAvailableFormat(String availabledate) {
        Date date = null;
        String str = null;

        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "yyyy-MM-dd";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        try {
            date = inputFormat.parse(availabledate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void showAvailabbleTimeSlots(final ArrayAdapter<String> arrayAdapter) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Available Time Slots:-");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                tv_BookAppt_Time.setText(""+strName);
            }
        });
        builderSingle.show();
    }

    public boolean validateAppointmentForm() {
//Email ID
        if (editText_BookAppt_UserEmail.getText().toString().trim().length() < 10) {
            editText_BookAppt_UserEmail.setError("Minimum 10 Characters!");
            editText_BookAppt_UserEmail.requestFocus();
            return false;
        } else if (editText_BookAppt_UserEmail.getText().toString().trim().length() > 50) {
            editText_BookAppt_UserEmail.setError("Cannot exceed more than 50 letters!");
            editText_BookAppt_UserEmail.requestFocus();
            return false;
        }
        if (editText_BookAppt_UserName.getText().toString().trim().length() < 4) {
            editText_BookAppt_UserName.setError("Minimum 4 Characters!");
            editText_BookAppt_UserName.requestFocus();
            return false;
        } else if (editText_BookAppt_UserName.getText().toString().trim().length() > 50) {
            editText_BookAppt_UserName.setError("Cannot exceed more than 50 letters!");
            editText_BookAppt_UserName.requestFocus();
            return false;
        }
        if (editText_BookAppt_UserMobile.getText().toString().trim().length() < 10) {
            editText_BookAppt_UserMobile.setError("Mobile number should have 10 digits!");
            editText_BookAppt_UserMobile.requestFocus();
            return false;
        }
        //Address lane
        if(editText_BookAppt_UserAddress1.getText().toString().trim().length() < 5)
        {
            editText_BookAppt_UserAddress1.setError("Minimum 5 letters are required!");
            editText_BookAppt_UserAddress1.requestFocus();
            return false;
        }
        else if(editText_BookAppt_UserAddress1.getText().toString().trim().length() > 70)
        {
            editText_BookAppt_UserAddress1.setError("Cannot exceed more than 70 letters!");
            editText_BookAppt_UserAddress1.requestFocus();
            return false;
        }
//Landmark
        if(editText_BookAppt_UserAddress2.getText().toString().trim().length() < 5)
        {
            editText_BookAppt_UserAddress2.setError("Minimum 5 letters are required!");
            editText_BookAppt_UserAddress2.requestFocus();
            return false;
        }
        else if(editText_BookAppt_UserAddress2.getText().toString().trim().length() > 70)
        {
            editText_BookAppt_UserAddress2.setError("Cannot exceed more than 70 letters!");
            editText_BookAppt_UserAddress2.requestFocus();
            return false;
        }
//City
        if(editText_BookAppt_City.getText().toString().trim().length() < 5)
        {
            editText_BookAppt_City.setError("Minimum 5 letters are required!");
            editText_BookAppt_City.requestFocus();
            return false;
        }
        else if(editText_BookAppt_City.getText().toString().trim().length() > 30)
        {
            editText_BookAppt_City.setError("Cannot exceed more than 30 letters!");
            editText_BookAppt_City.requestFocus();
            return false;
        }

//Pincode
        if(editText_BookAppt_Pincode.getText().toString().trim().length() < 6)
        {
            editText_BookAppt_Pincode.setError("All 6 digits are required!");
            editText_BookAppt_Pincode.requestFocus();
            return false;
        }
        else if(editText_BookAppt_Pincode.getText().toString().trim().length() > 6)
        {
            editText_BookAppt_Pincode.setError("Cannot exceed more than 6 digits!");
            editText_BookAppt_Pincode.requestFocus();
            return false;
        }
//State
        if(editText_BookAppt_State.getText().toString().trim().length() < 5)
        {
            editText_BookAppt_State.setError("Minimum 5 letters are required!");
            editText_BookAppt_State.requestFocus();
            return false;
        }
        else if(editText_BookAppt_State.getText().toString().trim().length() > 20)
        {
            editText_BookAppt_State.setError("Cannot exceed more than 20 letters!");
            editText_BookAppt_State.requestFocus();
            return false;
        }

        else if(tv_BookAppt_Date.getText().toString().isEmpty()){
            new CustomAlertDialog(getContext(), this, getView())
                    .showSnackbar("Date field cannot be empty!", CustomAlertDialog.LENGTH_LONG);
            return false;
        }
        else if(tv_BookAppt_Time.getText().toString().isEmpty()){
            new CustomAlertDialog(getContext(), this, getView())
                    .showSnackbar("Time field cannot be empty!", CustomAlertDialog.LENGTH_LONG);
            return false;
        }
    //        Patterns.EMAIL_ADDRESS.matcher()

        userName = editText_BookAppt_UserName.getText().toString();
        userMobileNumber = editText_BookAppt_UserMobile.getText().toString();

        return true;
    }

    public void LoadFormWithDefaultAddress()
    {
        try {
            iProductIndex = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, 0);
            //if (iProductIndex != 0) {
                if (iProductIndex == 994863253){
                    chkBox_PrintOutRequired.setVisibility(View.INVISIBLE);
                    String btobobject = MyApplication.prefs.getString("object","NA");
                    if (!btobobject.equalsIgnoreCase("NA")) {
                        try {
                            JSONObject jsonObj = new JSONObject(btobobject);
                            tv_BookAppt_PackageName.setText(""+jsonObj.get("package_name").toString());
                            partnerid = (int) jsonObj.get("partner_id");
                            //tv_ProductPrice.setText(context.getResources().getString(R.string.Rupee) + " FREE ");
                            /*Glide.with(this)
                                    .load(jsonObj.getString("partner_img_url")) // image url
                                    .override(240, 80).fitCenter().into(imgView_PartnerLogo);*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    chkBox_PrintOutRequired.setVisibility(View.VISIBLE);

                    if (isBookedFromCartFragment) {
                        DiagnosticsCartItem itemCart = mController.getDiagnosticsCart().getCartItemsList().get(iProductIndex);
                        if (itemCart != null) {
                            tv_BookAppt_PackageName.setText(itemCart.getProductName());
                            partnerid = itemCart.getPartnerId();
                        }
                    } else {
                        DiagnosticsItem item = mController.getPackageDetails(iProductIndex);
                        if (item != null) {
                            tv_BookAppt_PackageName.setText(item.getStrPackage_name());
                            partnerid = item.getPartnerId();
                            //tv_ProductPrice.setText(String.valueOf(item.getPrice()));
                        }
                    }
                }
            //}
            DiagnosticsUserAddress address = mController.getUserDefaultAddress();
            fillFormData(address);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fillFormData(DiagnosticsUserAddress address) {

        if (address != null)
        {
            if(!address.getStrCity().equalsIgnoreCase(null)){
                editText_BookAppt_City.setText(address.getStrCity());
            }
            if(address.getiPincode()!=null) {
                editText_BookAppt_Pincode.setText(String.valueOf(address.getiPincode()));
            }
            if(!address.getStrState().equalsIgnoreCase(null)){
                editText_BookAppt_State.setText(address.getStrState());
            }
            if(!address.getStrAddressLane().equalsIgnoreCase(null)) {
                editText_BookAppt_UserAddress1.setText(address.getStrAddressLane());
            }
            if(!address.getStrLandmark().equalsIgnoreCase(null)) {
                editText_BookAppt_UserAddress2.setText(address.getStrLandmark());
            }
            tv_BookAppt_Date.setText("");
            tv_BookAppt_Time.setText("");

            new HttpClient(HTTP_REQUEST_CODE_GET_PINCODE_VERIFICATION, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsBookAppointmentFragment.this)
                    .execute(mController.getDiagnosticsPinCodeVerification()+partnerid+"/"+editText_BookAppt_Pincode.getText());
        }else{
            /*filling the default account address*/
            if(!userProfileAddress1.equalsIgnoreCase("null")&&!userProfileAddress2.equalsIgnoreCase("null")&&!userProfileState.equalsIgnoreCase("null")&&!userProfileCity.equalsIgnoreCase("null")&&!userProfilePincode.equalsIgnoreCase("null")) {
                editText_BookAppt_City.setText(userProfileCity);
                editText_BookAppt_Pincode.setText(userProfilePincode);
                editText_BookAppt_State.setText(userProfileState);
                editText_BookAppt_UserAddress1.setText(userProfileAddress1);
                editText_BookAppt_UserAddress2.setText(userProfileAddress2);
                tv_BookAppt_Date.setText("");
                tv_BookAppt_Time.setText("");

                new HttpClient(HTTP_REQUEST_CODE_GET_PINCODE_VERIFICATION, MyApplication.HTTPMethod.GET.getValue(), DiagnosticsBookAppointmentFragment.this)
                        .execute(mController.getDiagnosticsPinCodeVerification() + partnerid + "/" + editText_BookAppt_Pincode.getText());
            }else{
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("You do not have any address. Do you wish to add one");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, DiagnosticsUserAddressActivity.class);
                        startActivity(i);
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();

            }
        }

    }

    public void bookAppointment()
    {
        if(selectedSchedule == null) {

            OnBookAppointmentFailedToConfirm("Scheduled date is not in appropriate format!");
            return;
        }

        else if (new InternetConnectionDetector(context).isConnected()) {

            String appointmentAddURL = mController.getDiagnosticsCreateAppointmentURL();
            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT,
                    MyApplication.HTTPMethod.POST.getValue(),
                    this.composeUserJSON(), DiagnosticsBookAppointmentFragment.this).execute( appointmentAddURL );
        }
        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    public void OnRazorpayPaymentSuccess(String razorpayPaymentID)
    {
        if (new InternetConnectionDetector(context).isConnected()) {

            String appointmentConfirmURL = mController.getDiagnosticsConfirmAppointmentURL();
            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_CONFIRM,
                    MyApplication.HTTPMethod.POST.getValue(),
                    this.composeConfirmJSON(razorpayPaymentID),
                    DiagnosticsBookAppointmentFragment.this).execute( appointmentConfirmURL );
        }
        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    public HashMap<String, Object> composeUserJSON() {

        HashMap<String, Object> hashMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {

            String dateOfAppointment = sdf.format(selectedSchedule.getTime()) + " " +tv_BookAppt_Time.getText().toString()+":00";


            String completeAddress = editText_BookAppt_UserAddress1.getText()
                                    + " " +
                                    editText_BookAppt_UserAddress1.getText()
                                    + " " +
                                    editText_BookAppt_City.getText()
                                    + " " +
                                    editText_BookAppt_State.getText();

            int printOutRequired = chkBox_PrintOutRequired.isChecked() ? 1 : 0;

            hashMap.put(KEY_DIAGNOSTICS_ADDRESS_FULL,   completeAddress);
            hashMap.put(KEY_PIN_CODE,                   editText_BookAppt_Pincode.getText());
            hashMap.put(KEY_DIAGNOSTICS_MOBILE_NUMBER,  editText_BookAppt_UserMobile.getText());

            if (editText_BookAppt_UserEmail.getText().toString().equalsIgnoreCase("")){
                hashMap.put(KEY_EMAIL,"diagnosticreports@doconline.com");
            }else {
                hashMap.put(KEY_EMAIL, editText_BookAppt_UserEmail.getText());
            }
            hashMap.put(KEY_DIAGNOSTICS_ORDER_BY,       editText_BookAppt_UserName.getText());
            hashMap.put(KEY_DIAGNOSTICS_APPOINTMENT_DATE, dateOfAppointment/*"2018-03-29 12:00:00"*/);
            hashMap.put(KEY_DIAGNOSTICS_PRINT_OUT,      printOutRequired);

            String bookingto = mFamilyList.get(spinner_appointment_for_who_diag.getSelectedItemPosition()).getFullName();
            String bookingby = editText_BookAppt_UserName.getText().toString();

            if (!bookingto.toLowerCase().contains(bookingby.toLowerCase())){
                hashMap.put(KEY_DIAGNOSTICS_BENEFICIARY_ID, "{\""+mFamilyList.get(spinner_appointment_for_who_diag.getSelectedItemPosition()).getFullName()+"\":"+mFamilyList.get(spinner_appointment_for_who_diag.getSelectedItemPosition()).getUserId()+"}");
            }else {
                hashMap.put(KEY_DIAGNOSTICS_BENEFICIARY_ID, "{\"self\":0}");
            }

            hashMap.put(KEY_DIAGNOSTICS_BENEFICIARY_COUNT, 1);

            iProductIndex = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, 0);
            //if (iProductIndex != 0) {

                if (iProductIndex == 994863253){
                    String btobobject = MyApplication.prefs.getString("object","NA");
                    if (!btobobject.equalsIgnoreCase("NA")) {
                        try {
                            JSONObject jsonObj = new JSONObject(btobobject);
                            //tv_BookAppt_PackageName.setText(""+jsonObj.get("package_name").toString());
                            hashMap.put(KEY_DIAGNOSTICS_PACKAGEID, jsonObj.get("package_id").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    if (isBookedFromCartFragment)
                    {
                        DiagnosticsCartItem itemCart = mController.getDiagnosticsCart().getCartItemsList().get(iProductIndex);
                        if (itemCart != null) {
                            hashMap.put(KEY_DIAGNOSTICS_PACKAGEID, itemCart.getProductID());
                        }
                    }
                    else
                    {
                        DiagnosticsItem item = mController.getPackageDetails(iProductIndex);
                        if (item != null) {
                            hashMap.put(KEY_DIAGNOSTICS_PACKAGEID, item.getPackageID());
                        }
                    }
                }
            //}
            //hashMap.put(KEY_DIAGNOSTICS_PARTNER_ID, thyrocarePartnerID);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public HashMap<String, Object> composeConfirmJSON(String transactionID)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_DIAGNOSTICS_APPOINTMENT_ID, appointment_id);
        hashMap.put(KEY_DIAGNOSTICS_TRANSACTION_ID,transactionID);

        return hashMap;
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

        try
        {
            JSONObject json = new JSONObject(response);
            String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

            if (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED) {
                switch (requestCode) {

                    case HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST:{
                        JSONArray jsa = new JSONArray();
                        jsa = json.getJSONArray(KEY_DATA);
                        this.parseUserAddressList(jsa);
                    }

                    case HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT: {
                        try {
                            iProductIndex = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, 0);
                            //if (iProductIndex != 0) {
                                if (iProductIndex == 994863253){
                                    this.parseDiagnosticsBookingData(json.getJSONObject(KEY_DATA));
                                }else {
                                    this.parseDiagnosticsBookingData(json.getJSONObject(KEY_DATA));
                                }
                            //}

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                    break;
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_CONFIRM: {
                        //Show the appointment confirmation screen
                        this.parseAppointmentBookingConfirmedData(json.getJSONObject(KEY_DATA));
                    }
                    break;
                    case HTTP_REQUEST_CODE_GET_PROFILE_STATE: {

                        JSONObject jsonResult = new JSONObject(String.valueOf(response));
                        String data = (jsonResult.isNull(KEY_DATA)) ? "" : jsonResult.getString(KEY_DATA);
                        this.json_data_profile(data);
                    }
                    break;
                    case HTTP_REQUEST_CODE_GET_PINCODE_VERIFICATION: {
                        //editText_BookAppt_Pincode.setText(editText_BookAppt_Pincode.getText());
                        editText_BookAppt_Pincode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_icon_pincode, 0, R.drawable.ic_check_icon, 0);
                        //change image
                    }
                    break;

                    case HTTP_REQUEST_CODE_GET_AVAILABLE_SLOTS:
                        JSONArray soltsResponseObject = json.getJSONArray(KEY_DATA);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
                        if (soltsResponseObject.length() > 0){
                            for (int i = 0; i<soltsResponseObject.length(); i++){
                                arrayAdapter.add(""+soltsResponseObject.getJSONObject(i).getString("start_time"));
                            }
                            showAvailabbleTimeSlots(arrayAdapter);
                        }else {
                            Toast.makeText(context, "Slots are not available on this date. Please change the date", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS:
                        try
                        {
                            JSONObject jsonnn = new JSONObject(response);
                            this.familyMemberSpinner(jsonnn.getJSONArray(KEY_DATA));
                        }

                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        break;
                }
            }


            else if (responseCode == HttpClient.UNPROCESSABLE_ENTITY) {

                switch (requestCode) {
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT:
                        new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
                        break;
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_CONFIRM:
                        OnBookAppointmentFailedToConfirm(json.getString(KEY_MESSAGE));
                        new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
                        return;
                    case HTTP_REQUEST_CODE_GET_PROFILE_STATE:
                        new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
                        break;
                    case HTTP_REQUEST_CODE_GET_PINCODE_VERIFICATION:
                        //editText_BookAppt_Pincode.setText(editText_BookAppt_Pincode.getText()+"   "+message);
                        editText_BookAppt_Pincode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_icon_pincode, 0, R.drawable.ic_close_grey, 0);
                        //change image
                        new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
                        break;
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST:
                        String mes = HttpResponseHandler.getValidationErrorMessage(response);
                        if (mes.equalsIgnoreCase("You do not have any address, try to add one.")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setMessage("You do not have any address. Do you wish to add one");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(context, DiagnosticsUserAddressActivity.class);
                                    startActivity(i);
                                }
                            });
                            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alert.show();

                        }
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if(pDialog != null && pDialog.isShowing())
            {
                pDialog.dismiss();
            }

            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    //layout_loading.setVisibility(View.GONE);
                    loadingStatusChangedListener.hideProgressbarWithSuccess();
                }
            });
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }

    private void familyMemberSpinner(JSONArray array) {
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

                boolean bookable = json.getInt(KEY_BOOKABLE) != 0;

                if(bookable) {
                    this.mFamilyList.add(new Patient(user_id, full_name, age, ""));
                }

                //this.mFamilyList.add(new Patient(user_id, full_name, age, ""));
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

    private void parseUserAddressList(JSONArray array)
    {
        try
        {
            mController.clearDiagnosticsAddressList();
            mController.setDiagnosticsUserAddressList(DiagnosticsUserAddress.getUserAddressListFromJSON(array));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
// custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.layout_diagnostics_spinner);
            dialog.setTitle("Title...");

            RecyclerView recyclerView_spinner = dialog.findViewById(R.id.recyclerView_spinner);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView_spinner.setLayoutManager(mLayoutManager);

            mAddressAdapter = new DiagnosticsAddressListRecyclerAdapter(context);
            recyclerView_spinner.setAdapter(mAddressAdapter);


            mAddressAdapter.SetOnItemClickListener(new DiagnosticsAddressListRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(int i) {
                    //fillFormData(mController.getUserAddress(i));
                    selectedPostion = i;
                    addressClickStatus = true;
                }
            });

            Button dialogButtonOk = dialog.findViewById(R.id.buttonOk);
            dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    fillFormData(mController.getUserAddress(selectedPostion));

                    if (!addressClickStatus){
                        for (int i = 0; i < mController.diagnosticsUserAddressList.size(); i++){
                            if (mController.diagnosticsUserAddressList.get(i).isDefaultAddress()){
                                fillFormData(mController.getUserAddress(i));
                            }
                        }
                    }

                    dialog.dismiss();
                }
            });

            Button dialogButtonCancel = dialog.findViewById(R.id.buttonCancel);
            dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            ImageView dialogClose = dialog.findViewById(R.id.imgView_close);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }


    private void OnBookAppointmentFailedToConfirm(String message) {
        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_ERROR, this)
                .showAlertDialogWithPositiveAction(getResources().getString(R.string.dialog_title_diagnostics_book_appointment_error),
                        message,
                        getResources().getString(R.string.OK), false);
    }

    private void parseDiagnosticsBookingData(JSONObject jsonObject) {

        try {
            if (jsonObject.getBoolean(KEY_SUCCESS) == true)
            {
                boolean b2b_available = jsonObject.getBoolean(KEY_DIAGNOSTICS_B2BAVAILABLE);
                jsonObject.getString(KEY_DIAGNOSTICS_APPOINTMENT_DATE);
                appointment_id = jsonObject.getString(KEY_DIAGNOSTICS_APPOINTMENT_ID);
                jsonObject.getDouble(KEY_AMOUNT);
                jsonObject.getString(KEY_DIAGNOSTICS_PACKAGENAME);
                if (b2b_available){
                    OnRazorpayPaymentSuccess("");
                }else {
                    amt = (float) jsonObject.getDouble(KEY_AMOUNT);
                    startPayment((float) jsonObject.getDouble(KEY_AMOUNT));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseAppointmentBookingConfirmedData(JSONObject jsonObject) {

        try {
            if (jsonObject.getBoolean(KEY_SUCCESS) == true)
            {
                String imgURL = jsonObject.getString(KEY_DIAGNOSTICS_PARTNERIMAGE);

                String appointmentBookedDate = jsonObject.getString(KEY_DIAGNOSTICS_APPOINTMENT_DATE);
                String bookedApptID      = jsonObject.getString(KEY_DIAGNOSTICS_APPOINTMENT_ID);
                String bookedPackageName = jsonObject.getString(KEY_DIAGNOSTICS_PACKAGENAME);

                listenerAppointmentBookingStatus.onAppointmentConfirmed(bookedApptID,bookedPackageName,appointmentBookedDate,isBookedFromCartFragment);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startPayment(float amount)
    {
        final Activity activity = this.getActivity();

        final Checkout razorpayCheckout = new Checkout();
        razorpayCheckout.setFullScreenDisable(true);
        razorpayCheckout.setKeyID(mController.getSession().getRazorpayApiKey() /*"rzp_test_RCwV1FuYhP6jL9"*/);

        try
        {
            JSONObject options = new JSONObject();
            JSONObject preFill = new JSONObject();
            JSONObject notesFill = new JSONObject();

            options.put(KEY_NAME,       getResources().getString(R.string.app_name));

            options.put(KEY_IMAGE,      "https://app.doconline.com/assets/images/logo.png");
            options.put(KEY_AMOUNT,     (amount * 100));

            preFill.put(KEY_EMAIL,      editText_BookAppt_UserEmail.getText());
            preFill.put(KEY_CONTACT,    editText_BookAppt_UserMobile.getText());

            notesFill.put(KEY_ADDRESS, "DocOnline");
            notesFill.put(KEY_PLATFORM, "android");
            notesFill.put(KEY_DOCONLINE_USER_ID, user_id);
            notesFill.put(KEY_ACTION_TYPE, "onetime");

            JSONArray walletArray = new JSONArray();
            walletArray.put("paytm");
            JSONObject map = new JSONObject();
            map.put("wallets",walletArray);
            options.put("external",map);

            options.put(KEY_NOTES, notesFill);
            options.put(KEY_PREFILL, preFill);

            razorpayCheckout.open(activity, options);
        }

        catch (Exception e)
        {
            this.showDialog("Error", "Error in payment : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void json_data_profile(String data)
    {
        try
        {
            //user = User.getUserProfileFromJSON(data);

            JSONObject jsonObject = new JSONObject(data);
            JSONObject json = jsonObject.getJSONObject(KEY_USER);
            //final int mobile_verified = json.getInt(Constants.KEY_MOBILE_VERIFIED);
            //final int email_verified = json.getInt(Constants.KEY_IS_VERIFIED);
            final String mobile_number = (json.isNull(Constants.KEY_MOBILE_NO)) ? "" : json.getString(Constants.KEY_MOBILE_NO);
            final String full_name = (json.isNull(Constants.KEY_FULL_NAME)) ? "" : json.getString(Constants.KEY_FULL_NAME);
            final String email = (json.isNull(Constants.KEY_EMAIL)) ? "" : json.getString(Constants.KEY_EMAIL);

            //LoadFormWithDefaultAddress();

            if (json.isNull(Constants.KEY_GENDER) || json.isNull(Constants.KEY_DOB)){
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Profile");
                alert.setMessage("Looks like you haven't updated your profile with complete details. Please update DOB and Gender");
                alert.setCancelable(false);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, ProfileActivity.class);
                        startActivity(i);
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

                alert.show();
            }

            json = json.getJSONObject(KEY_DETAILS);

            userProfileAddress1 = json.getString("address1");
            userProfileAddress2 = json.getString("address2");
            userProfileCity = json.getString("city");
            userProfileState = json.getString("state");
            userProfilePincode = json.getString("pin_code");

            user_id = json.getInt(KEY_USER_ID);

            //user_id = user.getUserId();
            //address = user.getAddress().getFullAddress();
            editText_BookAppt_UserName.setFocusable(false);
            editText_BookAppt_UserName.setClickable(false);

            String name = full_name.replace("Mr.","").replace("Mrs.","");
            editText_BookAppt_UserName.setText(name);

            editText_BookAppt_UserEmail.setText(email);
            editText_BookAppt_UserMobile.setText(mobile_number);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
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

        new CustomAlertDialog(getContext(), this).
                showAlertDialogWithPositiveAction(title, content, context.getResources().getString(R.string.OK),true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == FAMILY_REQUEST_CODE && resultCode == FAMILY_REQUEST_CODE)
        {
            if(new InternetConnectionDetector(context).isConnected())
            {
                this.initProgressAlert();

                new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyMemberURL());
            }
        }
    }

    private void initProgressAlert()
    {
        pDialog.setCancelable(true);
        pDialog.show();
    }


}
