package com.meatballs.youtrackkeeper.REST;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTClient {
    private static YouTrackRESTService youTrackApi;
    private Retrofit retrofit;
    final String TokenId;

    public RESTClient(String tokenId) {
        TokenId = tokenId;
    }

    public void Connect()
    {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request newRequest  = original.newBuilder()
                        .header("Authorization", "Bearer " + TokenId)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(newRequest);
            }
        }   ).build();
        Gson gson = new GsonBuilder()
                .setLenient() //Android ask it
                .create();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://keeper.myjetbrains.com/youtrack/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        youTrackApi = retrofit.create(YouTrackRESTService.class);
    }

    public static YouTrackRESTService getApi() {
        return youTrackApi;
    }
}
