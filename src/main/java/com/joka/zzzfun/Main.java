package com.joka.zzzfun;

import com.joka.zzzfun.data.StringMap;
import com.joka.zzzfun.http.HttpClient;
import com.joka.zzzfun.http.OkClient;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

//        try {
//            Document document = Jsoup.connect("http://www.zzzfun.com/").get();
//            System.out.println(document.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkClient client = HttpClient.getHttpClient();

        Request.Builder builder = new Request.Builder().url("http://speedtest.tokyo.linode.com/100MB-tokyo.bin").get();
        client.donwloanFile(builder,new StringMap());

    }

}
