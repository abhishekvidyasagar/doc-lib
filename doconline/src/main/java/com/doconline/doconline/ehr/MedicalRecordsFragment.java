package com.doconline.doconline.ehr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.FileViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.ehr.model.EHR;
import com.doconline.doconline.ehr.model.EHRResponse;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.helper.section.SectionParameters;
import com.doconline.doconline.helper.section.SectionedRecyclerViewAdapter;
import com.doconline.doconline.helper.section.StatelessSection;
import com.doconline.doconline.utils.DocumentUtils;
import com.doconline.doconline.utils.FileUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DELETE_EHR;
import static com.doconline.doconline.app.Constants.KEY_DATA;


public class MedicalRecordsFragment extends BaseFragment implements OnItemDeleted
{

    RecyclerView recycler_view;
    RelativeLayout layout_loading;
    LinearLayout layout_empty;
    TextView empty_message;
    RelativeLayout layout_root_view;

    private SectionedRecyclerViewAdapter mAdapter;

    private static final int HTTP_REQUEST_CODE_GET_EHR = 1;
    private static final int HTTP_REQUEST_CODE_DELETE_EHR = 2;

    private List<EHR> ehrList = new ArrayList<>();
    private List<EHR> tempList = new ArrayList<>();

    private boolean isLoading;
    private int CURRENT_PAGE;
    private int NEXT_PAGE;
    private int TEMP_CURRENT_PAGE;
    private int TEMP_NEXT_PAGE;

    private boolean isSearch;
    private String query = "";
    private String batchId;
    private int fileId;

    public MedicalRecordsFragment()
    {

    }


    public static MedicalRecordsFragment newInstance()
    {
        return new MedicalRecordsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ehr_medical_records, container, false);
      //  ButterKnife.bind(this, view);

