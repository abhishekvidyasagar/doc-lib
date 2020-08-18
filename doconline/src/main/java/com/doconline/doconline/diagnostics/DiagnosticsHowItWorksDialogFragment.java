package com.doconline.doconline.diagnostics;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.doconline.doconline.R;

import java.util.ArrayList;

/**
 * Created by admin on 2018-03-08.
 */

public class DiagnosticsHowItWorksDialogFragment extends AppCompatDialogFragment {

    ArrayList<String> listInstructions;
    ListView listView;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.diagnostics_howitworks_dialog_layout, null, false);
//        listView = (ListView)view.findViewById(R.id.listViewInstructions);
//
//        prepareInstructionsList();
//        listView.setAdapter(new ArrayAdapter<String>(
//                this.getContext(), R.layout.bulleted_list_item, listInstructions));
//
//        return view;
//    }

    private void prepareInstructionsList() {
            listInstructions = new ArrayList<String>();
            listInstructions.add("Select a Diagnostic package");
            //listInstructions.add("Compare the prices and Choose a Diagnostic partner");
            listInstructions.add("Enter your address and other Contact details");
            listInstructions.add("Make a payment and your Appointment is booked");
            listInstructions.add("Sit back and relax while the Diagnostics Executive will be at your address to collect the samples at the mentioned time");
            listInstructions.add("Visit your test results in the Diagnostics history section");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.diagnostics_howitworks_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        listView = dialog.findViewById(R.id.listViewInstructions);

        prepareInstructionsList();
        listView.setAdapter(new ArrayAdapter<String>(
                dialog.getContext(), R.layout.bulleted_list_item, listInstructions));

        // Close
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }
}
