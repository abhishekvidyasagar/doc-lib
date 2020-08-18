package com.doconline.doconline.api;

/**
 * Created by chiranjitbardhan on 17/01/18.
 */

public interface OnDownloadProgress extends OnHttpResponse
{
    void onProgressUpdate(String... progress);
}