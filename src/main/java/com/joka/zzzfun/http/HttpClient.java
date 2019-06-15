package com.joka.zzzfun.http;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpClient {

    private static class SimpleOkHttpClient{
        private static final OkClient okClient = new OkClient();
    }

    public static OkClient getHttpClient(){
        return SimpleOkHttpClient.okClient;

    }
}
