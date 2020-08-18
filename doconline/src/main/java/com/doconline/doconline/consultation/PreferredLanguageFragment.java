package com.doconline.doconline.consultation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.LanguageRecyclerAdapter;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.helper.dragable.EditItemTouchHelperCallback;
import com.doconline.doconline.helper.dragable.OnStartDragListener;
import com.doconline.doconline.model.PreferredLanguage;
import com.doconline.doconline.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.DEFAULT_LANGUAGE_SELECTION_LIMIT;

/**
 * Created by chiranjit on 14/12/16.
 */
public class PreferredLanguageFragment extends Fragment implements View.OnClickListener, OnTaskCompleted, OnStartDragListener
{

    private Context context = null;

    private LanguageRecyclerAdapter mAdapter;
    private SQLiteHelper helper;

    private RecyclerView recyclerView;
    private LinearLayout layout_empty;
    private List<PreferredLanguage> languages = new ArrayList<>();

    ItemTouchHelper mItemTouchHelper;


    public PreferredLanguageFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public PreferredLanguageFragment(Context context)
    {
        this.context = context;
        this.helper = new SQLiteHelper(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_preferred_language, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        layout_empty = view.findViewById(R.id.layout_empty);

        return view;
    }


    @Override
    public void onClick(View view)
    {

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown())
        {
            if (isVisibleToUser)
            {
                languages.clear();
                languages.addAll(helper.getAllLanguages(false));
                adapter_data_changes();
            }
        }
    }


    private void adapter_data_changes()
    {
        try
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
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new LanguageRecyclerAdapter(context, this, this, languages);

                ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(mAdapter);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(recyclerView);

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

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskCompleted(boolean is_checked, int index, String value)
    {
        if(languages.get(index).getID() == 0)
        {
            return;
        }

        if(is_checked)
        {
            languages.get(index).setStatus(1);
        }

        else
        {
            languages.get(index).setStatus(0);
        }

        helper.update(languages.get(index));

        if(helper.getAllLanguages(true).size() > DEFAULT_LANGUAGE_SELECTION_LIMIT + 1)
        {
            languages.get(index).setStatus(0);
            helper.update(languages.get(index));
            adapter_data_changes();

            Toast.makeText(context, "Maximum " + DEFAULT_LANGUAGE_SELECTION_LIMIT+ " language selection allowed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
        mItemTouchHelper.startDrag(viewHolder);
    }
}