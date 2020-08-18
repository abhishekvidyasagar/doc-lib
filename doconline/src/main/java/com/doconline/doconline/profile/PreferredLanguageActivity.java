package com.doconline.doconline.profile;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.LanguageRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.dragable.OnStartDragListener;
import com.doconline.doconline.model.PreferredLanguage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_ENGLISH_NAME;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_LANGUAGES;
import static com.doconline.doconline.app.Constants.KEY_READONLY;

/**
 * Created by chiranjitbardhan on 03/11/17.
 */

public class PreferredLanguageActivity extends BaseActivity implements View.OnClickListener, OnStartDragListener
{


    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    RecyclerView recyclerView;
    LinearLayout layout_empty;
    TextView empty_message;
    CoordinatorLayout layout_root_view;
    TextView tv_toolbar_title;
    Toolbar toolbar;

    private LanguageRecyclerAdapter mAdapter;

    private List<PreferredLanguage> languages = new ArrayList<>();

    private ItemTouchHelper mItemTouchHelper;

    private static final int HTTP_REQUEST_CODE_GET_LANGUAGES = 1;
    private static final int HTTP_REQUEST_CODE_ARRANGE_LANGUAGES = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_languages);
      //  ButterKnife.bind(this);
         layout_refresh = findViewById(R.id.layout_refresh);
       layout_block_ui = findViewById(R.id.layout_block_ui);
       recyclerView = findViewById(R.id.recycler_view);
        layout_empty = findViewById(R.id.layout_empty);
        empty_message = findViewById(R.id.empty_message);
        layout_root_view = findViewById(R.id.layout_root_view);
        tv_toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        empty_message.setText("List is Empty");

        this.refreshAdapter();
        tv_toolbar_title.setText(getResources().getString(R.string.text_preferred_language));

        // Get all the languages.
        if(new InternetConnectionDetector(this).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_LANGUAGES, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getLanguageURL());
        }

        else
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();

        }
    }


    @Override
    public void onClick(View view)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.action_save) {
            List<Integer> sortedList = new ArrayList<>();
            JSONArray array = new JSONArray();

            for (PreferredLanguage language : languages) {
                if (language.getStatus() == 1 /*&& !language.getReadOnly()*/) {
                    sortedList.add(language.getID());
                    array.put(language.getID());
                }
            }

            // sending prefered language in api call.
            if (sortedList.size() != 0) {
                if (new InternetConnectionDetector(this).isConnected()) {
                    new HttpClient(HTTP_REQUEST_CODE_ARRANGE_LANGUAGES, MyApplication.HTTPMethod.PUT.getValue(), true, PreferredLanguage.composeLanguageJSON(array), this)
                            .execute(mController.getProfileURL() + KEY_LANGUAGES);
                } else {
                    new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void refreshAdapter()
    {
        languages.clear();
        languages.addAll(this.mController.getSQLiteHelper().getAllLanguages(true));
        //languages.addAll(this.mController.getSQLiteHelper().getAllLanguages(false));

        adapter_data_changes();
    }


    private void adapter_data_changes()
    {
        try
        {
            if(!isFinishing())
            {
                if(languages.size() == 0)
                {
                    layout_empty.setVisibility(View.VISIBLE);
                    return;
                }

                layout_empty.setVisibility(View.GONE);

                if(mAdapter == null)
                {
                    /**
                     * The number of Columns
                     */
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(mLayoutManager);

                    mAdapter = new LanguageRecyclerAdapter(getApplicationContext(), this, this, languages);

                    //ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(mAdapter);
                    //mItemTouchHelper = new ItemTouchHelper(callback);
                    //mItemTouchHelper.attachToRecyclerView(recyclerView);

                    recyclerView.setAdapter(mAdapter);

                    mAdapter.SetOnItemClickListener(new LanguageRecyclerAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int code)
                        {

                        }
                    });
                }

                mAdapter.notifyDataSetChanged();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //save in sqlite and finish
    private void update_language_list(String json_data)
    {
        try
        {
            mController.getSession().putLanguagePreferences(json_data);

            for(int i=0; i<languages.size(); i++)
            {
                mController.getSQLiteHelper().update(languages.get(i).getID(), (i+1));
                mController.getSQLiteHelper().update(new PreferredLanguage(languages.get(i).getID(), languages.get(i).getStatus()));
            }

            mController.getSession().putLanguagePreferenceValue(PreferredLanguage.getLanguageName());
            Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_LONG).show();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            finish();
        }
    }


    private void init_language_list(String json_data)
    {
        try
        {
            JSONArray json_array = new JSONArray(json_data);

            this.mController.getSQLiteHelper().remove_all(Constants.TABLE_LANGUAGES);

            List<Integer> preferences = new ArrayList<>();
            // language code which is already selected.
            JSONArray array = new JSONArray(this.mController.getSession().getLanguagePreferences());

            for(int i=0; i<array.length(); i++)
            {
                preferences.add(array.getInt(i));
            }

            for(int i=0; i<json_array.length(); i++)
            {
                JSONObject json = json_array.getJSONObject(i);

                int id = json.getInt(KEY_ID);
                String language = json.getString(KEY_ENGLISH_NAME);
                boolean read_only = json.getBoolean(KEY_READONLY);

                PreferredLanguage pLanguage = new PreferredLanguage(id, language, 0, read_only);

                // set selected language status to 1.
                for(int index=1; index<=preferences.size(); index++)
                {
                    if(preferences.get(index-1) == pLanguage.getID())
                    {
                        pLanguage.setStatus(1);
                        pLanguage.setPreference(index);
                    }
                }

                /*if(pLanguage.getReadOnly())
                {
                    pLanguage.setStatus(1);
                }*/

                this.mController.getSQLiteHelper().insert(pLanguage);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            this.refreshAdapter();
        }
    }


    @Override
    public void onTaskCompleted(boolean is_checked, int index, String value)
    {
        for(int i=0; i<languages.size(); i++)
        {
            /*if(!languages.get(i).getReadOnly())
            {
                languages.get(i).setStatus(0);
            }*/

            languages.get(i).setStatus(0);
        }

        if(is_checked)
        {
            languages.get(index).setStatus(1);
        }

        else
        {
            languages.get(index).setStatus(0);
        }

        adapter_data_changes();
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
        mItemTouchHelper.startDrag(viewHolder);
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
        if(isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_GET_LANGUAGES && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.init_language_list(json.getString(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_ARRANGE_LANGUAGES && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.update_language_list((json.isNull(KEY_DATA)) ? "[]" : json.getString(KEY_DATA));
                }

                catch (Exception e)
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
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }
}