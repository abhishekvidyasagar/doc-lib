package com.doconline.doconline;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.doconline.doconline.model.Review;

public class RatingFragment extends DialogFragment {
    private Context context;
    private ViewHolder viewHolder;
    private AddReviewFragmentListener listener;
    private float rating;

    private ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setListener(AddReviewFragmentListener listener) {
        this.listener = listener;
    }

    public RatingFragment() {

    }

    @SuppressLint("ValidFragment")
    public RatingFragment(Context context, float rating) {
        this.context = context;
        this.rating = rating;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rating, null);

        viewHolder = new ViewHolder();
        viewHolder.populate(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.Submit, null)
                .setNegativeButton(R.string.NotNow, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        RatingFragment.this.getDialog().cancel();

                        if (listener != null) {
                            listener.onDialogNegativeClick(RatingFragment.this);
                        }
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (dataIsValid()) {
                            double rating = getViewHolder().rating_bar.getRating();

                            if (listener != null) {
                                listener.onDialogPositiveClick(RatingFragment.this, new Review(rating, "", ""));
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        return dialog;
    }

    private boolean dataIsValid() {
        boolean validData = true;

        int rating = (int) getViewHolder().rating_bar.getRating();

        if (rating == 0) {
            Toast.makeText(context, "Please provide your rating", Toast.LENGTH_SHORT).show();
            validData = false;
        }

        return validData;
    }

    public interface AddReviewFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog, Review review);

        void onDialogNegativeClick(DialogFragment dialog);

        void onDialogRatingChange(DialogFragment dialog, Review review);
    }

    private class ViewHolder {
        RatingBar rating_bar;

        private void populate(View view) {
            rating_bar = view.findViewById(R.id.rating_bar);
            rating_bar.setOnRatingBarChangeListener(onButtonClickListener);

            if (Build.VERSION.SDK_INT < 23) {
                LayerDrawable stars = (LayerDrawable) rating_bar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.star_active), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(ContextCompat.getColor(context, R.color.place_holder), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(ContextCompat.getColor(context, R.color.place_holder), PorterDuff.Mode.SRC_ATOP);
            }

            rating_bar.setRating(rating);
        }

        private RatingBar.OnRatingBarChangeListener onButtonClickListener = new RatingBar.OnRatingBarChangeListener() {

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromTouch) {
                if (listener != null) {
                    listener.onDialogRatingChange(RatingFragment.this, new Review(rating, "", ""));
                }
            }
        };
    }
}