        recycler_view =view.findViewById(R.id.recycler_view);
        layout_loading=view.findViewById(R.id.layout_loading);
       layout_empty=view.findViewById(R.id.layout_empty);
        empty_message=view.findViewById(R.id.empty_message);
        layout_root_view=view.findViewById(R.id.layout_root_view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.initAdapter();

        this.syncData();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void reinit()
    {
        this.setLoaded();

        this.CURRENT_PAGE = 0;
        this.NEXT_PAGE = 0;
        this.TEMP_CURRENT_PAGE = 0;
        this.TEMP_NEXT_PAGE = 0;

        this.ehrList.clear();
        this.tempList.clear();

        this.mAdapter.removeAllSections();
        this.mAdapter.notifyDataSetChanged();
    }

    public void syncData()
    {
        if (new InternetConnectionDetector(getContext()).isConnected())
        {
            this.reinit();

            new HttpClient(HTTP_REQUEST_CODE_GET_EHR, MyApplication.HTTPMethod.GET.getValue(),this)
                    .execute(mController.getFileURL());
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }


    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        mAdapter = new SectionedRecyclerViewAdapter();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(mLayoutManager);

        recycler_view.setAdapter(mAdapter);
        recycler_view.setNestedScrollingEnabled(false);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore()
            {
                Log.i("onLoadMore", "Load More");

                if(!new InternetConnectionDetector(getContext()).isConnected())
                {
                    Toast.makeText(getContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                isLoading = true;

                String url = mController.getFileURL() + (isSearch ? "/search?q=" + query + "&" : "?"  ) + "page=" + (CURRENT_PAGE + 1);

                new HttpClient(HTTP_REQUEST_CODE_GET_EHR, MyApplication.HTTPMethod.GET.getValue(), MedicalRecordsFragment.this)
                        .execute(url);
            }
        });

        this.load_more_on_scroll();
    }


    private void load_more_on_scroll()
    {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) recycler_view.getLayoutManager();

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                {
                    Log.i("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    Log.i("onScrolled", "" + visibleItemCount + "-" + totalItemCount + "-" + pastVisibleItems);

                    if (pastVisibleItems + visibleItemCount >= totalItemCount)
                    {
                        if(!isLoading && NEXT_PAGE != 0)
                        {
                            if (mAdapter.mOnLoadMoreListener != null)
                            {
                                mAdapter.mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    private void addSection(List<EHR> ehrList)
    {
        for(EHR ehr: ehrList)
        {
            mAdapter.addSection(new ContactsSection(ehr.getCategoryId(), ehr.getCreatedAt(),  ehr.getId(), ehr.getFiles()));
        }
    }

    public void setLoaded()
    {
        isLoading = false;
    }

    private class ContactsSection extends StatelessSection
    {
        private int category;
        private String date;
        private String batch;
        private List<FileUtils> list;

        ContactsSection(int category, String date, String batch, List<FileUtils> list)
        {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.recyclerlist_item_ehr_files)
                    .headerResourceId(R.layout.layout_ehr_header)
                    .build());

            this.category = category;
            this.date = date;
            this.batch = batch;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            final FileUtils file = list.get(position);
            itemHolder.tv_caption.setText(file.getCaption() == null ? "" : file.getCaption());
            itemHolder.ib_delete.setTag(position);

            try
            {
                if (FileUtils.isDocumentFile(file.getPath()))
                {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.file_placeholder);
                    itemHolder.iv_icon.setImageBitmap(bitmap);
                }

                else
                {
                    ImageLoader.loadThumbnail(getContext(), file.getPath(), itemHolder.iv_icon, R.drawable.ic_place_image, 120, 120);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            itemHolder.iv_icon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    if (!new InternetConnectionDetector(getContext()).isConnected())
                    {
                        new CustomAlertDialog(getContext(), MedicalRecordsFragment.this, getView()).snackbarForInternetConnectivity();
                        return;
                    }

                    Intent intent = new Intent(getActivity(), FileViewerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FILE", file);
                    startActivity(intent);
                }
            });

            itemHolder.ib_delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    batchId = batch;
                    fileId = file.getId();

                    delete_confirmation();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view)
        {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder)
        {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tv_category.setText(DocumentUtils.getCategoryName(mController.docCategoryList, category));

            try
            {
                String local_date = Helper.UTC_to_Local_TimeZone(date);

                SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
                Date dateObj = formatter.parse(local_date);
                formatter = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
                headerHolder.tv_date.setText(formatter.format(dateObj));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder
    {
       // @BindView(R.id.tv_date)
        TextView tv_date;
     //   @BindView(R.id.tv_category)
        TextView tv_category;

        HeaderViewHolder(View view)
        {
            super(view);
            //ButterKnife.bind(this, itemView);
             tv_date = view.findViewById(R.id.tv_date) ;
            tv_category = view.findViewById(R.id.tv_category);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv_caption;
        ImageView iv_icon;
        ImageButton ib_delete;

        ItemViewHolder(View view)
        {
            super(view);
            //ButterKnife.bind(this, itemView);
            tv_caption =  view.findViewById(R.id.tv_caption);
            iv_icon =  view.findViewById(R.id.iv_icon);
            ib_delete =  view.findViewById(R.id.ib_delete);

        }
    }


    @Override
    public void onPreExecute()
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        layout_empty.setVisibility(View.GONE);

        if(isSearch)
        {
            getEhrActivity().showSearchProgressBar();
        }

        else
        {
            layout_loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_GET_EHR && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    EHRResponse ehrResponse = new Gson().fromJson(json.toString(), EHRResponse.class);

                    if(isLoading)
                    {
                        setLoaded();
                    }

                    if(isSearch && CURRENT_PAGE == 0)
                    {
                        this.ehrList.clear();
                        this.mAdapter.removeAllSections();
                    }

                    CURRENT_PAGE = ehrResponse.getCurrentPage();
                    NEXT_PAGE = ehrResponse.getNextPage();

                    this.ehrList.addAll(ehrResponse.getEHRList());
                    this.addSection(ehrResponse.getEHRList());

                    if(!isSearch)
                    {
                        TEMP_CURRENT_PAGE = CURRENT_PAGE;
                        TEMP_NEXT_PAGE = NEXT_PAGE;

                        tempList.addAll(ehrResponse.getEHRList());
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    this.mAdapter.notifyDataSetChanged();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_DELETE_EHR && responseCode == HttpClient.NO_RESPONSE)
            {
                syncData();
                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(ehrList.size() == 0)
            {
                if(isSearch)
                {
                    empty_message.setText("No Records Found");
                }

                else
                {
                    empty_message.setText("At a Glance: View all your past medical\nrecords here!");
                }

                layout_empty.setVisibility(View.VISIBLE);
            }

            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            layout_loading.setVisibility(View.GONE);

            getEhrActivity().hideSearchProgressBar();
            getEhrActivity().hideFabBackground();
        }
    }


    public void changedSearch(CharSequence text)
    {
        this.query = text.toString();

        if(!isSearch)
        {
            return;
        }

        getEhrActivity().showSearchProgressBar();

        CURRENT_PAGE = 0;
        NEXT_PAGE = 0;

        String url = mController.getFileURL() + "/search?q=" + query;
        new HttpClient(HTTP_REQUEST_CODE_GET_EHR, MyApplication.HTTPMethod.GET.getValue(), MedicalRecordsFragment.this)
                .execute(url);
    }


    public void hideSearch()
    {
        this.isSearch = false;

        CURRENT_PAGE = TEMP_CURRENT_PAGE;
        NEXT_PAGE = TEMP_NEXT_PAGE;

        this.ehrList.clear();
        this.ehrList.addAll(tempList);

        this.mAdapter.removeAllSections();
        this.addSection(this.ehrList);

        this.mAdapter.notifyDataSetChanged();
    }


    private EHRActivity getEhrActivity()
    {
        return (EHRActivity) getActivity();
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Show search action bar if search icon is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_search) {
            this.isSearch = true;
            getEhrActivity().showSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemDeleted(int index, int item_id)
    {

    }

    private void delete_confirmation()
    {
        new CustomAlertDialog(getActivity(), DIALOG_REQUEST_CODE_DELETE_EHR, MedicalRecordsFragment.this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_are_you_sure),
                        getResources().getString(R.string.dialog_content_delete_record),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
    }

    // delete the selected document
    @Override
    public void onPositiveAction(int requestCode)
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            if(requestCode == DIALOG_REQUEST_CODE_DELETE_EHR)
            {
                getEhrActivity().showFabBackground();
                String url = mController.getFileURL() + "/" + batchId + "/" + fileId;

                new HttpClient(HTTP_REQUEST_CODE_DELETE_EHR, MyApplication.HTTPMethod.DELETE.getValue(), MedicalRecordsFragment.this)
                        .execute(url);
            }
        }

        else
        {
            new CustomAlertDialog(getContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }
}