package com.doconline.doconline.helper;

import android.content.Context;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

/**
 * Created by chiranjit on 04/07/17.
 */

public class SingleUploadBroadcastReceiver extends UploadServiceBroadcastReceiver
{
    public interface Delegate
    {
        void onProgress(final UploadInfo uploadInfo);
        void onError(final UploadInfo uploadInfo, final ServerResponse serverResponse, final Exception exception);
        void onCompleted(final UploadInfo uploadInfo, final ServerResponse serverResponse);
        void onCancelled(final UploadInfo uploadInfo);
    }

    private String mUploadID;
    private Delegate mDelegate;

    public void setUploadID(String uploadID)
    {
        mUploadID = uploadID;
    }

    public void setDelegate(Delegate delegate)
    {
        mDelegate = delegate;
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo)
    {
        try
        {
            if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null)
            {
                mDelegate.onProgress(uploadInfo);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse,
                        Exception exception) {

        try
        {
            if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null)
            {
                mDelegate.onError(uploadInfo, serverResponse, exception);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse)
    {
        try
        {
            if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null)
            {
                mDelegate.onCompleted(uploadInfo, serverResponse);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo)
    {
        try
        {
            if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null)
            {
                mDelegate.onCancelled(uploadInfo);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}