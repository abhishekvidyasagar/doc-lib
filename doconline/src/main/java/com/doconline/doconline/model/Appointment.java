package com.doconline.doconline.model;

import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.doconline.doconline.app.Constants.CALL_STATUS_ACTIVE;
import static com.doconline.doconline.app.Constants.CONSULTATION_FAMILY;
import static com.doconline.doconline.app.Constants.CONSULTATION_SELF;
import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_ATTACHMENTS;
import static com.doconline.doconline.app.Constants.KEY_AVATAR_URL;
import static com.doconline.doconline.app.Constants.KEY_CALL_CHANNEL;
import static com.doconline.doconline.app.Constants.KEY_CALL_TYPE;
import static com.doconline.doconline.app.Constants.KEY_DOB;
import static com.doconline.doconline.app.Constants.KEY_DOCTOR;
import static com.doconline.doconline.app.Constants.KEY_DOCTOR_NOTES;
import static com.doconline.doconline.app.Constants.KEY_FILE_URL;
import static com.doconline.doconline.app.Constants.KEY_FINISHED_AT;
import static com.doconline.doconline.app.Constants.KEY_FIRST_NAME;
import static com.doconline.doconline.app.Constants.KEY_FOLLOWUPDATE_BY_DOCTOR;
import static com.doconline.doconline.app.Constants.KEY_FULL_NAME;
import static com.doconline.doconline.app.Constants.KEY_GENDER;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_LAST_NAME;
import static com.doconline.doconline.app.Constants.KEY_MIDDLE_NAME;
import static com.doconline.doconline.app.Constants.KEY_NAME_PREFIX;
import static com.doconline.doconline.app.Constants.KEY_NOTES;
import static com.doconline.doconline.app.Constants.KEY_PATIENT;
import static com.doconline.doconline.app.Constants.KEY_PRACTITIONER_NUMBER;
import static com.doconline.doconline.app.Constants.KEY_PROVISIONAL_DIAGNOSIS;
import static com.doconline.doconline.app.Constants.KEY_PUBLIC_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_RATINGS;
import static com.doconline.doconline.app.Constants.KEY_SCHEDULED_AT;
import static com.doconline.doconline.app.Constants.KEY_SPECIALIZATION;
import static com.doconline.doconline.app.Constants.KEY_STARTED_AT;
import static com.doconline.doconline.app.Constants.KEY_STATUS;
import static com.doconline.doconline.app.Constants.KEY_SYMPTOMS;
import static com.doconline.doconline.app.Constants.KEY_TITLE;
import static com.doconline.doconline.app.Constants.KEY_USER;

/**
 * Created by chiranjit on 17/04/17.
 */

public class Appointment implements Serializable
{
    private String scheduled_at, patient_name, start_time, end_time, notes, symptoms, doctor_notes,
            provisional_diagnosis, followupdatebydoctor;
    private int appointment_id, call_type, call_channel, status, for_whom;
    private String public_appointment_id = "";
    private String prescription = "";
    private List<FileUtils> attachments = new ArrayList<>();
    private Doctor doctor = new Doctor();
    private Patient patient = new Patient();

    public Appointment()
    {

    }

    public Appointment(int appointment_id, String scheduled_at, int call_type, int status)
    {
        this.appointment_id = appointment_id;
        this.scheduled_at = scheduled_at;
        this.call_type = call_type;
        this.status = status;
    }

    public Appointment(int appointment_id, String public_appointment_id, String scheduled_at, int call_type, int status, Patient patient)
    {
        this.appointment_id = appointment_id;
        this.public_appointment_id = public_appointment_id;
        this.scheduled_at = scheduled_at;
        this.call_type = call_type;
        this.status = status;
        this.patient = patient;
    }

    public Appointment(int appointment_id, String scheduled_at, int call_type, int status, Doctor doctor, Patient patient)
    {
        this.appointment_id = appointment_id;
        this.scheduled_at = scheduled_at;
        this.call_type = call_type;
        this.status = status;
        this.doctor = doctor;
        this.patient = patient;
    }

    public Appointment(int appointment_id, String public_appointment_id, String scheduled_at, int call_type,
                       int call_channel, int status, String start_time, String end_time, String symptoms, String notes)
    {
        this.appointment_id = appointment_id;
        this.public_appointment_id = public_appointment_id;
        this.scheduled_at = scheduled_at;
        this.call_type = call_type;
        this.call_channel = call_channel;
        this.status = status;
        this.start_time = start_time;
        this.end_time = end_time;
        this.symptoms = symptoms;
        this.notes = notes;
    }

