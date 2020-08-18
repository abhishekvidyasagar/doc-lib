package com.doconline.doconline.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.MessageRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.doconline.doconline.app.Constants.KEY_AVATAR_URL;
import static com.doconline.doconline.app.Constants.KEY_BODY;
import static com.doconline.doconline.app.Constants.KEY_CDN_PHOTO_URL;
import static com.doconline.doconline.app.Constants.KEY_CREATED_AT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DOCTOR;
import static com.doconline.doconline.app.Constants.KEY_FULL_NAME;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_MCI_CODE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGES;
import static com.doconline.doconline.app.Constants.KEY_PRACTIONER_NO;
import static com.doconline.doconline.app.Constants.KEY_QUALIFICATION;
import static com.doconline.doconline.app.Constants.KEY_RATINGS;
import static com.doconline.doconline.app.Constants.KEY_SENDER_TYPE;
import static com.doconline.doconline.app.Constants.KEY_USER;
import static com.doconline.doconline.chat.ChatHistoryActivity.HTTP_REQUEST_CODE;
import static com.doconline.doconline.chat.ChatHistoryActivity.session_id;

/*import butterknife.BindView;
import butterknife.ButterKnife;*/

/**
 * Created by chiranjitbardhan on 23/08/17.
 */
public class ChatDetailsFragment extends BaseFragment {
    private OnHttpResponse response_listener;
    private OnTaskCompleted listener;

    private List<ChatMessage> mMessages = new ArrayList<>();

    RecyclerView recyclerView;
    LinearLayout layout_empty;
    TextView empty_message;
    LinearLayout doctorInfoLayout;
    CircleImageView docotorAvatarCIV;
    RatingBar ratingBar;
    TextView ratingTextView;
    TextView mciCodeTextView;
    TextView practitionerNoTextView;
    TextView qualificationTextView;
    Button dismissButton;


    private MessageRecyclerAdapter mAdapter;


    public static ChatDetailsFragment newInstance() {
        return new ChatDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_details, container, false);
       // ButterKnife.bind(this, view);

        recyclerView = view.findViewById(R.id.recycler_view);
        layout_empty = view.findViewById(R.id.layout_empty);
        empty_message= view.findViewById(R.id.empty_message);
        doctorInfoLayout= view.findViewById(R.id.layout_doctor_info);
        docotorAvatarCIV= view.findViewById(R.id.civ_doctor_avatar);
        ratingBar= view.findViewById(R.id.rating_bar);
        ratingTextView= view.findViewById(R.id.tv_rating);
        mciCodeTextView= view.findViewById(R.id.tv_mci_code);
        practitionerNoTextView= view.findViewById(R.id.tv_practitioner_no);
        qualificationTextView= view.findViewById(R.id.tv_qualification);
        dismissButton= view.findViewById(R.id.btn_dismiss);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initChatAdapter();

