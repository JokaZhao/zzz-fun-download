package com.joka.zzzfun;

import com.joka.zzzfun.data.StringMap;
import com.joka.zzzfun.http.HttpClient;
import com.joka.zzzfun.http.OkClient;
import okhttp3.Request;

public class Main {

    public static void main(String[] args) {

//        try {
//            Document document = Jsoup.connect("http://www.zzzfun.com/").get();
//            System.out.println(document.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkClient client = HttpClient.getHttpClient();

//        Request.Builder builder = new Request.Builder().url("http://speedtest.tokyo.linode.com/100MB-tokyo.bin").get();
//        Request.Builder builder = new Request.Builder().url("http://ting666.yymp3.com:86/new27/chenrui9/1.mp3").get();
        Request.Builder builder = new Request.Builder().url("http://down.kuwo.cn/mbox/kuwo_yy8844.exe").get();
        client.donwloanFile(builder, new StringMap());

    }

}