    public Appointment(int appointment_id, String scheduled_at, String start_time, String end_time, int call_type, int status)
    {
        this.appointment_id = appointment_id;
        this.scheduled_at = scheduled_at;
        this.start_time = start_time;
        this.end_time = end_time;
        this.call_type = call_type;
        this.status = status;
    }


    public void setAppointmentID(int appointment_id)
    {
        this.appointment_id = appointment_id;
    }

    public int getAppointmentID()
    {
        return this.appointment_id;
    }


    public String getPublicAppointmentId()
    {
        return this.public_appointment_id;
    }

    public void setPublicAppointmentId(String public_appointment_id)
    {
        this.public_appointment_id = public_appointment_id;
    }

    public String getPrescription()
    {
        return this.prescription;
    }

    public void setPrescription(String prescription)
    {
        this.prescription = prescription;
    }

    public void setPatientName(String patient_name)
    {
        this.patient_name = patient_name;
    }

    public String getPatientName()
    {
        return this.patient_name;
    }


    public void setScheduledAt(String scheduled_at)
    {
        this.scheduled_at = scheduled_at;
    }

    public String getScheduledAt()
    {
        return this.scheduled_at;
    }


    public void setStartedAt(String start_time)
    {
        this.start_time = start_time;
    }

    public String getStartedAt()
    {
        return this.start_time;
    }


    public void setEndedAt(String end_time)
    {
        this.end_time = end_time;
    }

    public String getEndedAt()
    {
        return this.end_time;
    }


    public void setForWhom(int for_whom)
    {
        this.for_whom = for_whom;
    }

