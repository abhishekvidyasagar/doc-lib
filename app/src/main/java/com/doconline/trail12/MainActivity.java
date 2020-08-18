package com.doconline.trail12;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.doconline.doconline.SplashScreenActivity;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait");
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    public void goToDoconline(View view) {
        pd.show();
        final JsonObject requestObject = new JsonObject();
        requestObject.addProperty("first_name","Abhishek");
        requestObject.addProperty("middle_name","VidyaSagar");
        requestObject.addProperty("last_name","Velpula");
        requestObject.addProperty("email","abhishekvidyasagar@gmail.com");
        requestObject.addProperty("mobile_no","9948632536");
        requestObject.addProperty("date_of_birth","1991-06-04");
        requestObject.addProperty("gender","Male");
        requestObject.addProperty("country_id",362);
        requestObject.addProperty("member_id","DOC0071");
        requestObject.addProperty("service_starts_at","2020-01-01 12:11:26");
        requestObject.addProperty("service_ends_at","2020-12-31 12:11:26");

        Log.e("AAA","request object : "+requestObject);
        Call<JsonObject> call2 = apiInterface.doLoginAPI(requestObject);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pd.dismiss();
                JsonObject responseObject = response.body();
                Log.e("AAA","RESPONSE : "+responseObject);
                SplashScreenActivity.start(MainActivity.this, String.valueOf(requestObject), String.valueOf(responseObject));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                pd.dismiss();
            }
        });

    }
}



class APIClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
                //.baseUrl("https://33a1df3b.ngrok.io")
                .baseUrl("https://demo.doconline.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}

interface APIInterface {

    //@GET("/json")
    //Call<JSONObject> getSynonyms();

    @Headers("Accept:application/json")
    @POST("/api/aes-decrypt")
    Call<JsonObject> sendEncryptedString(@Body JsonObject jsonObject);

    @Headers({"Accept:application/json","Content-Type:application/json","Authorization:Bearer QFLqog5py2guJPluS4hvw86n26PHNp1r"})
    @POST("/api/customer/user-login")
    Call<JsonObject> doLoginAPI(@Body JsonObject jsonObject);


}
