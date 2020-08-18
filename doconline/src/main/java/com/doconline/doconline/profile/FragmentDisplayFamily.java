package com.doconline.doconline.profile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.FamilyMemberRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnItemUpdated;
import com.doconline.doconline.helper.OnLinkAction;
import com.doconline.doconline.model.FamilyMember;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DELETE_FAMILY_MEMBER;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_UPDATE_CONSENT;
import static com.doconline.doconline.api.HttpClient.RESOURCE_LOCKED;
import static com.doconline.doconline.app.Constants.KEY_BOOKING_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_CONSENT_STATUS;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_REMOVE_FAMILY_MEMBERS;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_SEND_ACTIVATION_LINK;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_UPDATE_CONSENT_STATUS;

/**
 * Created by chiranjitbardhan on 10/08/17.
 */
public class FragmentDisplayFamily extends BaseFragment
        implements OnItemUpdated, OnItemDeleted, OnLinkAction, View.OnClickListener
{

    RecyclerView mRecyclerView;

    LinearLayout layout_empty;

    TextView empty_message;

    RelativeLayout layout_loading;

    Button btn_save;

    AppCompatCheckBox check_consent;

    TextView tv_consent_text;

    LinearLayout layout_consent;

    private boolean isParentUser;
    private boolean isConsentLoaded;

    private String name = "Allow user to book a consultation on behalf of me.";

    private FamilyMemberRecyclerAdapter mAdapter;

    private int index = -1;
    private int page_index;

    public static FragmentDisplayFamily newInstance()
    {
        return new FragmentDisplayFamily();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_display_family_members, container, false);
       // ButterKnife.bind(this, view);
        mRecyclerView  = view.findViewById(R.id.recycler_view);
         layout_empty = view.findViewById(R.id.layout_empty);
        empty_message= view.findViewById(R.id.empty_message);
         layout_loading= view.findViewById(R.id.layout_loading);
        btn_save= view.findViewById(R.id.btnDone);
        check_consent= view.findViewById(R.id.check_consent);
       tv_consent_text= view.findViewById(R.id.tv_consent_text);
         layout_consent= view.findViewById(R.id.layout_consent);

         layout_consent.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!isConsentLoaded)
                 {
                     return;
                 }

                 if(check_consent.isChecked())
                 {
                     consent_confirmation();
                 }

                 else
                 {
                     Intent intent = new Intent(getActivity(), ConsentFormActivity.class);
                     startActivityForResult(intent, 100);
                 }
             }
         });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.setHasOptionsMenu(false);
        this.initAdapter();
        this.refreshAdapter();

        btn_save.setOnClickListener(this);
        btn_save.setText("+ Add A Family Member");
        empty_message.setText("No Member Found");

        layout_empty.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);

        if(getActivity() != null)
        {
            this.isParentUser = getActivity().getIntent().getBooleanExtra("IS_PARENT_USER", false);
        }
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnDone) {
            startActivity(new Intent(getActivity(), FamilyMemberActivity.class));
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        try
        {
            if(page_index == 2 && !mController.getSession().getFamilyMemberConfig() && !mController.getFamilyMemberMessage().isEmpty() && this.isParentUser)
            {
                Toast.makeText(getContext(), mController.getFamilyMemberMessage(), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if(getView() == null)
        {
            return;
        }

        try
        {
            if(isVisibleToUser && !mController.getSession().getFamilyMemberConfig() && !mController.getFamilyMemberMessage().isEmpty() && this.isParentUser)
            {
                Toast.makeText(getContext(), mController.getFamilyMemberMessage(), Toast.LENGTH_LONG).show();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() == null)
        {
            return;
        }

        this.page_index = getActivity().getIntent().getIntExtra("PAGE", 0);
    }

    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FamilyMemberRecyclerAdapter(getContext(), this, this, this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new FamilyMemberRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {

            }
        });
    }


    private void delete_confirmation()
    {
        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DELETE_FAMILY_MEMBER, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_are_you_sure),
                        getResources().getString(R.string.dialog_content_delete_family_member),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            // Whether parent user can book child's consultation or not.
            if(!isConsentLoaded && !isParentUser)
            {
                layout_loading.setVisibility(View.VISIBLE);
                new HttpClient(HTTP_REQUEST_CODE_GET_CONSENT_STATUS, MyApplication.HTTPMethod.GET.getValue(), this).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getConsentURL());
            }

            // If parent user is there then show family members.
            if(isParentUser)
            {
                layout_empty.setVisibility(View.GONE);
                layout_loading.setVisibility(View.VISIBLE);
                new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyURL());
            }
        }
    }

    // edit child user
    @Override
    public void onItemUpdated(int index, int item_id)
    {
        this.index = index;

        Intent intent = new Intent(getActivity(), FamilyMemberActivity.class);
        intent.putExtra("MEMBER", mController.getFamilyMember(index));
        startActivity(intent);
    }

    // delete child user
    @Override
    public void onItemDeleted(int index, int item_id)
    {
        this.index = index;
        this.delete_confirmation();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(getActivity(), FamilyMemberActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void familyMember(String json_data)
    {
        try
        {
            mController.clearFamilyMember();
            mController.setFamilyMemberList(FamilyMember.getFamilyMemberListFromJSON(json_data));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            refreshAdapter();
        }
    }


    private void refreshAdapter()
    {
        try
        {
            if(mController.getFamilyMemberCount() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No Member Found");
            }

            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            if(mController.getFamilyMemberCount() < mController.getSession().getFamilyMemberAllowed())
            {
                btn_save.setVisibility(View.VISIBLE);
            }

            else
            {
                btn_save.setVisibility(View.GONE);
            }

            mAdapter.notifyDataSetChanged();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // remove family member.
    private void delete_family_member()
    {
        if(getActivity() == null)
        {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_loading.setVisibility(View.GONE);
                mController.clearFamilyMember(index);
                mAdapter.notifyItemRemoved(index);
                mAdapter.notifyItemRangeChanged(index, mController.getFamilyMemberCount());

                if(mController.getFamilyMemberCount() == 0)
                {
                    layout_empty.setVisibility(View.VISIBLE);
                }

                else
                {
                    layout_empty.setVisibility(View.GONE);
                }

                if(mController.getFamilyMemberCount() < mController.getSession().getFamilyMemberAllowed())
                {
                    btn_save.setVisibility(View.VISIBLE);
                }

                else
                {
                    btn_save.setVisibility(View.GONE);
                }

                Toast.makeText(getContext(), "Family Member Removed", Toast.LENGTH_LONG).show();
            }
        });

        index = -1;
    }

    @Override
    public void onPreExecute()
    {

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
            if(requestCode == HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.familyMember(json.getString(KEY_DATA));
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_REMOVE_FAMILY_MEMBERS && responseCode == HttpClient.NO_RESPONSE)
            {
                this.delete_family_member();
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_UPDATE_CONSENT_STATUS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_DATA);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_CONSENT_STATUS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    /**
                     * Parent User
                     */
                    if(json.isNull(KEY_DATA))
                    {
                        isParentUser = true;

                        if(new InternetConnectionDetector(getContext()).isConnected())
                        {
                            layout_consent.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.GONE);
                            layout_loading.setVisibility(View.VISIBLE);

                            new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyURL());
                        }
                    }

                    /**
                     * Child User
                     */
                    // checkbox for child user, whether parent should book chils's appointment
                    else
                    {
                        isParentUser = false;

                        json = json.getJSONObject(KEY_DATA);
                        this.name = json.getString(KEY_NAME);
                        int bookingConsent = json.isNull(KEY_BOOKING_CONSENT) ? 0 : json.getInt(KEY_BOOKING_CONSENT);

                        if(bookingConsent == 0)
                        {
                            check_consent.setChecked(false);
                        }

                        else
                        {
                            check_consent.setChecked(true);
                        }

                        SpannableString consent_text = new SpannableString(Html.fromHtml("Allow <b>" + Helper.toCamelCase(name) + "</b> to book a consultation on behalf of me."));
                        tv_consent_text.setText(consent_text);
                        layout_consent.setVisibility(View.VISIBLE);
                        btn_save.setVisibility(View.GONE);
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    isConsentLoaded = true;
                }

                return;
            }

            if(requestCode ==  HTTP_REQUEST_CODE_SEND_ACTIVATION_LINK && responseCode == HttpClient.OK)
            {
                new CustomAlertDialog(getContext(), this).
                        showAlertDialogWithoutTitle("Activation information is sent to your family member Mobile Number/Email ID. Please ensure your Family member activates his/her account in order to use DocOnline’s services.", getActivity().getResources().getString(R.string.OK),true);
                return;
            }

            if(responseCode == RESOURCE_LOCKED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);
                    this.showDialog("Information", message);
                }

                catch (JSONException e)
                {
                    new HttpResponseHandler(getContext(), this, getView()).handle(0, response);
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
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    layout_loading.setVisibility(View.GONE);
                }
            });
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
                showAlertDialogWithPositiveAction(title, content, getActivity().getResources().getString(R.string.OK),true);
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            if(requestCode == DIALOG_REQUEST_CODE_DELETE_FAMILY_MEMBER)
            {
                layout_loading.setVisibility(View.VISIBLE);

                new HttpClient(HTTP_REQUEST_CODE_REMOVE_FAMILY_MEMBERS, MyApplication.HTTPMethod.DELETE.getValue(), FragmentDisplayFamily.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyURL() + mController.getFamilyMember(index).getMemberId());
            }

            if(requestCode == DIALOG_REQUEST_CODE_UPDATE_CONSENT)
            {
                updateConsentStatus(false);
            }
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }


    @Override
    public void onNegativeAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_UPDATE_CONSENT)
        {
            check_consent.setChecked(true);
        }
    }


    private void consent_confirmation()
    {
        String consent_text = "Are you sure you don't want to allow " + name + " to book a consultation on behalf of you.";

        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_UPDATE_CONSENT, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_confirmation), consent_text,
                        getResources().getString(R.string.Yes),
                        getResources().getString(R.string.No), false);
    }


    /*@OnClick(R.id.layout_consent)
    public void onConsentClick(View view)
    {
        if(!isConsentLoaded)
        {
            return;
        }

        if(check_consent.isChecked())
        {
            consent_confirmation();
        }

        else
        {
            Intent intent = new Intent(getActivity(), ConsentFormActivity.class);
            startActivityForResult(intent, 100);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == 100 && resultCode == 100)
        {
            updateConsentStatus(true);

        }

        if(requestCode == 100 && resultCode == 200)
        {
            check_consent.setChecked(false);
        }
    }

    // send consent status to server.
    private void updateConsentStatus(boolean flag)
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            check_consent.setChecked(flag);

            String json_data = FamilyMember.composeBookingConsent(flag);

            new HttpClient(HTTP_REQUEST_CODE_UPDATE_CONSENT_STATUS, MyApplication.HTTPMethod.PATCH.getValue(), true, json_data, FragmentDisplayFamily.this).
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getConsentURL());
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    // When clicking on activation link(parent user).
    @Override
    public void onLinkClick(int index, int member_id)
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_SEND_ACTIVATION_LINK, MyApplication.HTTPMethod.GET.getValue(), FragmentDisplayFamily.this).
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getActivationURL() + member_id);
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }
}