    public int getForWhom()
    {
        return this.for_whom;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getNotes()
    {
        return this.notes;
    }

    public void setAttachments(FileUtils attachments)
    {
        this.attachments.add(attachments);
    }

    public List<FileUtils> getAttachments()
    {
        return this.attachments;
    }


    public void setCallType(int call_type)
    {
        this.call_type = call_type;
    }

    public int getCallType()
    {
        return this.call_type;
    }


    public int getCallChannel()
    {
        return this.call_channel;
    }

    public void setCallChannel(int call_channel)
    {
        this.call_channel = call_channel;
    }

    public void setAppointmentStatus(int status)
    {
        this.status = status;
    }

    public int getAppointmentStatus()
    {
        return this.status;
    }


    public void setDoctor(Doctor doctor)
    {
        this.doctor = doctor;
    }

    public Doctor getDoctor()
    {
        return this.doctor;
    }


    public void setPatient(Patient patient)
    {
        this.patient = patient;
    }

    public Patient getPatient()
    {
        return this.patient;
    }


    public String getDoctorNotes()
    {
        return this.doctor_notes;
    }

    public void setDoctorNotes(String doctor_notes)
    {
        this.doctor_notes = doctor_notes;
    }

    public String getProvisionalDiagnosis()
    {
        return this.provisional_diagnosis;
    }

    public void setProvisionalDiagnosis(String provisional_diagnosis)
    {
        this.provisional_diagnosis = provisional_diagnosis;
    }

    public String getFollowUpDateByDoctor()
    {
        return this.followupdatebydoctor;
    }

    public void setFollowUpDateByDoctor(String followupdatebydoctor)
    {
        this.followupdatebydoctor = followupdatebydoctor;
    }

    public static String getAppointmentTimer(List<Appointment> aList)
    {
        List<Long> time_slot = new ArrayList<>();
        List<Appointment> appointments = new ArrayList<>();

        long remaining_second = -1;
        int appointment_id = -1;

        for(Appointment appointment: aList)
        {
            if(appointment.getAppointmentStatus() == 1)
            {
                long second = Helper.time_diff(appointment.scheduled_at);

                if(second > 0)
                {
                    appointments.add(appointment);
                    time_slot.add(second);
                }
            }
        }

        Collections.sort(time_slot);

        for(int i=0; i<time_slot.size(); i++)
        {
            if(time_slot.get(i) >= 10 && time_slot.get(i) <= 300)
            {
                remaining_second = time_slot.get(i);
                appointment_id = appointments.get(i).getAppointmentID();
                break;
            }
        }

        return appointment_id + "|" + remaining_second;
    }


    public static Appointment getNextAppointment(List<Appointment> aList)
    {
        List<Appointment> appointments = new ArrayList<>();
        Appointment temp;

        for(Appointment appointment: aList)
        {
            if (appointment.getAppointmentStatus() == CALL_STATUS_ACTIVE)
            {
                int diff = Helper.compare_date(Helper.UTC_to_Local_Date(appointment.getScheduledAt()), System.currentTimeMillis());

                if(diff == 1)
                {
                    appointments.add(appointment);
                }
            }
        }

        for(int i=0; i < appointments.size(); i++)
        {
            for(int j=1; j < (appointments.size()-i); j++)
            {
                int diff = Helper.compare_date(Helper.UTC_to_Local_Date(appointments.get(j-1).getScheduledAt()),
                        Helper.UTC_to_Local_Date(appointments.get(j).getScheduledAt()));

                if( diff == 1)
                {
                    temp = appointments.get(j-1);
                    appointments.set((j-1), appointments.get(j));
                    appointments.set(j, temp);
                }
            }
        }

        if(appointments.size() > 0)
        {
            return appointments.get(0);
        }

        return new Appointment();
    }


    public static List<Appointment> getAppointmentListFromJSON(String json_data)
    {
        List<Appointment> appointmentList = new ArrayList<>();

        try
        {
            JSONArray data_array = new JSONArray(json_data);

            for(int i=0; i<data_array.length(); i++)
            {
                JSONObject json = data_array.getJSONObject(i);

                int appointment_id = json.getInt(KEY_ID);
                String scheduled_at = json.getString(KEY_SCHEDULED_AT);
                int call_type = json.getInt(KEY_CALL_TYPE);
                int status = json.getInt(KEY_STATUS);

                JSONObject patient_json = json.getJSONObject(KEY_PATIENT);
                int patient_id = patient_json.getInt(KEY_ID);
                String first_name = patient_json.getString(KEY_FIRST_NAME);
                String gender = (patient_json.isNull(KEY_GENDER)) ? "" : patient_json.getString(KEY_GENDER);
                String dob = (patient_json.isNull(KEY_DOB)) ? "" : patient_json.getString(KEY_DOB);
                String avatar_url = (patient_json.isNull(KEY_AVATAR_URL)) ? "" : patient_json.getString(KEY_AVATAR_URL);

                Patient patient = new Patient(patient_id, first_name, gender, dob, avatar_url);

                JSONObject doctor_json = json.getJSONObject(KEY_DOCTOR);
                String full_name = (doctor_json.isNull(KEY_FULL_NAME)) ? "" : doctor_json.getString(KEY_FULL_NAME);
                String specialization = (doctor_json.isNull(KEY_SPECIALIZATION)) ? "" : doctor_json.getString(KEY_SPECIALIZATION);

                Doctor doctor = new Doctor(full_name, specialization);

                appointmentList.add(new Appointment(appointment_id, scheduled_at, call_type, status, doctor, patient));
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return appointmentList;
    }


    public static Appointment getAppointmentFromJSON(String data)
    {
        Appointment appointment = new Appointment();

        try
        {
            JSONObject json = new JSONObject(data);

            int appointment_id = json.getInt(KEY_APPOINTMENT_ID);
            String public_appointment_id = json.getString(KEY_PUBLIC_APPOINTMENT_ID);
            String scheduled_at = json.getString(KEY_SCHEDULED_AT);
            String started_at = json.getString(KEY_STARTED_AT);
            String finished_at = json.getString(KEY_FINISHED_AT);
            String notes = json.getString(KEY_NOTES);
            int call_type = json.getInt(KEY_CALL_TYPE);
            int call_channel = json.getInt(KEY_CALL_CHANNEL);
            int status = json.getInt(KEY_STATUS);

            String symptoms = json.isNull(KEY_SYMPTOMS) ? "" : json.getString(KEY_SYMPTOMS);

            appointment = new Appointment(appointment_id, public_appointment_id, scheduled_at, call_type, call_channel, status, started_at, finished_at, symptoms, notes);

            JSONArray json_attachments = new JSONArray(json.getString(KEY_ATTACHMENTS));

            for(int i=0; i<json_attachments.length(); i++)
            {
                JSONObject attachment_obj = json_attachments.getJSONObject(i);
                int id = attachment_obj.getInt(KEY_ID);
                String title = attachment_obj.isNull(KEY_TITLE) ? "" : attachment_obj.getString(KEY_TITLE);
                appointment.setAttachments(new FileUtils(id, attachment_obj.getString(KEY_FILE_URL), title));
            }

            String doctor_notes = (json.isNull(KEY_DOCTOR_NOTES)) ? "No remarks yet" : json.getString(KEY_DOCTOR_NOTES);

            appointment.setDoctorNotes(doctor_notes);

            String provisional_diagnosis = (json.isNull(KEY_PROVISIONAL_DIAGNOSIS)) ? "No provisional diagnosis found" : json.getString(KEY_PROVISIONAL_DIAGNOSIS);
            appointment.setProvisionalDiagnosis(provisional_diagnosis);

            //String followupdatebydoctor = (json.isNull(KEY_FOLLOWUPDATE_BY_DOCTOR)) ? "No followup date found" : json.getString(KEY_FOLLOWUPDATE_BY_DOCTOR);
            String followupdatebydoctor = (json.isNull(KEY_FOLLOWUPDATE_BY_DOCTOR)) ? "-NA-" : json.getString(KEY_FOLLOWUPDATE_BY_DOCTOR);
            appointment.setFollowUpDateByDoctor(followupdatebydoctor);

            JSONObject json_data = new JSONObject(json.getString(KEY_USER));
            int user_id = json_data.getInt(KEY_ID);

            int patient_id = 0;

            if(json.has(KEY_PATIENT))
            {
                json_data = new JSONObject(json.getString(KEY_PATIENT));

                if(json_data.has(KEY_ID))
                {
                    patient_id = (json_data.isNull(KEY_ID)) ? 0 : json_data.getInt(KEY_ID);
                }

                if(json_data.has(KEY_FULL_NAME))
                {
                    String first_name = (json_data.isNull(KEY_FIRST_NAME)) ? "Not Available" : json_data.getString(KEY_FIRST_NAME);
                    appointment.getPatient().setFirstName(first_name);
                }

                if(json_data.has(KEY_GENDER))
                {
                    String gender = (json_data.isNull(KEY_GENDER)) ? "" : json_data.getString(KEY_GENDER);
                    appointment.getPatient().setGender(gender);
                }

                if(json_data.has(KEY_DOB))
                {
                    String dob = (json_data.isNull(KEY_DOB)) ? "" : json_data.getString(KEY_DOB);
                    appointment.getPatient().setDateOfBirth(dob);
                }

                if(json_data.has(KEY_AVATAR_URL))
                {
                    String avatar_url = (json_data.isNull(KEY_AVATAR_URL)) ? "" : json_data.getString(KEY_AVATAR_URL);
                    appointment.getPatient().setProfilePic(avatar_url);
                }

            }

            if(user_id == patient_id)
            {
                appointment.setForWhom(CONSULTATION_SELF);
            }

            else
            {
                appointment.setForWhom(CONSULTATION_FAMILY);
            }

            json_data = new JSONObject(json.getString(KEY_DOCTOR));

            int doctor_id = json_data.getInt(KEY_ID);
            String name_prefix = json_data.getString(KEY_NAME_PREFIX);
            String first_name = json_data.getString(KEY_FIRST_NAME);
            String middle_name = json_data.getString(KEY_MIDDLE_NAME);
            String last_name = json_data.getString(KEY_LAST_NAME);
            String full_name = json_data.getString(KEY_FULL_NAME);
            String avatar_url = json_data.getString(KEY_AVATAR_URL);
            int practitioner_number = json_data.getInt(KEY_PRACTITIONER_NUMBER);
            String specialization = json_data.getString(KEY_SPECIALIZATION);
            double ratings = json_data.getDouble(KEY_RATINGS);

            appointment.setDoctor(new Doctor(doctor_id, name_prefix, first_name, middle_name, last_name, full_name, avatar_url, practitioner_number, specialization, ratings));
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return appointment;
    }



    public static List<Appointment> getPrescriptionListFromJSON(String json_data)
    {
        List<Appointment> appointmentList = new ArrayList<>();

        try
        {
            JSONArray data_array = new JSONArray(json_data);

            for(int i=0; i<data_array.length(); i++)
            {
                JSONObject json = data_array.getJSONObject(i);

                int appointment_id = json.getInt(KEY_ID);
                String public_appointment_id = json.getString(KEY_PUBLIC_APPOINTMENT_ID);
                String scheduled_at = json.getString(KEY_SCHEDULED_AT);
                int call_type = json.getInt(KEY_CALL_TYPE);
                int status = json.getInt(KEY_STATUS);

                JSONObject patient_json = json.getJSONObject(KEY_PATIENT);
                int patient_id = patient_json.getInt(KEY_ID);
                String full_name = patient_json.getString(KEY_FULL_NAME);

                Patient patient = new Patient(patient_id, full_name);

                appointmentList.add(new Appointment(appointment_id, public_appointment_id, scheduled_at, call_type, status, patient));
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return appointmentList;
    }


    public static int getPrescriptionIndex(List<Appointment> appointments, int appointment_id)
    {
        for(int index=0; index<appointments.size(); index++)
        {
            if(appointments.get(index).getAppointmentID() == appointment_id)
            {
                return index;
            }
        }

        return -1;
    }

    public String getSymptoms() {
        return symptoms;
    }
}