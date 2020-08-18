package com.doconline.doconline.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_BODY;
import static com.doconline.doconline.app.Constants.KEY_CREATED_AT;
import static com.doconline.doconline.app.Constants.KEY_DOCTOR;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_LAST_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MCI_CODE;
import static com.doconline.doconline.app.Constants.KEY_PHOTO;
import static com.doconline.doconline.app.Constants.KEY_PRACTIONER_NO;
import static com.doconline.doconline.app.Constants.KEY_QUALIFICATION;


public class ChatMessage implements Serializable {
    public static final int TYPE_MESSAGE_IN = 0;
    public static final int TYPE_MESSAGE_OUT = 1;

    private int mType;
    private String mMessage;
    private String mUsername;


    public String message_id, message, image = "", path, user_name, timestamp;
    public int session_id, doctor_id;

    public String body = "", s_type, doctor_avatar_url = "", doctor_full_name = "", cdn_photo_url = "",
            doctor_mci = "", doctor_praction_no = "", doctor_qualification;


    public ChatMessage() {
        super();
    }


    public ChatMessage(String message_id, String mMessage, String s_type, String timestamp, int mType) {
        super();

        this.message_id = message_id;
        this.mMessage = mMessage;
        this.s_type = s_type;
        this.timestamp = timestamp;
        this.mType = mType;
    }


    public ChatMessage(String message_id, String mMessage, String s_type, String doctor_full_name, String doctor_avatar_url, String timestamp, int mType) {
        super();

        this.message_id = message_id;
        this.mMessage = mMessage;
        this.s_type = s_type;
        this.doctor_full_name = doctor_full_name;
        this.doctor_avatar_url = doctor_avatar_url;
        this.timestamp = timestamp;
        this.mType = mType;
    }


    public ChatMessage(int session_id, String mMessage, String timestamp, int doctor_id, String doctor_full_name, String doctor_avatar_url,
                       String doctor_mci, String doctor_praction_no, String doctor_qualification) {
        super();

        this.session_id = session_id;
        this.mMessage = mMessage;
        this.timestamp = timestamp;
        this.doctor_id = doctor_id;
        this.doctor_full_name = doctor_full_name;
        this.doctor_avatar_url = doctor_avatar_url;
        this.doctor_mci = doctor_mci;
        this.doctor_praction_no = doctor_praction_no;
        this.doctor_qualification = doctor_qualification;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return this.timestamp;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }


    public void setDoctorAvatarURL(String doctor_avatar_url) {
        this.doctor_avatar_url = doctor_avatar_url;
    }

    public String getDoctorAvatarURL() {
        return this.doctor_avatar_url;
    }

    public void setDoctorFullName(String doctor_full_name) {
        this.doctor_full_name = doctor_full_name;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctorFullName() {
        return this.doctor_full_name;
    }

    public void setCDNPhotoURL(String cdn_photo_url) {
        this.cdn_photo_url = cdn_photo_url;
    }

    public String getCDNPhotoURL() {
        return this.cdn_photo_url;
    }

    public String getDoctor_mci() {
        return doctor_mci;
    }

    public void setDoctor_mci(String doctor_mci) {
        this.doctor_mci = doctor_mci;
    }

    public String getDoctor_praction_no() {
        return doctor_praction_no;
    }

    public void setDoctor_praction_no(String doctor_praction_no) {
        this.doctor_praction_no = doctor_praction_no;
    }

    public String getDoctor_qualification() {
        return doctor_qualification;
    }

    public void setDoctor_qualification(String doctor_qualification) {
        this.doctor_qualification = doctor_qualification;
    }

    public int getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getUsername() {
        return mUsername;
    }


    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;

        public Builder(int type) {
            mType = type;
        }

        public ChatMessage.Builder username(String username) {
            mUsername = username;
            return this;
        }

        public ChatMessage.Builder message(String message) {
            mMessage = message;
            return this;
        }

        public ChatMessage build() {
            ChatMessage message = new ChatMessage();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }


    public static HashMap<String, Object> composeChatAttachmentJSON(String path) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_PHOTO, path);

        return hashMap;
    }


    public static HashMap<String, Object> composeMessageJSON(String body) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_BODY, body);

        return hashMap;
    }


    public static List<ChatMessage> getMessageListFromJSON(String json_data) {
        List<ChatMessage> mList = new ArrayList<>();

        try {
            JSONArray data_array = new JSONArray(json_data);

            for (int i = 0; i < data_array.length(); i++) {
                JSONObject json = data_array.getJSONObject(i);

                int session_id = json.getInt(KEY_ID);
                String last_message = json.getString(KEY_LAST_MESSAGE);
                String timestamp = json.getString(KEY_CREATED_AT);

                json = new JSONObject(json.getString(KEY_DOCTOR));

                int doctor_id = 0;
                Object id = json.get(KEY_ID);
                if (id instanceof String) {
                    doctor_id = 0;
                } else if (id instanceof Integer) {
                    doctor_id = ((Integer) id).intValue();
                }
                String doctor_full_name = ""/*json.getString(KEY_FULL_NAME)*/;
                String doctor_mci_code = json.isNull(KEY_MCI_CODE) ? "NA" : json.getString(KEY_MCI_CODE);
                String doctor_practitioner_no = json.isNull(KEY_PRACTIONER_NO) ? "NA" : json.getString(KEY_PRACTIONER_NO);
                String doctor_qualification = json.isNull(KEY_QUALIFICATION) ? "" : json.getString(KEY_QUALIFICATION);
                String doctor_avatar_url = "";

                mList.add(new ChatMessage(session_id, last_message, timestamp, doctor_id, doctor_full_name, doctor_avatar_url,
                        doctor_mci_code, doctor_practitioner_no, doctor_qualification));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mList;
    }
}