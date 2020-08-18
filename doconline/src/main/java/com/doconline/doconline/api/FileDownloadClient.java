package com.doconline.doconline.api;

import android.os.AsyncTask;
import android.os.Environment;

import com.doconline.doconline.app.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chiranjitbardhan on 17/01/18.
 */

public class FileDownloadClient extends AsyncTask<String, String, String>
{
    private int requestCode, responseCode;
    private OnDownloadProgress listener;
    private boolean isDownLoadComplete;


    public FileDownloadClient(int requestCode, OnDownloadProgress listener)
    {
        this.requestCode = requestCode;
        this.listener = listener;
    }


    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        if(responseCode == HttpClient.OK && isDownLoadComplete)
        {
            listener.onPostExecute(requestCode, responseCode, String.valueOf(result));
        }
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... progress)
    {
        listener.onProgressUpdate(progress[0]);
    }

    protected String doInBackground(String... args)
    {
        try
        {
            //set the download URL, a url that points to a file on the internet
            //this is the file to be downloaded
            URL url = new URL(args[0]);

            //create the new connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty(Constants.ACCEPT, "application/json");
            urlConnection.setRequestProperty(Constants.CONTENT_TYPE, "application/json");

            //set up some things on the connection
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);

            //and connect!
            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            //set the path where we want to save the file
            //in this case, going to save it on the root directory of the
            //sd card.
            //File SDCardRoot = Environment.getExternalStorageDirectory();

            // External sdcard location
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.MEDIA_DIRECTORY_NAME);
            // File noMedia = new File ( Environment.getExternalStoragePublicDirectory(DIRECTORY_NAME), ".nomedia" );

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists())
            {

                if (!mediaStorageDir.mkdirs())
                {
                    //Log.d(DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                    return null;
                }

                    /*if (!noMedia.exists())
                    {
                        FileOutputStream noMediaOutStream = new FileOutputStream ( noMedia );
                        noMediaOutStream.write ( 0 );
                        noMediaOutStream.close();
                    }*/
            }

            //create a new file, specifying the path, and the filename
            //which we want to save the file as.
            File file = new File(mediaStorageDir, args[1]);

            //this will be used to write the downloaded data into the file we created
            FileOutputStream fileOutput = new FileOutputStream(file);

            //this will be used in reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file
            long totalSize = urlConnection.getContentLength();

            //variable to store total downloaded bytes
            long downloadedSize = 0;

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength; //used to store a temporary size of the buffer

            //now, read through the input buffer and write the contents to the file
            while ( (bufferLength = inputStream.read(buffer)) != -1 )
            {

                //add the data in the buffer to the file in the file output stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);

                //add up the size so we know how much is downloaded
                downloadedSize += bufferLength;

                //this is where you would do something to report the prgress, like this maybe
                //updateProgress(downloadedSize, totalSize);

                // After this onProgressUpdate will be called
                long progress = (downloadedSize * 100) / totalSize;
                publishProgress("" + progress);
            }

            if(downloadedSize == totalSize)
            {
                isDownLoadComplete = true;
            }

            //close the output stream when done
            //fileOutput.close();

            //catch some possible errors...
        }

        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}