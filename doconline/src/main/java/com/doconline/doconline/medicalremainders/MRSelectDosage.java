package com.doconline.doconline.medicalremainders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.BaseActivity;

public class MRSelectDosage extends BaseActivity implements AdapterView.OnItemSelectedListener {

    TextView toolbar_title;
    Toolbar toolbar;
    EditText edDosage;
    String[] units = { "cc", "ml", "gr", "mg", "mcg"};
    Button donebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_mrselect_dosage);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.title_activity_mrselect_dosage));



        edDosage = findViewById(R.id.editTextDosageUnits);

        final Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,units);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        donebutton = findViewById(R.id.done);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MRSelectDosage.this, MRAddRemainder.class);
                if (!edDosage.getText().toString().equalsIgnoreCase("")){
                    i.putExtra("dosage",edDosage.getText().toString()+spin.getSelectedItem().toString());
                }else {
                    i.putExtra("dosage","");
                }
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: //Back arrow pressed
            {
                this.onBackPressed();
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(Color.BLACK);
        ((TextView) view).setText(units[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