        empty_message.setText("No Conversation");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof OnHttpResponse) {
            response_listener = (OnHttpResponse) getActivity();
        }

        if (getActivity() instanceof OnTaskCompleted) {
            listener = (OnTaskCompleted) getActivity();
        }

        if (new InternetConnectionDetector(getContext()).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), this).execute(mController.getChatURL() + session_id);
        } else {
            new CustomAlertDialog(getContext(), ChatDetailsFragment.this, getView()).snackbarForInternetConnectivity();
        }
    }


    private void initChatAdapter() {
        if (mAdapter != null) {
            return;
        }

        mAdapter = new MessageRecyclerAdapter(getContext(), this, mMessages);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }


    private void scrollToBottom() {
        recyclerView.smoothScrollToPosition(0);
    }


    private void json_data(String json_data) {
        try {
            JSONObject json = new JSONObject(json_data);

            JSONObject doc_json = new JSONObject(json.getString(KEY_DOCTOR));
            String doctor_avatar_url = (doc_json.isNull(KEY_AVATAR_URL)) ? "" : doc_json.getString(KEY_AVATAR_URL);
            String doctor_full_name = (doc_json.isNull(KEY_FULL_NAME)) ? "" : doc_json.getString(KEY_FULL_NAME);
            String doctor_mci_no = (doc_json.isNull(KEY_MCI_CODE)) ? "" : doc_json.getString(KEY_MCI_CODE);
            String doctor_practitioner_no = (doc_json.isNull(KEY_PRACTIONER_NO)) ? "" : doc_json.getString(KEY_PRACTIONER_NO);
            String doctor_qualification = (doc_json.isNull(KEY_QUALIFICATION)) ? "" : doc_json.getString(KEY_QUALIFICATION);
            double avgRating = doc_json.getDouble(KEY_RATINGS);

            setToolbar(doctor_mci_no, doctor_practitioner_no, doctor_qualification,avgRating);

            JSONArray json_array = new JSONArray(json.getString(KEY_MESSAGES));
            for (int i = 0; i < json_array.length(); i++) {
                json = json_array.getJSONObject(i);

                String message_id = json.getString(KEY_ID);
                String body = (json.isNull(KEY_BODY)) ? "" : json.getString(KEY_BODY);
                String cdn_photo_url = (json.isNull(KEY_CDN_PHOTO_URL)) ? "" : json.getString(KEY_CDN_PHOTO_URL);
                String sender_type = (json.isNull(KEY_SENDER_TYPE)) ? "" : json.getString(KEY_SENDER_TYPE);
                String timestamp = json.getString(KEY_CREATED_AT);

                if (sender_type.equalsIgnoreCase(KEY_USER)) {
                    ChatMessage chat = new ChatMessage(message_id, body, sender_type, doctor_full_name, doctor_avatar_url, timestamp, ChatMessage.TYPE_MESSAGE_OUT);
                    chat.setCDNPhotoURL(cdn_photo_url);
                    mMessages.add(chat);
                } else {
                    ChatMessage chat = new ChatMessage(message_id, body, sender_type, doctor_full_name, doctor_avatar_url, timestamp, ChatMessage.TYPE_MESSAGE_IN);
                    chat.setCDNPhotoURL(cdn_photo_url);
                    mMessages.add(chat);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Collections.reverse(mMessages);
            mAdapter.notifyDataSetChanged();
            scrollToBottom();

            if (mMessages.size() == 0) {
                layout_empty.setVisibility(View.VISIBLE);
            } else {
                layout_empty.setVisibility(View.GONE);
            }
        }
    }

    private void setToolbar(String doctor_mci_no, String doctor_practitioner_no, final String doctor_qualification, final double avgRating) {
        final String mciCode = (doctor_mci_no == null) ? "NA" : doctor_mci_no;
        final String practitionorNo = (doctor_practitioner_no == null) ? "NA" : doctor_practitioner_no;
        String qualification = (doctor_qualification == null) ? "" : doctor_qualification;

        ChatHistoryActivity chatHistoryActivity = (ChatHistoryActivity) getActivity();
        chatHistoryActivity.mciPNoTextView.setText("MCI: " + mciCode/* + ", Reg No: " + practitionorNo*/);
        chatHistoryActivity.qualificationTextView.setText(qualification);
        chatHistoryActivity.doctorInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDoctorInfo(mciCode, practitionorNo, doctor_qualification, avgRating);
            }
        });

        chatHistoryActivity.toolbarDoctorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDoctorInfo(mciCode, practitionorNo, doctor_qualification, avgRating);
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation exitToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_to_top);
                doctorInfoLayout.startAnimation(exitToTop);
                doctorInfoLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showDoctorInfo(String mciCode, String practitionorNo, String doctor_qualification, double avgRating) {
        if (doctorInfoLayout.getVisibility() == View.GONE) {
            Animation enterFromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_bottom);
            doctorInfoLayout.startAnimation(enterFromBottom);
            doctorInfoLayout.setVisibility(View.VISIBLE);

            mciCodeTextView.setText(mciCode);
            //practitionerNoTextView.setText(practitionorNo);
            qualificationTextView.setText(doctor_qualification);
            ratingBar.setRating((float) avgRating);
            ratingTextView.setText(String.valueOf(avgRating));
        }
    }

    @Override
    public void onPreExecute() {
        response_listener.onPreExecute();
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
            if (requestCode == HTTP_REQUEST_CODE && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.json_data(json.getString(KEY_DATA));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }


    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }

    @Override
    public void onTaskCompleted(boolean flag, int code, String message) {
        listener.onTaskCompleted(flag, code, message);
    }
}