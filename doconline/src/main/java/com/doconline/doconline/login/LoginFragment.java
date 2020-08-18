package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnPageSelection;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.MOBILE_NUMBER_PATTERN;
import static com.doconline.doconline.app.Constants.MRN_PATTERN;
import static com.doconline.doconline.app.MyApplication.prefs;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_LOGIN;


/**
 * Created by chiranjit on 22/12/16.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener
{

    EditText edit_email;
    EditText edit_password;
    Button button_sign_in;
    Button button_forgot_password;
    Button button_new_user;
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

    private User mProfile;
    private boolean isPasswordVisible;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public LoginFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public LoginFragment(Context context, OnPageSelection page_listener, OnHttpResponse response_listener, User mProfile)
    {
        this.context = context;
        this.page_listener = page_listener;
        this.response_listener = response_listener;
        this.mProfile = mProfile;
        this.mController.getSession().clearSession();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        edit_email = view.findViewById(R.id.editEmail);
        edit_password = view.findViewById(R.id.editPassword);
        button_sign_in = view.findViewById(R.id.btnGo);
        button_forgot_password = view.findViewById(R.id.btnForgotPassword);
        button_new_user = view.findViewById(R.id.btnNewUser);
        button_facebook_sign_in = view.findViewById(R.id.facebook_sign_in);
        button_google_sign_in = view.findViewById(R.id.google_sign_in);
        fb_sign_in = view.findViewById(R.id.facebook_sign_in_button);
        btnTogglePassword = view.findViewById(R.id.btnTogglePassword);
        //ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(!mProfile.getUserAccount().getRegEmail().isEmpty())
        {
            edit_email.setText(String.valueOf(mProfile.getUserAccount().getRegEmail()));
        }

        if(!mProfile.getUserAccount().getPassword().isEmpty())
        {
            edit_password.setText(String.valueOf(mProfile.getUserAccount().getPassword()));
        }

        this.addListener();

        sharedPreferences = this.getActivity().getSharedPreferences ("SOCIALNWLOGIN", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        this.initFacebookAuthentication();
        //this.initGoogleAuthentication();

        button_sign_in.setText(R.string.button_login);
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
                            new CustomAlertDialog(getContext(), LoginFragment.this, getView()).snackbarForInternetConnectivity();
                            return;
                        }

                        new HttpClient(HTTP_REQUEST_CODE_LOGIN, MyApplication.HTTPMethod.POST.getValue(),
                                UserAccount.composeSocialAuthenticationJSON(access_token, "facebook"), LoginFragment.this)
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
            /*mGoogleApiClient = new GoogleApiClient.Builder(context)
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

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void addListener()
    {
        button_sign_in.setOnClickListener(this);
        button_forgot_password.setOnClickListener(this);
        button_new_user.setOnClickListener(this);
        button_google_sign_in.setOnClickListener(this);
        button_facebook_sign_in.setOnClickListener(this);
        fb_sign_in.setOnClickListener(this);
        btnTogglePassword.setOnClickListener(this);
    }

    // Manual Login
    private void sign_in(User profile)
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            new CustomAlertDialog(getContext(), LoginFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        new HttpClient(HTTP_REQUEST_CODE_LOGIN, MyApplication.HTTPMethod.POST.getValue(),
                UserAccount.composeBasicAuthenticationJSON(profile.getUserAccount()),
                LoginFragment.this).execute(mController.getOAuthURL());
    }

    private void initAccountData()
    {
        mProfile.getUserAccount().setRegEmail(edit_email.getText().toString());
        mProfile.getUserAccount().setPassword(edit_password.getText().toString());
        mProfile.getUserAccount().setFcmRegId(prefs.getString(Constants.TOKEN, ""));
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnTogglePassword) {
            if (!this.isPasswordVisible) {
                edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setText("Hide");
            } else {
                edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setText("Show");
            }

            this.isPasswordVisible = !this.isPasswordVisible;
        } else if (id == R.id.btnGo) {
            if (!new InternetConnectionDetector(context).isConnected()) {
                new CustomAlertDialog(getContext(), LoginFragment.this, getView()).snackbarForInternetConnectivity();
                return;
            }

            this.initAccountData();

            if (isValid_Email_MobileNo_MRN(mProfile.getUserAccount().getRegEmail()) && isValidPassword(mProfile.getUserAccount().getPassword())) {
                sign_in(mProfile);
            }
        } else if (id == R.id.btnForgotPassword) {
            page_listener.onPageSelection(2, "FORGOT PASSWORD");
        } else if (id == R.id.btnNewUser) {
            page_listener.onPageSelection(1, "SIGN UP");
        } else if (id == R.id.google_sign_in) {
            if (!new InternetConnectionDetector(context).isConnected()) {
                new CustomAlertDialog(getContext(), LoginFragment.this, getView()).snackbarForInternetConnectivity();
                return;
            }

            googleSignIn();
        } else if (id == R.id.facebook_sign_in) {
            if (!new InternetConnectionDetector(context).isConnected()) {
                new CustomAlertDialog(getContext(), LoginFragment.this, getView()).snackbarForInternetConnectivity();
                return;
            }

            fb_sign_in.performClick();
        } else if (id == R.id.facebook_sign_in_button) {
        }
    }

    private boolean isValid_Email_MobileNo_MRN(String input)
    {
        if(input == null || TextUtils.isEmpty(input))
        {
            edit_email.setError("Email / Mobile No. / MRN Required");
            edit_email.requestFocus();
            return false;
        }

        /*else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
                && !TextUtils.isEmpty(input) && input.length() != 10)*/

        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
                && !input.matches(MOBILE_NUMBER_PATTERN) && !input.matches(MRN_PATTERN))
        {
            edit_email.setError("Invalid Email / Mobile No. / MRN");
            edit_email.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValiEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobileNo(String mobile_no)
    {
        return TextUtils.isEmpty(mobile_no) || mobile_no.length() == 10;
    }

    private boolean isValidPassword(String password)
    {
        if(TextUtils.isEmpty(password))
        {
            edit_password.setError("Enter Password");
            edit_password.requestFocus();
            return false;
        }

        return true;
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

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
                        //Toast.makeText(getApplicationContext(), "Sign Out" + status, Toast.LENGTH_LONG).show();
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
                    new CustomAlertDialog(getContext(), LoginFragment.this, getView()).snackbarForInternetConnectivity();
                    return;
                }

                new RetrieveTokenTask().execute(email);

                /*new HttpClient(HTTP_REQUEST_CODE_LOGIN, MyApplication.HTTPMethod.POST.getValue(),
                        UserAccount.composeSocialAuthenticationJSON(access_token, "google"),LoginFragment.this)
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
            Log.v("googleresult",""+result.isSuccess());
        }
    }

    private void initSession(User profile)
    {
        try
        {
            if(isValidMobileNo(profile.getUserAccount().getRegEmail()))
            {
                profile.getUserAccount().setRegMobileNo(profile.getUserAccount().getRegEmail());
                profile.getUserAccount().setRegEmail("");
            }

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
            if(requestCode == HTTP_REQUEST_CODE_LOGIN && responseCode == HttpClient.UNAUTHORIZED)
            {
                new CustomAlertDialog(getContext(), this, getView()).showSnackbar("Invalid Credentials", CustomAlertDialog.LENGTH_LONG);
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_LOGIN && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    editor.putBoolean("status", true);
                    editor.putString("type", "google");
                    editor.commit();

                    if(json.has(Constants.KEY_ERROR) && json.has(KEY_MESSAGE))
                    {
                        new CustomAlertDialog(getContext(), this, getView()).showSnackbar(json.getString(KEY_MESSAGE), CustomAlertDialog.LENGTH_LONG);
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

            // google login
            new HttpClient(HTTP_REQUEST_CODE_LOGIN, MyApplication.HTTPMethod.POST.getValue(),
                    UserAccount.composeSocialAuthenticationJSON(String.valueOf(access_token), "google"), LoginFragment.this)
                    .execute(mController.getOAuthURL());
        }
    }




   /* @OnClick(R.id.btnTogglePassword)
    public void onClick()
    {
        if(!this.isPasswordVisible)
        {
            edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setText("Hide");
        }

        else
        {
            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setText("Show");
        }

        this.isPasswordVisible = !this.isPasswordVisible;
    }*/
}