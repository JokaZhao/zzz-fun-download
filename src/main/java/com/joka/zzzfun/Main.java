package com.joka.zzzfun;

import com.joka.zzzfun.http.HttpClient;
import com.joka.zzzfun.http.ZResponse;
import okhttp3.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        OkHttpClient client = new OkHttpClient();

//        Request request = new Request.Builder().url("https://ss1.bdstatic.com/5eN1bjq8AAUYm2zgoY3K/r/www/cache/bdorz/baidu.min.css").get().build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                System.out.println("Fail :"+e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println("onResponse :"+response.body().string());
//            }
//        });

        String url = "https://ss1.bdstatic.com/5eN1bjq8AAUYm2zgoY3K/r/www/cache/bdorz/baidu.min.css";
        HttpClient client1 = new HttpClient();
        ZResponse zResponse = client1.get(url);

        System.out.println(zResponse.isOK());
        System.out.println(zResponse.toString());
        System.out.println(zResponse.url());
        System.out.println(zResponse.bodyString());



    }

}
