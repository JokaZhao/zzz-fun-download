package com.joka.zzzfun.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joka.zzzfun.constant.HttpConstatns;
import com.joka.zzzfun.data.StringMap;
import okhttp3.MediaType;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

public final class ZResponse {

    public static final int InvalidArgument = -4;
    public static final int InvalidFile = -3;
    public static final int Cancelled = -2;
    public static final int NetworkError = -1;

    /**
     * 状态码
     */
    public final int statusCode;
    /**
     * 错误信息
     */
    public String error;
    /**
     * 请求消耗时间
     */
    public final double duration;

    private byte[] body;

    private Response response;

    public ZResponse(String error, double duration, Response response) {
        this.statusCode = response.code();
        this.error = error;
        this.duration = duration;
        this.response = response;
    }

    public ZResponse(double duration, Response response) {
        this.duration = duration;
        this.response = response;
        this.statusCode = response.code();
        try {
            this.body = response.body().bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String ctype(Response response) {
        MediaType mediaType = response.body().contentType();
        if (mediaType == null) {
            return "";
        }
        return mediaType.type() + "/" + mediaType.subtype();
    }

    public static class ErrorBody {
        public String error;
    }

    public boolean isJson() {
        return contentType().equals(HttpClient.JsonMine);
    }

    public String contentType(){
        return ctype(response);
    }

    public boolean isOK(){
        return statusCode == 200 && error == null;
    }

    public boolean isNetworkBroken(){
        return statusCode == NetworkError;
    }

    public boolean isServerError(){
        return (statusCode>=500 && statusCode<600 && statusCode!=579) || statusCode == 996;
    }

    public boolean neddSwitchServer(){
        return isNetworkBroken() || (statusCode >= 500 && statusCode <600 && statusCode!=579);
    }

    public boolean needRetry(){
        return isNetworkBroken() || isServerError() || statusCode == 406 || (statusCode == 200 && error != null);
    }

    public <T> T jsonToObject(Class<T> classOfT){
        if (!isJson()){
            return null;
        }
        String b = bodyString();
        return JSONObject.parseObject(b,classOfT);
    }

    public String bodyString(){
        return new String(body, HttpConstatns.UTF_8);
    }

    public StringMap jsonToMap(){
        if (!isJson()){
            return null;
        }
        String b = bodyString();
        Map map = JSONObject.parseObject(b, Map.class);
        return new StringMap(map);
    }

    public String url(){
        return response.request().url().toString();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
