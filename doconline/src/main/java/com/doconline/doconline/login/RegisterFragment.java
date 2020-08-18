package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.analytics.GATracker;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.helper.OnSignUpListener;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.User;
import com.doconline.doconline.model.UserAccount;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MEDIA_PARTNER_NAME;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_OTP_SENT;
import static com.doconline.doconline.app.Constants.PASSWORD_PATTERN;
import static com.doconline.doconline.app.MyApplication.prefs;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_REGISTER;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_SOCIAL_AUTHENTICATION;

/**
 * Created by chiranjit on 22/12/16.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener
{

    TextView tv_step_one;

    EditText edit_first_name;

    EditText edit_last_name;

    EditText edit_email;

    EditText edit_password;

    EditText edit_confirm_password;

    EditText edit_coupon_code;

    EditText edit_mobile_no;

    //AppCompatCheckBox check_coupon_code;

    Button button_sign_up;

    Button button_existing_user;

    Button button_facebook_sign_in;

    Button button_google_sign_in;

    LoginButton fb_sign_in;

    Button btnTogglePassword;

    /**
     * Google Plus Authentication variables/class
     * Signing Options
     * Google api client
     * Sign In constant to check the activity result
     */
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Facebook Authentication variables/classes
     */
    private CallbackManager callbackManager;

    /**
     * Request code variables
     */
    private static final int GP_SIGN_IN = 100;
    private static int FB_SIGN_IN;

    private Context context = null;
    private OnPageSelection page_listener;
    private OnHttpResponse response_listener;
    private OnTaskCompleted task_listener;
    private OnSignUpListener signup_listener;

    private User mProfile;
    private boolean isPasswordVisible;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    public RegisterFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public RegisterFragment(Context context, OnPageSelection page_listener, OnSignUpListener signup_listener, OnHttpResponse response_listener, OnTaskCompleted task_listener, User mProfile)
    {
        this.context = context;
        this.page_listener = page_listener;
        this.response_listener = response_listener;
        this.task_listener = task_listener;
        this.signup_listener = signup_listener;
        this.mProfile = mProfile;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        //ButterKnife.bind(this, view);
         tv_step_one = view.findViewById(R.id.tv_step_one);
         edit_first_name= view.findViewById(R.id.editFirstName);
         edit_last_name= view.findViewById(R.id.editLastName);
         edit_email= view.findViewById(R.id.editEmail);
         edit_password= view.findViewById(R.id.editPassword);
        edit_confirm_password= view.findViewById(R.id.editConfirmPassword);
         edit_coupon_code= view.findViewById(R.id.editCouponCode);
         edit_mobile_no= view.findViewById(R.id.editMobileNo);
         //check_coupon_code= view.findViewById(R.id.check_coupon);
         button_sign_up= view.findViewById(R.id.btnGo);
         button_existing_user= view.findViewById(R.id.btnExistingUser);
         button_facebook_sign_in= view.findViewById(R.id.facebook_sign_in);
        button_google_sign_in= view.findViewById(R.id.google_sign_in);
        fb_sign_in= view.findViewById(R.id.facebook_sign_in_button);
         btnTogglePassword= view.findViewById(R.id.btnTogglePassword);

         btnTogglePassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!isPasswordVisible)
                 {
                     edit_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                     edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                     btnTogglePassword.setText("Hide");
                 }

                 else
                 {
                     edit_confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                     edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                     btnTogglePassword.setText("Show");
                 }

                 isPasswordVisible = !isPasswordVisible;
             }
         });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.hideKeyboard(view);
        this.addListener();
        this.setAccountFields();

        sharedPreferences = this.getActivity().getSharedPreferences ("SOCIALNWLOGIN", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        this.initFacebookAuthentication();
        this.initGoogleAuthentication();

        button_sign_up.setText(R.string.button_register);
        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
    }


    /**
     * Call this method to initialize Facebook Authentication
     */
    private void initFacebookAuthentication()
    {
        try
        {
            callbackManager = CallbackManager.Factory.create();
            fb_sign_in.setReadPermissions(Arrays.asList("public_profile", "email"));

            /**
             * Initialize Facebook Sign In request code
             */
            FB_SIGN_IN = fb_sign_in.getRequestCode();

            fb_sign_in.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
            {
                @Override
                public void onSuccess(LoginResult loginResult)
                {
                    if(AccessToken.getCurrentAccessToken() != null)
                    {
                        String access_token = AccessToken.getCurrentAccessToken().getToken();
                        LoginManager.getInstance().logOut();

                        if(!new InternetConnectionDetector(context).isConnected())
                        {
                            new CustomAlertDialog(getContext(), RegisterFragment.this, getView()).snackbarForInternetConnectivity();
                            return;
                        }

                        new HttpClient(HTTP_REQUEST_CODE_SOCIAL_AUTHENTICATION, MyApplication.HTTPMethod.POST.getValue(),
                                UserAccount.composeSocialAuthenticationJSON(access_token, "facebook"), RegisterFragment.this)
                                .execute(mController.getOAuthURL());

                        editor.putBoolean("status", true);
                        editor.putString("type", "facebook");
                        editor.commit();

                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException exception) {
                }
            });
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Call this method to initialize Google Authentication
     */
    private void initGoogleAuthentication()
    {
        try
        {
            /**
             * Initializing google sign in option
             */
            /*gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build();*/

            /**
             * Initializing google api client
             */
           /* mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();*/


        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }


    private void addListener()
    {
        button_sign_up.setOnClickListener(this);
        button_existing_user.setOnClickListener(this);
        button_google_sign_in.setOnClickListener(this);
        button_facebook_sign_in.setOnClickListener(this);
        fb_sign_in.setOnClickListener(this);

        /*check_coupon_code.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                edit_coupon_code.setText("");

                if(isChecked)
                {
                    edit_coupon_code.setVisibility(View.VISIBLE);
                }

                else
                {
                    edit_coupon_code.setVisibility(View.GONE);
                }
            }
        });*/
    }


    private void hideKeyboard(final View rootView) {

        edit_confirm_password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (edit_password.getText().toString().equals(edit_confirm_password.getText().toString()))
                {
                    try
                    {
                        if(getActivity() != null)
                        {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                            if(imm != null)
                            {
                                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                            }
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void sign_up(User profile)
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            new CustomAlertDialog(getContext(), RegisterFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        HashMap<String, Object> map = UserAccount.composeBasicAuthenticationJSON(profile, "");
        map.put(KEY_MEDIA_PARTNER_NAME,sharedPreferences.getString("mediapartnername",""));

        new HttpClient(HTTP_REQUEST_CODE_REGISTER, MyApplication.HTTPMethod.POST.getValue(), map,this)
                .execute(mController.getSignOutURL());
    }


    @Override
    public void onClick(View view)
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            new CustomAlertDialog(getContext(), RegisterFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        int id = view.getId();
        if (id == R.id.btnGo) {
            this.initAccountData();

            if (!isValidFirstName(mProfile.getFirstName())) {
                return;
            }

            if (!isValidLastName(mProfile.getLastName())) {
                return;
            }

            if (mProfile.getUserAccount().getRegEmail().isEmpty() && mProfile.getUserAccount().getRegMobileNo().isEmpty()) {
                Toast.makeText(context, "Mobile No./Email Required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(mProfile.getUserAccount().getRegEmail())
                    || !isValidMobileNo(mProfile.getUserAccount().getRegMobileNo())) {
                return;
            }

            if (isValidPassword(mProfile.getUserAccount().getPassword())
                    && isConfirmedPassword(mProfile.getUserAccount().getPassword(), edit_confirm_password.getText().toString())
                /*&& isValidCouponCode(edit_coupon_code.getText().toString())*/) {
                sign_up(mProfile);
            }
        } else if (id == R.id.google_sign_in) {
            googleSignIn();
        } else if (id == R.id.facebook_sign_in) {
            fb_sign_in.performClick();
        } else if (id == R.id.facebook_sign_in_button) {
        } else if (id == R.id.btnExistingUser) {
            this.mProfile.getUserAccount().setPassword("");
            this.mProfile.getUserAccount().setRegEmail("");
            this.mProfile.getUserAccount().setConfirmPassword("");
            this.mProfile.getUserAccount().setRegMobileNo("");

            page_listener.onPageSelection(0, "SIGN IN");
        }
    }


    private void setAccountFields()
    {
        edit_first_name.setText(mProfile.getFirstName());
        edit_last_name.setText(mProfile.getLastName());
        edit_email.setText(mProfile.getUserAccount().getRegEmail());
        edit_mobile_no.setText(mProfile.getUserAccount().getRegMobileNo());
        edit_password.setText(mProfile.getUserAccount().getPassword());
        edit_confirm_password.setText(mProfile.getUserAccount().getConfirmPassword());

        /*if(mProfile.getUserAccount().isCouponEnabled() == 1)
        {
            edit_coupon_code.setVisibility(View.VISIBLE);
            edit_coupon_code.setText(mProfile.getUserAccount().getCouponCode());
        }

        else
        {
            edit_coupon_code.setVisibility(View.GONE);
            edit_coupon_code.setText("");
        }*/
    }


    private void initAccountData()
    {
        mProfile.setFirstName(edit_first_name.getText().toString());
        mProfile.setLastName(edit_last_name.getText().toString());

        mProfile.getUserAccount().setRegEmail(edit_email.getText().toString());
        mProfile.getUserAccount().setRegMobileNo(edit_mobile_no.getText().toString());
        mProfile.getUserAccount().setPassword(edit_password.getText().toString());
        mProfile.getUserAccount().setConfirmPassword(edit_confirm_password.getText().toString());
        mProfile.getUserAccount().setFcmRegId(prefs.getString(Constants.TOKEN, ""));

        if (edit_coupon_code.getText().toString().equalsIgnoreCase("")){
            mProfile.getUserAccount().setCouponEnabled(0);
        }else {
            mProfile.getUserAccount().setCouponEnabled(1);
        }
        /*if(check_coupon_code.isChecked())
        {
            mProfile.getUserAccount().setCouponEnabled(1);
        }

        else
        {
            mProfile.getUserAccount().setCouponEnabled(0);
        }*/

        mProfile.getUserAccount().setCouponCode(edit_coupon_code.getText().toString());
    }


    private boolean isValidMobileNo(String mobile_no)
    {
        /*if(mobile_no == null || TextUtils.isEmpty(mobile_no))
        {
            edit_mobile_no.setError("Mobile Number Required");
            edit_mobile_no.requestFocus();
            return false;
        }

        else if(mobile_no.length() != 10)
        {
            edit_mobile_no.setError("Must be 10 digits!");
            edit_mobile_no.requestFocus();
            return false;
        }*/


        if(!(mobile_no == null || TextUtils.isEmpty(mobile_no)) && mobile_no.length() != 10)
        {
            edit_mobile_no.setError("Must be 10 digits!");
            edit_mobile_no.requestFocus();
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email)
    {
        /*if(email == null || TextUtils.isEmpty(email))
        {
            edit_email.setError("Email Required");
            edit_email.requestFocus();
            return false;
        }

        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edit_email.setError("Invalid Email");
            edit_email.requestFocus();
            return false;
        }*/

        if(!(email == null || TextUtils.isEmpty(email)) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edit_email.setError("Invalid Email");
            edit_email.requestFocus();
            return false;
        }

        /*else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edit_email.setError("Invalid Email");
            edit_email.requestFocus();
            return false;
        }*/

        return true;
    }

    private boolean isValidPassword(String password)
    {
        if(password.length() < 8)
        {
            edit_password.setError("Minimum 8 Characters");
            edit_password.requestFocus();
            return false;
        }

        if(password.contains(" "))
        {
            edit_password.setError("Blank spaces not allowed.");
            edit_password.requestFocus();
            return false;
        }

        if(!password.matches(PASSWORD_PATTERN))
        {
            edit_password.setError("Must contain at least one digit, uppercase letter, lowercase letter, and special character.");
            edit_password.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isConfirmedPassword(String password, String confirm_password)
    {
        if(!password.equals(confirm_password))
        {
            edit_confirm_password.setError("Password Confirmation Error");
            edit_confirm_password.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidFirstName(String f_name)
    {
        if(TextUtils.isEmpty(f_name))
        {
            edit_first_name.setError("Enter First Name");
            edit_first_name.requestFocus();
            return false;
        }

        if(f_name.trim().length() < 3)
        {
            edit_first_name.setError("Minimum 3 Characters!");
            edit_first_name.requestFocus();
            return false;
        }

        return true;
    }


    private boolean isValidLastName(String l_name)
    {
        if(TextUtils.isEmpty(l_name))
        {
            edit_last_name.setError("Enter Last Name");
            edit_last_name.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidCouponCode(String code)
    {
        /*if(check_coupon_code.isChecked() && TextUtils.isEmpty(code))
        {
            edit_coupon_code.setError("Enter Coupon Code");
            edit_coupon_code.requestFocus();
            return false;
        }*/

        return true;
    }


    /**
     * Codes for Google Plus Authentication
     * @param connectionResult - Pass Google Plus connection result
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    /**
     * This function will option signing intent
     */
    private void googleSignIn()
    {
        /**
         * Creating an intent
         * Starting intent for result
         */
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GP_SIGN_IN);
    }

    /**
     * This function will option sign out intent
     */
    private void googleSignOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(

                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status)
                    {
                        //Toast.makeText(context, "Sign Out" + status, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Call when activity result
     * @param requestCode - Pass request code
     * @param resultCode - Pass result code
     * @param data - Pass data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * If Google Sign In
         */
        if (requestCode == GP_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            /**
             * Calling a new function to handle sign in
             */
            handleSignInResult(result);
        }

        /**
         * If Facebook Sign In
         */
        if (requestCode == FB_SIGN_IN)
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * After the signing we are calling this function
     * @param result - pass GoogleSignInResult object
     */
    private void handleSignInResult(GoogleSignInResult result)
    {
        /**
         * If the login succeed
         */
        if (result.isSuccess())
        {
            /**
             * Getting google account
             */
            GoogleSignInAccount acct = result.getSignInAccount();

            if(acct != null)
            {
                String access_token = String.valueOf(acct.getIdToken());
                String email = String.valueOf(acct.getEmail());

                Log.i("GOOGLE_ID_TOKEN", "" + acct.getIdToken());
                Log.i("GOOGLE_ID_TOKEN", "" + email);

                googleSignOut();

                if(!new InternetConnectionDetector(context).isConnected())
                {
                    new CustomAlertDialog(getContext(), RegisterFragment.this, getView()).snackbarForInternetConnectivity();
                    return;
                }

                new RetrieveTokenTask().execute(email);

                /*new HttpClient(HTTP_REQUEST_CODE_REGISTER, MyApplication.HTTPMethod.POST.getValue(),
                        UserAccount.composeSocialAuthenticationJSON(access_token, "google"),RegisterFragment.this)
                        .execute(mController.getOAuthURL());*/
            }

            else
            {
                Toast.makeText(context, "Failed to Authenticate", Toast.LENGTH_LONG).show();
            }
        }

        else
        {
            /**
             * If login fails
             */
            Toast.makeText(context, "Authentication Failure", Toast.LENGTH_LONG).show();
        }
    }


    private void initSession(User profile)
    {
        try
        {
            mController.getSession().createLoginSession(profile);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(getActivity() != null)
            {
                getActivity().finish();
            }
        }
    }


    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
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
            if(requestCode == HTTP_REQUEST_CODE_REGISTER && responseCode == HttpClient.CREATED)
            {
                mProfile.getUserAccount().setPassword("");
                mProfile.getUserAccount().setRegEmail("");

                /**
                 * Track registration success event (Google Analytics)
                 */
                GATracker.trackEvent(GATracker.GA_CATEGORY_REGISTRATION, GATracker.GA_ACTION_REGISTRATION_SUCCESS, GATracker.GA_LABEL_ANDROID, 0);

                editor.putBoolean("status", true);
                editor.putString("type", "google");
                editor.commit();

                page_listener.onPageSelection(6, "EMAIL CONFIRMATION");
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_REGISTER && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    json = json.getJSONObject(KEY_DATA);

                    if(json.getBoolean(KEY_OTP_SENT))
                    {
                        signup_listener.onMobileSignUp(mProfile, message);
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_SOCIAL_AUTHENTICATION && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    if(json.has(Constants.KEY_ERROR) && json.has(KEY_MESSAGE))
                    {
                        new CustomAlertDialog(context, this, getView()).showSnackbar(json.getString(KEY_MESSAGE), CustomAlertDialog.LENGTH_LONG);
                        return;
                    }

                    if(json.has(Constants.KEY_TOKEN_TYPE) && json.has(Constants.KEY_EXPIRES_IN) && json.has(Constants.KEY_ACCESS_TOKEN) && json.has(Constants.KEY_REFRESH_TOKEN))
                    {
                        OAuth.saveOAuthCredential(json);
                        this.initSession(mProfile);
                        return;
                    }

                    new CustomAlertDialog(getContext(), this, getView()).
                            showSnackbar(getResources().getString(R.string.error_http_default), CustomAlertDialog.LENGTH_LONG);
                }

                catch (Exception e)
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
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }

    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }


    private class RetrieveTokenTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = "";

            try
            {
                token = GoogleAuthUtil.getToken(context, accountName, scopes);
            }

            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
            }

            catch (UserRecoverableAuthException e)
            {
                //startActivityForResult(e.getIntent(), GP_SIGN_IN);
            }

            catch (GoogleAuthException e)
            {
                Log.e(TAG, e.getMessage());
            }

            return token;
        }

        @Override
        protected void onPostExecute(String access_token)
        {
            super.onPostExecute(String.valueOf(access_token));

            if(String.valueOf(access_token).isEmpty())
            {
                Toast.makeText(context, "Authentication Failure", Toast.LENGTH_SHORT).show();
                return;
            }

            new HttpClient(HTTP_REQUEST_CODE_SOCIAL_AUTHENTICATION, MyApplication.HTTPMethod.POST.getValue(),
                    UserAccount.composeSocialAuthenticationJSON(String.valueOf(access_token), "google"), RegisterFragment.this)
                    .execute(mController.getOAuthURL());
        }
    }


   /* @OnClick(R.id.btnTogglePassword)
    public void onClick()
    {
        if(!this.isPasswordVisible)
        {
            edit_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setText("Hide");
        }

        else
        {
            edit_confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setText("Show");
        }

        this.isPasswordVisible = !this.isPasswordVisible;
    }*/
}