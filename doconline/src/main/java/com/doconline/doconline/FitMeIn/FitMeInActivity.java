package com.doconline.doconline.FitMeIn;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.doconline.doconline.GPS.GoogleGpsFinder;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;

public class FitMeInActivity extends BaseActivity implements DatePickerListener, MaterialSearchBar.OnSearchActionListener, View.OnTouchListener {

    private HorizontalPicker picker;
    public static final int HTTP_REQUEST_CODE_WORKOUTS_LIST = 2;
    public static final int HTTP_REQUEST_CODE_LOCATION_SEARCH = 5;
    int REQUEST_CODE_SETTINGS = 3;
    int REQUEST_CODE_GET_LAT_LONG = 4;

    RelativeLayout layout_loading;
    RelativeLayout rootLayout;
    LinearLayout workout_list;
    String selectedDate = "";
    TextView location_search;
    RelativeLayout searchLayout;
    MaterialSearchBar searchBar;
    RelativeLayout footer;
    LinearLayout calendar_layout;

    TextView nodatalabel;

    double mLatitude = 0.0;
    double mLongitude = 0.0;
    List<String> lastSearches;

    JSONArray list_Search = null;
    Date currentDateSelected = new Date();
    String currentAddress = "";
    String currentSearchString = "";
    SuggestionsAdapter adapter;


