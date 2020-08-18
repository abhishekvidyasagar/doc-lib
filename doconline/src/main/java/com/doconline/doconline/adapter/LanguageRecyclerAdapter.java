package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.helper.dragable.ItemTouchHelperAdapter;
import com.doconline.doconline.helper.dragable.ItemTouchHelperViewHolder;
import com.doconline.doconline.helper.dragable.OnStartDragListener;
import com.doconline.doconline.model.PreferredLanguage;

import java.util.Collections;
import java.util.List;


public class LanguageRecyclerAdapter extends RecyclerView.Adapter<LanguageRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter
{
    private List<PreferredLanguage> languageList;
    private Context context;
    private OnTaskCompleted listener;
    private OnItemClickListener clickListener;
    private boolean is_selectable;

    private static final int TYPE_ITEM = 0;
    private final LayoutInflater mInflater;
    //private final OnStartDragListener mDragStartListener;


    public LanguageRecyclerAdapter(Context context, OnTaskCompleted listener, OnStartDragListener dragListener, List<PreferredLanguage> languageList)
    {
        this.context = context;
        this.listener = listener;
        this.languageList = languageList;
        this.is_selectable = true;

        this.mInflater = LayoutInflater.from(context);
       // this.mDragStartListener = dragListener;
    }

    public LanguageRecyclerAdapter(Context context/*, OnStartDragListener dragListener,*/ ,List<PreferredLanguage> languageList)
    {
        this.context = context;
        this.languageList = languageList;
        this.is_selectable = false;

        this.mInflater = LayoutInflater.from(context);
      //  this.mDragStartListener = dragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        if (i == TYPE_ITEM)
        {
            if(is_selectable)
            {
                View view = mInflater.inflate(R.layout.recyclerlist_item_preferred_language, viewGroup, false);
                return new ViewHolder(view);
            }

            View view = mInflater.inflate(R.layout.recyclerlist_item_language, viewGroup, false);
            return new ViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position)
    {
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int i)
    {
        vHolder.bindData(languageList.get(i));
    }

    @Override
    public int getItemCount()
    {
        return languageList == null ? 0 : languageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

        private TextView tv_language;
        private CheckBox check_area;
        private RadioButton radio_area;
        private ImageView image_menu;

        private ViewHolder(View itemView)
        {
            super(itemView);

            tv_language = itemView.findViewById(R.id.tv_language);

            if(is_selectable)
            {
                image_menu = itemView.findViewById(R.id.image_menu);
                check_area = itemView.findViewById(R.id.check_area);
                radio_area = itemView.findViewById(R.id.radio_area);

                /*check_area.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton v, boolean isChecked)
                    {
                        listener.onTaskCompleted(isChecked, getAdapterPosition(), "");
                    }
                });*/

                radio_area.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        RadioButton radioButton = (RadioButton) view;
                        listener.onTaskCompleted(radioButton.isChecked(), getAdapterPosition(), "");
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(!radio_area.isChecked() && radio_area.isEnabled())
                        {
                            radio_area.setChecked(true);
                            listener.onTaskCompleted(true, getAdapterPosition(), "");
                        }
                    }
                });
            }
        }

        private void bindData(PreferredLanguage language)
        {
            tv_language.setText(Helper.toCamelCase(language.getLanguage()));

            if(is_selectable)
            {
                if(language.getStatus() == 1)
                {
                    //check_area.setChecked(true);
                    radio_area.setChecked(true);
                }

                else
                {
                    //check_area.setChecked(false);
                    radio_area.setChecked(false);
                }

                if(language.getReadOnly())
                {
                    //check_area.setEnabled(false);
                    //radio_area.setEnabled(false);
                }

                else
                {
                    //check_area.setEnabled(true);
                    //radio_area.setEnabled(true);
                }

                /*image_menu.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)
                        {
                            mDragStartListener.onStartDrag(vHolder);
                        }

                        return false;
                    }
                });*/
            }

            else
            {
                if(language.getIsEnabled())
                {
                    itemView.findViewById(R.id.layout_main).setBackground(ContextCompat.getDrawable(context, R.drawable.primary_color_green_round_button_style));
                }

                else
                {
                    itemView.findViewById(R.id.layout_main).setBackground(ContextCompat.getDrawable(context, R.drawable.primary_color_grey_round_button_style));
                }
            }
        }

        @Override
        public void onClick(View v)
        {
            //clickListener.onItemClick(v, languageList.get(getAdapterPosition()).code);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }


    @Override
    public void onItemDismiss(int position)
    {
        languageList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        Log.wtf("OnItemMove", "Log position " + fromPosition + " to " + toPosition);

        if (fromPosition < languageList.size() && toPosition < languageList.size())
        {
            if (fromPosition < toPosition)
            {
                for (int i = fromPosition; i < toPosition; i++)
                {
                    Collections.swap(languageList, i, i + 1);
                }
            }

            else
            {
                for (int i = fromPosition; i > toPosition; i--)
                {
                    Collections.swap(languageList, i, i - 1);
                }
            }

            notifyItemMoved(fromPosition, toPosition);

            //int fromPositionId = languageList.get(fromPosition).getID();
            //int toPositionId = languageList.get(toPosition).getID();

            //new SQLiteHelper(context).update(fromPositionId, toPosition);
            //new SQLiteHelper(context).update(toPositionId, fromPositionId);
        }

        return true;
    }
}