package com.doconline.doconline;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.doconline.doconline.helper.NumberInputFilter;
import com.doconline.doconline.model.HealthProfile;


public class BMIDialogFragment extends DialogFragment
{
    private Context context;
    private ViewHolder viewHolder;
    private BMICalculationFragmentListener listener;
    private float height;
    private float weight;
    private ViewHolder getViewHolder()
    {
        return viewHolder;
    }

    public void setListener(BMICalculationFragmentListener listener)

    {
        this.listener = listener;
    }

    public BMIDialogFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public BMIDialogFragment(Context context, float height, float weight)
    {
        this.context = context;
        this.height = height;
        this.weight = weight;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bmi_calculation, null);

        viewHolder = new ViewHolder();
        viewHolder.populate(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.Calculate, null)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        hideKeyboard();
                        dialog.cancel();

                        if (listener != null)
                        {
                            listener.onDialogNegativeClick(BMIDialogFragment.this);
                        }
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface)
            {

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view)
                {
                    if (listener != null)
                    {
                        hideKeyboard();

                        if(getViewHolder().editHeight.getText().toString().trim().isEmpty())
                        {
                            getViewHolder().editHeight.setError("What's your height ?");
                            return;
                        }

                        if(getViewHolder().editWeight.getText().toString().trim().isEmpty())
                        {
                            getViewHolder().editWeight.setError("What's your weight ?");
                            return;
                        }

                        try
                        {
                            float height = Float.parseFloat(getViewHolder().editHeight.getText().toString());
                            float weight = Float.parseFloat(getViewHolder().editWeight.getText().toString());

                            float tBMI = HealthProfile.calculateBMI(height, weight);

                            if(tBMI > 0.f)
                            {
                                dialog.dismiss();
                                listener.onDialogPositiveClick(BMIDialogFragment.this, height, weight, tBMI);
                            }

                            else
                            {
                                if(tBMI == -1.f)
                                {
                                    if(height == 0)
                                    {
                                        getViewHolder().editHeight.setError("Should be 51cm to 275cm");
                                        getViewHolder().editHeight.requestFocus();
                                        return;
                                    }

                                    if(weight == 0)
                                    {
                                        getViewHolder().editWeight.setError("Should be 2Kg to 500Kg");
                                        getViewHolder().editWeight.requestFocus();
                                    }
                                }

                                else if(tBMI == -2.f)
                                {
                                    getViewHolder().editHeight.setError("Should be 51cm to 275cm");
                                    getViewHolder().editHeight.requestFocus();
                                }

                                else if(tBMI == -3.f)
                                {
                                    getViewHolder().editWeight.setError("Should be 2Kg to 500Kg");
                                    getViewHolder().editWeight.requestFocus();
                                }
                            }
                        }

                        catch (NumberFormatException nfx)
                        {
                            Toast.makeText(context, "Invalid Value", Toast.LENGTH_SHORT).show();
                            nfx.printStackTrace();
                        }
                    }
                }
            });
          }
        });

        return dialog;
    }


    public interface BMICalculationFragmentListener
    {
        void onDialogPositiveClick(DialogFragment dialog, float height, float weight, float tBMI);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private class ViewHolder
    {
        EditText editHeight;
        EditText editWeight;

        private void populate(View view)
        {
            editHeight = view.findViewById(R.id.editHeight);
            editWeight = view.findViewById(R.id.editWeight);

            editWeight.setFilters(new InputFilter[] { new NumberInputFilter(5, 2)} );
            editHeight.setFilters(new InputFilter[] { new NumberInputFilter(5, 2)} );

            if(weight > 0)
            {
                editWeight.setText(String.valueOf(weight));
            }

            if(height > 0)
            {
                editHeight.setText(String.valueOf(height));
            }
        }
    }


    private void hideKeyboard()
    {
        try
        {
            if(getActivity() == null)
            {
                return;
            }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(imm != null)
            {
                imm.hideSoftInputFromWindow(getViewHolder().editHeight.getWindowToken(), 0);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}