    SuggestionsAdapter.OnItemViewClickListener listener = new SuggestionsAdapter.OnItemViewClickListener() {
        @Override
        public void OnItemClickListener(int position, View v) {
            searchLayout.setVisibility(View.GONE);
            String address = lastSearches.get(position);
            //location_search.setText(address);
            currentAddress = address;
            setSpannableText();
            LatLng p1 = getLocationFromAddress(getApplicationContext(), address);
            Log.e("BBB", "latitude : " + p1.latitude + " longitude : " + p1.longitude);
            mLatitude = p1.latitude;
            mLongitude = p1.longitude;
            if (mLongitude != 0.0 && mLatitude != 0.0) {
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                callWorkoutsListByDateandLocation(mLatitude, mLongitude, format2.format(currentDateSelected) + "");
            }
        }

        @Override
        public void OnItemDeleteListener(int position, View v) {
            //lastSearches.remove(position);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_me_in);

        rootLayout = findViewById(R.id.fitmein_home_root_layout);
        calendar_layout = findViewById(R.id.calendar_layout);
        location_search = findViewById(R.id.location_search);
        layout_loading = findViewById(R.id.layout_loading);
        nodatalabel = findViewById(R.id.nodatalabel);
        footer = findViewById(R.id.footer);
        workout_list = findViewById(R.id.workout_list);

        if (!checkLocationServiceOnOff()) {
            showLocationSettingsAlert();
        } else {
            Intent i = new Intent(FitMeInActivity.this, GoogleGpsFinder.class);
            startActivityForResult(i, REQUEST_CODE_GET_LAT_LONG);
            overridePendingTransition(0, 0);
        }
        initialiseHorizontalDatePicker();

        searchLayout = findViewById(R.id.rl_search);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Search Location");
        //searchBar.setClearIcon(0);
        //searchBar.setClearIconTint(0);
        searchBar.setSpeechMode(false);
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(this);

        searchBar.addTextChangeListener(new TextWatcher() {
            long lastChange = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("BBB", "onTextChanged : " + s);
                currentSearchString = String.valueOf(s);
                if (s.length() > 2) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (System.currentTimeMillis() - lastChange >= 300) {
                                //send request
                                callLocationSearch(currentSearchString);
                            }
                        }
                    }, 300);
                    lastChange = System.currentTimeMillis();

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("BBB", "beforeTextChanged : " + s);
               // Log.e("BBB", "beforeTextChanged : " + String.valueOf(s));

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("BBB", "afterTextChanged : " + s);
                //callLocationSearch(s.toString());
            }
        });

        location_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.VISIBLE);
                searchBar.enableSearch();
            }
        });

        createAdapter();

        //searchBar.setSuggestionsClickListener(listener);

        /*searchLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                searchLayout.setVisibility(View.GONE);
            }
        });*/

        searchBar.setOnTouchListener(this);

        recursiveLoopChildren(searchBar);

    }

    public void onClickBtn(View v) {
        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        onDateSelected(new DateTime());
    }

    public void createAdapter() {

        searchBar.setCustomSuggestionAdapter(null);
        LayoutInflater inflater = LayoutInflater.from(this);

        adapter = new SuggestionsAdapter(inflater) {
            //private final View.OnClickListener mOnClickListener = new MyOnClickListener();
            private ClickListener clickListener;

            @Override
            public void onBindSuggestionHolder(Object suggestion, RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof searchPlansViewHolder) {
                    searchPlansViewHolder viewHolder = (searchPlansViewHolder) holder;
                    viewHolder.txtSearch.setText(suggestion.toString());
                }

            }

            @Override
            public int getSingleViewHeight() {
                Log.e("BBB", "getSingleViewHeight");
                return 60;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_search_list, parent, false);

                return new searchPlansViewHolder(view);
            }

            class searchPlansViewHolder extends RecyclerView.ViewHolder {
                TextView txtSearch;

                public searchPlansViewHolder(View itemView) {
                    super(itemView);
                   // txtSearch = itemView.findViewById(R.id.medicinename);
                    txtSearch = (TextView) itemView.findViewById(R.id.medicinename);
                    txtSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchLayout.setVisibility(View.GONE);
                            String address = lastSearches.get(getAdapterPosition());
                            //location_search.setText(address);
                            currentAddress = address;
                            setSpannableText();
                            LatLng p1 = getLocationFromAddress(getApplicationContext(), address);
                            Log.e("BBB", "latitude : " + p1.latitude + " longitude : " + p1.longitude);
                            mLatitude = p1.latitude;
                            mLongitude = p1.longitude;
                            if (mLongitude != 0.0 && mLatitude != 0.0) {
                                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                                callWorkoutsListByDateandLocation(mLatitude, mLongitude, format2.format(currentDateSelected) + "");
                            }
                        }
                    });
                }
            }
        };

        searchBar.setCustomSuggestionAdapter(adapter);

    }

    public Boolean checkLocationServiceOnOff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled || isNetworkEnabled;
    }

    public void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Enable Location Service !");
        alertDialog.setMessage("Please turn on device location");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            if (checkLocationServiceOnOff()) {
                Intent i = new Intent(FitMeInActivity.this, GoogleGpsFinder.class);
                startActivityForResult(i, REQUEST_CODE_GET_LAT_LONG);
                overridePendingTransition(0, 0);
            } else {
                Toast.makeText(this, "Please turn on Location services ", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

        if (requestCode == REQUEST_CODE_GET_LAT_LONG) {
            Log.e("AAA", "longitude : " + data.getExtras().get("Longitude"));
            Log.e("AAA", "latitude : " + data.getExtras().get("Latitude"));
            mLongitude = data.getExtras().getDouble("Longitude");
            mLatitude = data.getExtras().getDouble("Latitude");
            getCurrentAddressFromLatLong();
        }
    }

    private void initialiseHorizontalDatePicker() {
        picker = findViewById(R.id.datePicker);
        picker.setOffset(3);
        picker.setDays(30);
        picker.setListener((DatePickerListener) this);
        picker.showTodayButton(false);
        picker.setDateSelectedColor(getResources().getColor(R.color.colorPrimary));
        picker.setDateSelectedTextColor(getResources().getColor(R.color.white));
        picker.setMonthAndYearTextColor(getResources().getColor(R.color.white));
        picker.setTodayDateTextColor(getResources().getColor(R.color.white));
        picker.setTodayDateBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        picker.setUnselectedDayTextColor(getResources().getColor(R.color.white));
        picker.setDayOfWeekTextColor(getResources().getColor(R.color.white));
        picker.setBackground(getResources().getDrawable(R.drawable.action_bar_bg));
        //picker.setDays(5000);
        picker.init();
        picker.setDate(new DateTime());
    }

    private void getCurrentAddressFromLatLong() {
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();// Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //location_search.setText(address);
            currentAddress = address;
            setSpannableText();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void setSpannableText() {
        Spannable spannable = new SpannableString(currentAddress + "  " + "Change");
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), (currentAddress + "  ").length(), (currentAddress + "  " + "Change").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), (currentAddress + "  ").length(), (currentAddress + "  " + "Change").length(), 0);
        //location_search.setText(spannable,TextView.BufferType.SPANNABLE);
        location_search.setText(spannable);
    }

    private void enableUserIntaraction(Boolean userIntaraction) {
        if (userIntaraction) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    private void callWorkoutsListByDateandLocation(double latitude, double longitude, String selecteddate) {
        layout_loading.setVisibility(View.VISIBLE);
        enableUserIntaraction(false);

        workout_list.removeAllViews();

        selectedDate = selecteddate;
        Gson gson = new Gson();
        HashMap<String, Object> requestObject = new HashMap<>();
        requestObject.put(Constants.KEY_FITMEIN_WORKOUT_DATE, selecteddate);
        requestObject.put(Constants.KEY_LONGITUDE, longitude);
        requestObject.put(Constants.KEY_LATITUDE, latitude);
        requestObject.put(Constants.KEY_LOCATION, currentAddress);
        String rawRequestObject = gson.toJson(requestObject);
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_WORKOUTS_LIST, MyApplication.HTTPMethod.POST.getValue(),
                    true, rawRequestObject, this).execute(mController.getWorkoutlistUrl());
        } else {
            new CustomAlertDialog(FitMeInActivity.this, this, rootLayout.getRootView()).snackbarForInternetConnectivity();
        }
    }

    private void callLocationSearch(String searchString) {
        layout_loading.setVisibility(View.VISIBLE);
        enableUserIntaraction(false);
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_LOCATION_SEARCH, MyApplication.HTTPMethod.GET.getValue(), this).
                    execute(mController.getLocationSearchUrl() + searchString);
        } else {
            new CustomAlertDialog(FitMeInActivity.this, this, rootLayout.getRootView()).snackbarForInternetConnectivity();
        }
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        try {
            String dateStr = String.valueOf(dateSelected);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format1.parse(dateStr);
            currentDateSelected = date;
            if (mLongitude != 0.0 && mLatitude != 0.0) {
                callWorkoutsListByDateandLocation(mLatitude, mLongitude, format2.format(date) + "");
            }
        } catch (Exception e) {
            Log.e("AAA", "" + e);
        }
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        layout_loading.setVisibility(View.GONE);
        enableUserIntaraction(true);
        try {
            if (requestCode == HTTP_REQUEST_CODE_WORKOUTS_LIST && responseCode == 200) {
                populateList(response);
            } else if (requestCode == HTTP_REQUEST_CODE_LOCATION_SEARCH && responseCode == 200) {
                populateLocationList(response);
            }
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(responseCode, response);

        } catch (Exception e) {
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(0, response);
            e.printStackTrace();
        }

    }

    private void populateList(String response) {
        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray listArray = responseObject.getJSONArray(Constants.KEY_DATA);
            Log.e("AAA", "object : " + listArray);
            workout_list.removeAllViews();
            if (listArray.length() > 0) {
                nodatalabel.setVisibility(View.GONE);
                workout_list.setVisibility(View.VISIBLE);
                listArray = FitMeInActivity.sortJsonArray(listArray, Constants.KEY_DISTANCE);

                for (int i = 0; i < listArray.length(); i++) {
                    final JSONObject eachObject = listArray.getJSONObject(i);

                    String toTime = eachObject.get(Constants.KEY_TO_TIME).toString();
                    if (compareDates(toTime)){
                        final LinearLayout linearLayout = (LinearLayout) View.inflate(FitMeInActivity.this, R.layout.inflater_workout_list, null);
                        ((TextView) linearLayout.findViewById(R.id.name)).setText(eachObject.get(Constants.KEY_OUTLETNAME).toString());
                        ((TextView) linearLayout.findViewById(R.id.workoutname)).setText(eachObject.get("cn").toString());
                        ((TextView) linearLayout.findViewById(R.id.location)).setText(" " + eachObject.get(Constants.KEY_AREA).toString() + ", " + eachObject.get(Constants.KEY_CITY_).toString());
                        ((TextView) linearLayout.findViewById(R.id.distance)).setText(eachObject.get(Constants.KEY_DISTANCE).toString() + "kms");
                        ((TextView) linearLayout.findViewById(R.id.timings)).setText(eachObject.get(Constants.KEY_FROM_TIME).toString() + " To " + eachObject.get(Constants.KEY_TO_TIME).toString());

                        String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
                        String workoutID = eachObject.get(Constants.KEY_WORKOUT_ID_).toString();
                        final GlideUrl glideUrl = new GlideUrl(mController.getFitMeInWorkoutImageURL() + "/" + workoutID, new LazyHeaders.Builder()
                                .addHeader(AUTHORIZATION, "Bearer " + header)
                                .addHeader(ACCEPT, "application/json")
                                .addHeader(CONTENT_TYPE, "application/json")
                                .addHeader(DOCONLINE_API, DOCONLINE_API_VERSION)
                                .build());

                        Glide.with(FitMeInActivity.this).load(glideUrl).centerCrop().placeholder(R.drawable.ic_loading).dontAnimate()
                                .into((ImageView) linearLayout.findViewById(R.id.image_view));

                        ((RelativeLayout) linearLayout.findViewById(R.id.root_layout)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(FitMeInActivity.this, WorkoutDetailsActivity.class);
                                try {
                                    i.putExtra(Constants.WOORKOUT_DETAILS_ID, eachObject.get(Constants.KEY_WORKOUT_ID).toString());
                                    i.putExtra(Constants.SELECTED_DATE, selectedDate);
                                    i.putExtra(Constants.FROM_SCREEN_KEY, Constants.MAIN_ACTIVITY);
                                    i.putExtra(Constants.DISTANCE, eachObject.get(Constants.KEY_DISTANCE).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                FitMeInActivity.this.startActivity(i);
                            }
                        });
                        workout_list.addView(linearLayout);
                    }

                }

            } else {
                nodatalabel.setVisibility(View.VISIBLE);
                workout_list.setVisibility(View.GONE);
                workout_list.removeAllViews();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean compareDates(String totime){
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);
        int ap = now.get(Calendar.AM_PM);
        String ampm = "PM";
        if(ap == Calendar.AM){
            ampm = "AM";
        }
        Date date = parseDate(hour + ":" + minute + " " + ampm);
        Date dateCompareOne = parseDate(totime);
        if (dateCompareOne.after(date)) {
            return true;
        }
        return false;
    }

    private Date parseDate(String date) {
        String inputFormat = "hh:mm a";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private void populateLocationList(String response) {
        try {
            JSONObject responseObject = new JSONObject(response);
            list_Search = responseObject.getJSONObject(Constants.KEY_DATA).getJSONArray(Constants.KEY_PREDICTIONS);
            Log.e("BBB", "object : " + list_Search);
            lastSearches = new ArrayList<>();
            for (int i = 0; i < list_Search.length(); i++) {
                final JSONObject eachObject = list_Search.getJSONObject(i);
                lastSearches.add(eachObject.get(Constants.KEY_DESCRIPTION).toString());
            }
            searchBar.updateLastSuggestions(lastSearches);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void goBack(View view) {
        finish();
    }

    public void goHistory(View view) {
        Intent i = new Intent(FitMeInActivity.this, HistoryActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    public void goStudios(View view) {
        Intent i = new Intent(FitMeInActivity.this, StudiosListActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (enabled) {
            Log.e("BBB", "onSearchStateChanged");
        } else {
            searchLayout.setVisibility(View.GONE);
            searchBar.clearSuggestions();
        }

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Log.e("BBB", "onSearchConfirmed");
        startSearch(text.toString(), true, null, true);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_BACK:
                searchLayout.setVisibility(View.GONE);
                break;
        }
    }

    public static JSONArray sortJsonArray(JSONArray array, final String key) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            try {
                jsons.add(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                Double lid = null;
                try {
                    lid = lhs.getDouble(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Double rid = null;
                try {
                    rid = rhs.getDouble(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Log.e("BBB", "onTouch");
        searchLayout.setVisibility(View.GONE);

        return false;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void recursiveLoopChildren(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                recursiveLoopChildren((ViewGroup) child);
                // DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED
            } else {
                if (child != null) {
                    // DO SOMETHING WITH VIEW
                    if (child instanceof EditText) {
                        Log.e("BBB", "EditText");
                        EditText temp = (EditText) child;
                        temp.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        temp.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                            @Override
                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                return false;
                            }
                        });
                    }
                }
            }
        }
    }
}






