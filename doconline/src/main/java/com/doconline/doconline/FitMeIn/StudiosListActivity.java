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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.GPS.GoogleGpsFinder;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StudiosListActivity extends BaseActivity implements MaterialSearchBar.OnSearchActionListener, View.OnTouchListener {

    TextView location_search;
    RelativeLayout searchLayout;
    MaterialSearchBar searchBar;
    String currentSearchString = "";
    RelativeLayout layout_loading;

    RelativeLayout rootLayout;
    LinearLayout studios_list;
    RelativeLayout footer;
    TextView nodatalabel;

    JSONArray list_Search = null;

    public static final int HTTP_REQUEST_CODE_LOCATION_SEARCH = 1;
    public static final int HTTP_REQUEST_CODE_STUDIO_LIST = 2;

    int REQUEST_CODE_SETTINGS = 3;
    int REQUEST_CODE_GET_LAT_LONG = 4;

    double mLatitude = 0.0;
    double mLongitude = 0.0;
    List<String> lastSearches;
    SuggestionsAdapter adapter;
    String currentAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studios_list);

        rootLayout = findViewById(R.id.fitmein_home_root_layout);
        location_search = findViewById(R.id.location_search);
        layout_loading = findViewById(R.id.layout_loading);
        nodatalabel = findViewById(R.id.nodatalabel);
        footer = findViewById(R.id.footer);
        studios_list = findViewById(R.id.studios_list);

        if (!checkLocationServiceOnOff()) {
            showLocationSettingsAlert();
        } else {
            Intent i = new Intent(StudiosListActivity.this, GoogleGpsFinder.class);
            startActivityForResult(i, REQUEST_CODE_GET_LAT_LONG);
            overridePendingTransition(0, 0);
        }

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
        searchBar.setOnTouchListener(this);
        recursiveLoopChildren(searchBar);

    }

    public void goBack(View view) {
        finish();
    }

    public void goWorkoutsList(View view) {
        Intent i = new Intent(StudiosListActivity.this, FitMeInActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    public void goHistory(View view) {
        Intent i = new Intent(StudiosListActivity.this, HistoryActivity.class);
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Log.e("BBB", "onTouch");
        searchLayout.setVisibility(View.GONE);

        return false;
    }

    private void callLocationSearch(String searchString) {
        layout_loading.setVisibility(View.VISIBLE);
        enableUserIntaraction(false);
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_LOCATION_SEARCH, MyApplication.HTTPMethod.GET.getValue(), this).
                    execute(mController.getLocationSearchUrl() + searchString);
        } else {
            new CustomAlertDialog(StudiosListActivity.this, this, rootLayout.getRootView()).snackbarForInternetConnectivity();
        }
    }

    public void createAdapter() {

        searchBar.setCustomSuggestionAdapter(null);
        LayoutInflater inflater = LayoutInflater.from(this);

        adapter = new SuggestionsAdapter(inflater) {
            //private final View.OnClickListener mOnClickListener = new MyOnClickListener();
            private FitMeInActivity.ClickListener clickListener;

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
                    txtSearch = itemView.findViewById(R.id.medicinename);
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
                                callStudiosListByLocation(mLatitude, mLongitude);
                            }
                        }
                    });
                }
            }
        };

        searchBar.setCustomSuggestionAdapter(adapter);

    }

    private void callStudiosListByLocation(double latitude, double longitude) {
        layout_loading.setVisibility(View.VISIBLE);
        enableUserIntaraction(false);

        studios_list.removeAllViews();

        Gson gson = new Gson();
        HashMap<String, Object> requestObject = new HashMap<>();
        requestObject.put(Constants.KEY_LONGITUDE, longitude);
        requestObject.put(Constants.KEY_LATITUDE, latitude);
        requestObject.put(Constants.KEY_LOCATION, currentAddress);
        String rawRequestObject = gson.toJson(requestObject);
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_STUDIO_LIST, MyApplication.HTTPMethod.POST.getValue(),
                    true, rawRequestObject, this).execute(mController.getStudiolistUrl());
        } else {
            new CustomAlertDialog(StudiosListActivity.this, this, rootLayout.getRootView()).snackbarForInternetConnectivity();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            if (checkLocationServiceOnOff()) {
                Intent i = new Intent(StudiosListActivity.this, GoogleGpsFinder.class);
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

            if (mLongitude != 0.0 && mLatitude != 0.0) {
                callStudiosListByLocation(mLatitude, mLongitude);
            }else {
                Toast.makeText(this, "unable to get location", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void enableUserIntaraction(Boolean userIntaraction) {
        if (userIntaraction) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        layout_loading.setVisibility(View.GONE);
        enableUserIntaraction(true);
        try {
            if (requestCode == HTTP_REQUEST_CODE_STUDIO_LIST && responseCode == 200) {
                Log.e("AAA","Studios List : "+response);
                populateStudiosList(response);
            }else if (requestCode == HTTP_REQUEST_CODE_LOCATION_SEARCH && responseCode == 200) {
                populateLocationList(response);
            }
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(responseCode, response);

        } catch (Exception e) {
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(0, response);
            e.printStackTrace();
        }

    }

    private void populateStudiosList(String response) {
        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray listArray = responseObject.getJSONArray(Constants.KEY_DATA);
            Log.e("AAA", "object : " + listArray);
            studios_list.removeAllViews();
            if (listArray.length() > 0) {
                nodatalabel.setVisibility(View.GONE);
                studios_list.setVisibility(View.VISIBLE);
                listArray = FitMeInActivity.sortJsonArray(listArray, Constants.KEY_STUDIO_DISTANCE);

                for (int i = 0; i < listArray.length(); i++) {
                    final JSONObject eachObject = listArray.getJSONObject(i);
                    final LinearLayout linearLayout = (LinearLayout) View.inflate(StudiosListActivity.this, R.layout.inflater_workout_list, null);
                    ((TextView) linearLayout.findViewById(R.id.name)).setText(eachObject.get(Constants.KEY_STUDIO_OUTLET_NAME).toString());
                    //((TextView) linearLayout.findViewById(R.id.workoutname)).setText(eachObject.get("workouts").toString());
                    linearLayout.findViewById(R.id.workoutname).setVisibility(View.GONE);
                    ((TextView) linearLayout.findViewById(R.id.location)).setText(" " + eachObject.get(Constants.KEY_STUDIO_ADDRESS).toString());
                    ((TextView) linearLayout.findViewById(R.id.distance)).setText(eachObject.get(Constants.KEY_STUDIO_DISTANCE).toString() + "kms");
                    linearLayout.findViewById(R.id.timings).setVisibility(View.GONE);
                    linearLayout.findViewById(R.id.img).setVisibility(View.GONE);

                    linearLayout.findViewById(R.id.root_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent i = new Intent(StudiosListActivity.this, StudiosWorkoutsList.class);
                                i.putExtra(Constants.KEY_STUDIO_OUTLET_NAME,eachObject.get(Constants.KEY_STUDIO_OUTLET_NAME).toString());
                                i.putExtra(Constants.KEY_STUDIO_ADDRESS,eachObject.get(Constants.KEY_STUDIO_ADDRESS).toString());
                                i.putExtra(Constants.KEY_STUDIO_PARTNER_NAME, eachObject.get(Constants.KEY_STUDIO_PARTNER_NAME).toString());
                                i.putExtra(Constants.KEY_STUDIO_DISTANCE, eachObject.get(Constants.KEY_STUDIO_DISTANCE).toString());
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    studios_list.addView(linearLayout);
                }

            } else {
                nodatalabel.setVisibility(View.VISIBLE);
                studios_list.setVisibility(View.GONE);
                studios_list.removeAllViews();
            }


        } catch (JSONException e) {
            e.printStackTrace();
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

}
