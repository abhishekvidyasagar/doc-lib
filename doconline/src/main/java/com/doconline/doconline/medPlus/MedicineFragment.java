package com.doconline.doconline.medPlus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.MedicineAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnProcureMedicineListener;
import com.doconline.doconline.helper.dragable.MedicineRecyclerItemTouchHelper;
import com.doconline.doconline.model.Medicine;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_REMOVE_MEDICINE_AND_CONTINUE;


/**
 * Created by cbug on 25/9/17.
 */
public class MedicineFragment extends BaseFragment implements View.OnClickListener,
        MedicineRecyclerItemTouchHelper.RecyclerItemTouchHelperListener
{

    RecyclerView mRecyclerView;

    LinearLayout layout_empty;

    Button button_go;

    TextView empty_message;

    TextView tv_step_two;

    TextView tv_step_one;

    private MedicineAdapter mAdapter;
    private OnProcureMedicineListener medicine_listener;
    private Context context;
    private ArrayList<Medicine> mList;


    public MedicineFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public MedicineFragment(Context context, OnProcureMedicineListener medicine_listener, ArrayList<Medicine> mList)
    {
        this.context = context;
        this.medicine_listener = medicine_listener;
        this.mList = mList;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_medicine_list, container, false);
        //ButterKnife.bind(this, view);

        mRecyclerView = view.findViewById(R.id.medicine_list_recycler_view);
        layout_empty = view.findViewById(R.id.layout_empty);
        button_go = view.findViewById(R.id.btnGo);
        empty_message= view.findViewById(R.id.empty_message);
        tv_step_two= view.findViewById(R.id.tv_step_two);

        tv_step_one = view.findViewById(R.id.tv_step_one);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        tv_step_two.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        layout_empty.setVisibility(View.GONE);
        empty_message.setText("It looks like, you don't have a list of medicines to proceed or order.");

        button_go.setText(R.string.Next);

        this.initAdapter();
        this.addListener();

        this.checkAvailability();
    }


    private void addListener()
    {
        button_go.setOnClickListener(this);
    }


    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        mAdapter = new MedicineAdapter(mList, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new MedicineRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(mRecyclerView);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position)
    {
        if (viewHolder instanceof MedicineAdapter.MedicineViewHolder)
        {
            final int deletedIndex = viewHolder.getAdapterPosition();

            final Medicine deletedItem = mList.get(deletedIndex);

            mAdapter.removeItem(deletedIndex);

            if(getView() == null)
            {
                return;
            }

            Snackbar bar = Snackbar.make(getView(), deletedItem.getMedicineName() + " removed!", Snackbar.LENGTH_LONG);

            bar.setAction("UNDO", new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    mAdapter.restoreItem(deletedItem, deletedIndex);
                    refreshAdapter();
                }
            });

            bar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

            TextView textView = bar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            bar.setActionTextColor(Color.WHITE);
            bar.show();

            this.refreshAdapter();
        }
    }


    private void refreshAdapter()
    {
        if(this.mList.size() == 0)
        {
            layout_empty.setVisibility(View.VISIBLE);
        }

        else
        {
            layout_empty.setVisibility(View.GONE);
        }

        this.checkAvailability();
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnGo) {
            if (validate()) {
                if (getSubTotal(this.mList) > 200) {
                    medicine_listener.onMedicineFound(this.mList);
                    medicine_listener.onPageSelection(4, "Enter Address");
                } else {
                    new CustomAlertDialog(getContext(), this).
                            showAlertDialogWithoutTitle("Minimum Order Rs.200/-", getActivity().getResources().getString(R.string.OK), true);
                }

            }
        }
    }

    private double getSubTotal(ArrayList<Medicine> list)
    {
        double sub_total = 0;

        for (Medicine e : list)
        {
            double price = Double.parseDouble(e.getPrice());
            int quantity = e.getRequired_pack_size();

            sub_total += (price * quantity * 100) / 100;
        }

        return sub_total;
    }


    private boolean validate()
    {
        if(Medicine.isAllMedicineAvailable(this.mList).isEmpty())
        {
            if(this.mList.size() == 0)
            {
                Toast.makeText(getActivity(), "No medicine to order", Toast.LENGTH_LONG).show();
                return false;
            }

            else
            {
                return true;
            }
        }

        else
        {
            StringBuilder builder = new StringBuilder();
            HashMap<String, Integer> hm = Medicine.isAllMedicineAvailable(this.mList);

            int count = 0;

            for(Map.Entry m : hm.entrySet())
            {
                count++;

                if(m.getValue().toString().equals("0"))
                {
                    builder.append(m.getKey());
                }

                if(count != hm.size())
                {
                    builder.append(", ");
                }
            }

            builder.append(" out of stock. Do you want to remove and continue ?");

            new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_REMOVE_MEDICINE_AND_CONTINUE, this).
                    showDialogWithActionAndIcon("Out of Stock", builder.toString(), "Continue", "Cancel", true, R.drawable.ic_order_medicines);
        }

        return false;
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_REMOVE_MEDICINE_AND_CONTINUE)
        {
            ArrayList<Medicine> tmpList = new ArrayList<>();

            for (Medicine m: mList)
            {
                if(m.getAvailableQuantity() > 0)
                {
                    tmpList.add(m);
                }
            }

            if(tmpList.size() > 0)
            {
                if(getSubTotal(tmpList) > 200){
                    medicine_listener.onMedicineFound(tmpList);
                    medicine_listener.onPageSelection(4, "Enter Address");
                }else {
                    new CustomAlertDialog(getContext(), this).
                            showAlertDialogWithoutTitle("Minimum Order Rs.200/-", getActivity().getResources().getString(R.string.OK),true);
                }

            }

            else
            {
                Toast.makeText(getActivity(), "No medicine to order", Toast.LENGTH_LONG).show();
                this.checkAvailability();
            }
        }
    }

    @Override
    public void onNegativeAction()
    {

    }


    private void checkAvailability()
    {
        if(Medicine.isAllMedicineAvailable(this.mList).size() == this.mList.size())
        {
            //button_go.setBackground(getResources().getDrawable(R.drawable.round_ui_disable_button_style));
            button_go.setEnabled(false);
        }

        else
        {
            //button_go.setBackground(getResources().getDrawable(R.drawable.round_ui_button_style));
            button_go.setEnabled(true);
        }
    }
}