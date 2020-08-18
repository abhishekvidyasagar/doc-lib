package com.doconline.doconline.consultation.guest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/*import butterknife.BindView;
import butterknife.ButterKnife;*/

/**
 * Created by chiranjit on 25/04/17.
 */
public class GuestAppointmentConfirmationFragment extends BaseFragment implements OnItemDeleted
{

    TextView tv_patient_name;
    TextView tv_consultation_date;
    TextView tv_doctor_name;
    ImageView doctor_image;
    TextView tv_specialization;
    TextView tv_doctor_rating;
    RatingBar doctor_rating_star;
    TextView tv_step_one;
    TextView tv_step_two;
    TextView tv_message;

    private Appointment mAppointment;
    private Context context = null;
    private User mProfile;


    public GuestAppointmentConfirmationFragment()
    {

    }


    @SuppressLint("ValidFragment")
    public GuestAppointmentConfirmationFragment(Context context, Appointment mAppointment, User mProfile)
    {
        this.context = context;
        this.mAppointment = mAppointment;
        this.mProfile = mProfile;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_guest_appointment_confirmation, container, false);
     //   ButterKnife.bind(this, view);

        tv_patient_name = view.findViewById(R.id.tv_patient_name);
        tv_consultation_date= view.findViewById(R.id.tv_consultation_date);
        tv_doctor_name= view.findViewById(R.id.doctor_name);
        doctor_image= view.findViewById(R.id.doctor_image);
        tv_specialization= view.findViewById(R.id.specialization);
        tv_doctor_rating= view.findViewById(R.id.doctor_rating);
        doctor_rating_star= view.findViewById(R.id.doctor_rating_star);
        tv_step_one= view.findViewById(R.id.tv_step_one);
        tv_step_two= view.findViewById(R.id.tv_step_two);
        tv_message= view.findViewById(R.id.tv_message);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_two.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        if(!mProfile.getPhoneNo().isEmpty())
        {
            tv_message.setText("Now you can login with your mobile no and password.");
        }

        else
        {
            tv_message.setText("Now you just need to confirm email address. Please click the link in the email we sent you.");
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown() && isVisibleToUser)
        {
            if(!mAppointment.getScheduledAt().isEmpty())
            {
                String date = Helper.UTC_to_Local_TimeZone(mAppointment.getScheduledAt());

                try
                {
                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
                    Date value = sdf.parse(date);
                    sdf = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY_HH_MM_A);
                    tv_consultation_date.setText(sdf.format(value));
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            this.bindView(this.mAppointment);
        }
    }

    @Override
    public void onItemDeleted(int index, int item_id)
    {

    }


    private void bindView(Appointment appointment)
    {
        try
        {
            tv_patient_name.setText(Helper.toCamelCase(appointment.getPatientName()));

            tv_doctor_name.setText(Helper.toCamelCase(appointment.getDoctor().getFullName()));
            tv_specialization.setText(appointment.getDoctor().getSpecialization());
            tv_doctor_rating.setText(String.valueOf(appointment.getDoctor().getRatings()));
            doctor_rating_star.setRating((float) appointment.getDoctor().getRatings());

            if(!appointment.getDoctor().getAvatarURL().isEmpty())
            {
                try
                {
                    ImageLoader.load(context, appointment.getDoctor().getAvatarURL(), doctor_image, R.drawable.doctor, 180, 180);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}