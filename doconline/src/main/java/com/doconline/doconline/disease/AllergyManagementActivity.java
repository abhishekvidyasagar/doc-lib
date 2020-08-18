package com.doconline.doconline.disease;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.model.Allergy;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.TABLE_ALLERGIES;


public class AllergyManagementActivity extends BaseActivity implements View.OnClickListener
{
    private static final int HTTP_REQUEST_CODE = 1;


    Toolbar toolbar;

    TextView toolbar_title;

    CoordinatorLayout layout_root_view;

    EditText edit_allergy;

    Button button_go;

    RelativeLayout layout_refresh;

    RelativeLayout layout_block_ui;

    private Allergy mAllergy;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_management);
      //  ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title= findViewById(R.id.toolbar_title);
        layout_root_view= findViewById(R.id.layout_root_view);
        edit_allergy= findViewById(R.id.editAllergy);
        button_go= findViewById(R.id.btnGo);
        layout_refresh= findViewById(R.id.layout_refresh);
         layout_block_ui= findViewById(R.id.layout_block_ui);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(getIntent().getSerializableExtra("ALLERGY") != null)
        {
            mAllergy = (Allergy) getIntent().getSerializableExtra("ALLERGY");
            toolbar_title.setText(getResources().getString(R.string.text_update_allergy));
            edit_allergy.setText(mAllergy.disease);
            button_go.setText(R.string.Save);
        }

        else
        {
            this.mAllergy = new Allergy();
            toolbar_title.setText(getResources().getString(R.string.text_add_allergy));
            button_go.setText(R.string.Add);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnGo) {
            if (new InternetConnectionDetector(getApplicationContext()).isConnected()) {
                mAllergy.disease = edit_allergy.getText().toString();

                if (getIntent().getSerializableExtra("ALLERGY") == null) {
                    new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.POST.getValue(),
                            Allergy.composeAllergyJSON(this.mAllergy.disease), this)
                            .execute(mController.getHealthProfileURL() + Constants.KEY_ALLERGY);
                } else {
                    String url = mController.getHealthProfileURL() + Constants.KEY_ALLERGY + "/" + this.mAllergy.id;

                    new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.PATCH.getValue(),
                            Allergy.composeAllergyJSON(this.mAllergy.disease), this)
                            .execute(url);
                }
            } else {
                new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            }
        }
    }


    @Override
    public void onPreExecute()
    {
        if(isFinishing())
        {
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_refresh.setVisibility(View.VISIBLE);
                layout_block_ui.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE && (responseCode == HttpClient.CREATED || responseCode == HttpClient.OK))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.allergy(json.getJSONObject(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(this, this, layout_root_view).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    layout_refresh.setVisibility(View.INVISIBLE);
                    layout_block_ui.setVisibility(View.GONE);
                }
            });
        }
    }


    @Override
    public void onPositiveAction()
    {

    }

    @Override
    public void onNegativeAction()
    {

    }


    private void allergy(JSONObject json)
    {
        try
        {
            int id = json.getInt(Constants.KEY_ID);
            String name = json.getString(Constants.KEY_NAME);

            if(!mController.getSQLiteHelper().insert(new Allergy(id, name)))
            {
                mController.getSQLiteHelper().update(TABLE_ALLERGIES, id, name);
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            finish();
        }
    }
}