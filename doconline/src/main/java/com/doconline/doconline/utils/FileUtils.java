package com.doconline.doconline.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.helper.Helper;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_FILE_NAME;
import static com.doconline.doconline.app.Constants.KEY_TITLE;

public class FileUtils implements Serializable {

    @SerializedName(value = "id", alternate = "index")
    private int id;
    @SerializedName(value = "file_name", alternate = "url")
    private String path;
    @SerializedName("title")
    private String caption;

    public FileUtils(String path, String caption)
    {
        this.path = path;
        this.caption = caption;
    }

    public FileUtils(int id, String path, String caption)
    {
        this.id = id;
        this.path = path;
        this.caption = caption;
    }


    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCaption()
    {
        return this.caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }


    public static boolean isDocumentFile(String file_path)
    {
        if(URLUtil.isValidUrl(file_path))
        {
            String url = getUrlWithoutParameters(file_path);
            String extension = url.substring(url.lastIndexOf("."));
            return Arrays.asList(Constants.FILE_EXTENSIONS).contains(extension.substring(1).toLowerCase());
        }

        String extension = file_path.substring(file_path.lastIndexOf("."));
        return Arrays.asList(Constants.FILE_EXTENSIONS).contains(extension.substring(1).toLowerCase());
    }

    private static String getUrlWithoutParameters(String _url)
    {
        String url = _url;

        try
        {
            url = url.replaceFirst("\\?.*$", "");
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static String getFileName(String _url)
    {
        String url = _url;

        try
        {
            url = getUrlWithoutParameters(url);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new File(url).getName();
    }

    public static String getFileExtension(String file_path)
    {
        String extension = file_path.substring(file_path.lastIndexOf("."));
        return extension.substring(1).toLowerCase();
    }


    public static void openFileBrowser(Context context, String fileName)
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.MEDIA_DIRECTORY_NAME);

        if(!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdir();
        }

        Uri fileUri;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
        {
            fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(mediaStorageDir, fileName));
        }

        else
        {
            fileUri = Uri.fromFile(new File(mediaStorageDir, fileName));
        }

        if(Helper.fileExist(fileUri.getPath()))
        {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();

            Intent target = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(FileUtils.getFileExtension(fileName));

            Log.i("FILE_URI", "" + mimeType);

            target.setDataAndType(fileUri, mimeType);
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = Intent.createChooser(target, "Open File");

            try
            {
                context.startActivity(intent);
            }

            catch (ActivityNotFoundException e)
            {
                Toast.makeText(context, "File Not Found", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        else
        {
            Toast.makeText(context, "Invalid Source", Toast.LENGTH_LONG).show();
        }
    }


    public static JSONArray getFileJsonArray(List<FileUtils> utils)
    {
        JSONArray array = new JSONArray();

        try
        {
            for (FileUtils fileUtils: utils)
            {
                JSONObject json = new JSONObject();
                json.put(KEY_FILE_NAME, fileUtils.getPath());
                json.put(KEY_TITLE, fileUtils.getCaption());

                array.put(json);
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return array;
    }
}