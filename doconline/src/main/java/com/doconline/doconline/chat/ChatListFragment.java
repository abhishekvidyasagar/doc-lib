package com.doconline.doconline.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.ChatListRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_CHAT_CONFIRMATION;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_ONETIME_TYPE;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;
import static com.doconline.doconline.chat.ChatHistoryActivity.HTTP_REQUEST_CODE;

/**
 * Created by chiranjitbardhan on 23/08/17.
 */

public class ChatListFragment extends BaseFragment implements View.OnClickListener
{
    private OnPageSelection page_listener;
    private OnHttpResponse response_listener;
    private OnTaskCompleted task_listener;

    private ChatListRecyclerAdapter mAdapter;


    RecyclerView mRecyclerView;
    LinearLayout layout_empty;
    TextView empty_message;
    Button btn_go;

    private List<ChatMessage> mMessages = new ArrayList<>();
    private boolean isLoading;
    private int CURRENT_PAGE, LAST_PAGE;


    public static ChatListFragment newInstance()
    {
        return new ChatListFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
      // ButterKnife.bind(this, view);
         mRecyclerView  = view.findViewById(R.id.recycler_view);
        layout_empty= view.findViewById(R.id.layout_empty);
         empty_message= view.findViewById(R.id.empty_message);
        btn_go= view.findViewById(R.id.btnDone);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.initChatAdapter();
        this.btn_go.setOnClickListener(this);
        this.btn_go.setText("Start New Chat Session");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if(!new InternetConnectionDetector(getContext()).isConnected())
        {
            new CustomAlertDialog(getContext(), ChatListFragment.this, getView()).snackbarForInternetConnectivity();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if (getActivity() instanceof OnHttpResponse)
        {
            response_listener = (OnHttpResponse) getActivity();
        }

        if (getActivity() instanceof OnPageSelection)
        {
            page_listener = (OnPageSelection) getActivity();
        }

        if (getActivity() instanceof OnTaskCompleted)
        {
            task_listener = (OnTaskCompleted) getActivity();
        }

        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), this).execute(mController.getChatURL());
        }

        else
        {
            new CustomAlertDialog(getContext(), ChatListFragment.this, getView()).snackbarForInternetConnectivity();
        }
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnDone) {
            if (mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_ONETIME_TYPE)
                    || mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID)) {
                new CustomAlertDialog(getContext(), this, getView()).showSnackbar("You cannot use chat feature in current plan", CustomAlertDialog.LENGTH_SHORT);
                return;
            }

            new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_CHAT_CONFIRMATION, this)
                    .showDialogWithActionAndIcon(getResources().getString(R.string.dialog_title_chat_confirmation),
                            getResources().getString(R.string.dialog_content_chat_confirmation),
                            getResources().getString(R.string.StartNow),
                            getResources().getString(R.string.NoThanks), true, R.drawable.ic_chat);
        }
    }


    private void initChatAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChatListRecyclerAdapter(getContext(), this, mMessages);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new ChatListRecyclerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int i)
            {
                // get session_id to get the particular chat
                ChatHistoryActivity.session_id = mMessages.get(i).session_id;
                page_listener.onPageSelection(1, ""+mMessages.get(i).getDoctor_id());
            }
        });


        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore()
            {
                Log.v("onLoadMore", "Load More");

                if(!new InternetConnectionDetector(getContext()).isConnected())
                {
                    new CustomAlertDialog(getContext(), ChatListFragment.this, getView()).snackbarForInternetConnectivity();
                    return;
                }

                isLoading = true;

                Handler handler = new Handler();
                final Runnable r = new Runnable()
                {
                    public void run()
                    {
                        mMessages.add(null);
                        mAdapter.notifyItemInserted(mMessages.size() - 1);
                    }
                };

                handler.post(r);
                new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), ChatListFragment.this).execute(mController.getChatURL() + "?page=" + (CURRENT_PAGE + 1));
            }
        });

        this.load_more_on_scroll();
    }


    private void load_more_on_scroll()
    {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                {
                    Log.v("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount >= totalItemCount)
                    {
                        if(!isLoading && CURRENT_PAGE != LAST_PAGE)
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

    public void setLoaded()
    {
        isLoading = false;
    }


    private void json_data(String json_data)
    {
        if(isLoading)
        {
            mMessages.remove(mMessages.size() - 1);
            mAdapter.notifyItemRemoved(mMessages.size());
            setLoaded();
        } else {
            mMessages.clear();
        }

        try
        {
            JSONObject json = new JSONObject(json_data);

            CURRENT_PAGE = json.getInt(Constants.KEY_CURRENT_PAGE);
            LAST_PAGE = json.getInt(Constants.KEY_LAST_PAGE);

            mMessages.addAll(ChatMessage.getMessageListFromJSON(json.getString(KEY_DATA)));
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            this.adapter_refresh();
        }
    }


    private void adapter_refresh()
    {
        try
        {
            if(mMessages.size() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No History Found");
            }

            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            mAdapter.notifyDataSetChanged();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
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
            if(requestCode == HTTP_REQUEST_CODE && responseCode == HttpClient.OK)
            {
                try
                {

                    JSONObject json = new JSONObject(response);
                    this.json_data(json.getString(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

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
            response_listener.onPostExecute(requestCode, responseCode, response);
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

    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_CHAT_CONFIRMATION)
        {
            startActivity(new Intent(getActivity(), FirebaseChatActivity.class));
        }
    }

    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {
        task_listener.onTaskCompleted(flag, code, message);
    }
}