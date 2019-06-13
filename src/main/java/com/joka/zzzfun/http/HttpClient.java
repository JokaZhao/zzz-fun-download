package com.joka.zzzfun.http;

import com.joka.zzzfun.constant.HttpConstatns;
import com.joka.zzzfun.data.StringMap;
import okhttp3.*;
import okio.BufferedSink;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class HttpClient {

    public static final String ContentTypeHeader = "Content-Type";
    public static final String DefaultMine = "application/octet-stream";
    public static final String JsonMine = "application/json";
    public static final String FormMine = "application/x-www-form-urlencoded";
    public static final StringMap EMPTY_HEADER = new StringMap();
    private final OkHttpClient client;

    /**
     * 构建一个默认配置的 HTTP Client 类
     */
    public HttpClient() {
        this(null, false, null,
                HttpConstatns.CONNECT_TIMEOUT, HttpConstatns.READ_TIMEOUT, HttpConstatns.WRITE_TIMEOUT,
                HttpConstatns.DISPATCHER_MAX_REQUESTS, HttpConstatns.DISPATCHER_MAX_REQUESTS_PER_HOST,
                HttpConstatns.CONNECTION_POOL_MAX_IDLE_COUNT, HttpConstatns.CONNECTION_POOL_MAX_IDLE_MINUTES);
    }

    public HttpClient(final Dns dns, final boolean hostFirst, final ProxyConfiguration proxy,
                      int connTimeout, int readTimeout, int writeTimeout, int dispatcherMaxRequests,
                      int dispatcherMaxRequestsPerHost, int connectionPoolMaxIdleCount,
                      int connectionPoolMaxIdleMinutes) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(dispatcherMaxRequests);
        dispatcher.setMaxRequestsPerHost(dispatcherMaxRequestsPerHost);
        ConnectionPool connectionPool = new ConnectionPool(connectionPoolMaxIdleCount,
                connectionPoolMaxIdleMinutes, TimeUnit.MINUTES);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.dispatcher(dispatcher);
        builder.connectionPool(connectionPool);
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                okhttp3.Response response = chain.proceed(request);
                IpTag tag = (IpTag) request.tag();
                try {
                    tag.ip = chain.connection().socket().getRemoteSocketAddress().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    tag.ip = "";
                }
                return response;
            }
        });
        if (dns != null) {
            builder.dns(new okhttp3.Dns() {
                @Override
                public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                    try {
                        return dns.lookup(hostname);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return okhttp3.Dns.SYSTEM.lookup(hostname);
                }
            });
        }
        if (proxy != null) {
            builder.proxy(proxy.proxy());
            if (proxy.user != null && proxy.password != null) {
                builder.proxyAuthenticator(proxy.authenticator());
            }
        }
        builder.connectTimeout(connTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        client = builder.build();
    }

    private static String userAgent() {
        String javaVersion = "Java/" + System.getProperty("java.version");
        String os = System.getProperty("os.name") + " "
                + System.getProperty("os.arch") + " " + System.getProperty("os.version");
        String sdk = "JokaZhao/" + HttpConstatns.VERSION;
        return sdk + " (" + os + ") " + javaVersion;
    }

    private static RequestBody create(final MediaType contentType,
                                      final byte[] content, final int offset, final int size) {
        if (content == null) throw new NullPointerException("content == null");

        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return size;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(content, offset, size);
            }
        };
    }

    public ZResponse get(String url) {
        return get(url, EMPTY_HEADER);
    }

    public ZResponse get(String url, StringMap header) {
        Request.Builder builder = new Request.Builder().get().url(url);
        return send(builder, header);
    }

    public ZResponse delete(String url, StringMap header) {
        Request.Builder builder = new Request.Builder().delete().url(url);
        return send(builder, header);
    }

    public ZResponse post(String url, byte[] body, StringMap header, String contentType) {
        RequestBody rbody;
        if (body != null && body.length > 0) {
            MediaType t = MediaType.parse(contentType);
            rbody = RequestBody.create(t, body);
        } else {
            rbody = RequestBody.create(null, new byte[0]);
        }
        return post(url, rbody, header);
    }

    public ZResponse post(String url, StringMap params, StringMap header) {

        final FormBody.Builder builder = new FormBody.Builder();
        params.forEach((key, value) -> {
            builder.add(key, value.toString());
        });

        return post(url, builder.build(), header);
    }

    public ZResponse post(String url, RequestBody body, StringMap header) {
        Request.Builder builder = new Request.Builder().url(url).post(body);
        return send(builder, header);
    }

    public ZResponse put(String url, byte[] body, StringMap headers, String contentType) {
        RequestBody rbody;
        if (body != null && body.length > 0) {
            MediaType t = MediaType.parse(contentType);
            rbody = RequestBody.create(t, body);
        } else {
            rbody = RequestBody.create(null, new byte[0]);
        }
        return put(url, rbody, headers);
    }

    private ZResponse put(String url, RequestBody body, StringMap headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url).put(body);
        return send(requestBuilder, headers);
    }

    public ZResponse multipartPost(String url,
                                   StringMap fields,
                                   String name,
                                   String fileName,
                                   byte[] fileBody,
                                   String mimeType,
                                   StringMap headers) {
        RequestBody file = RequestBody.create(MediaType.parse(mimeType), fileBody);
        return multipartPost(url, fields, name, fileName, file, headers);
    }

    public ZResponse multipartPost(String url,
                                   StringMap fields,
                                   String name,
                                   String fileName,
                                   File fileBody,
                                   String mimeType,
                                   StringMap headers) {
        RequestBody file = RequestBody.create(MediaType.parse(mimeType), fileBody);
        return multipartPost(url, fields, name, fileName, file, headers);
    }

    private ZResponse multipartPost(String url, StringMap fields, String name, String fileName, RequestBody file, StringMap header) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart(name, fileName, file);
        fields.forEach(((key, value) -> {
            builder.addFormDataPart(key, value.toString());
        }));
        builder.setType(HttpConstatns.CONTENT_TYPE_MULTIPART);
        MultipartBody body = builder.build();
        Request.Builder reuqestBuilder = new Request.Builder().url(url).post(body);
        return send(reuqestBuilder, header);

    }

    public ZResponse send(final Request.Builder requestBuilder, StringMap headers) throws RuntimeException {
        if (headers != null) {
            headers.forEach(new StringMap.Consumer() {
                public void accept(String key, Object value) {
                    requestBuilder.header(key, value.toString());
                }
            });
        }

        requestBuilder.header("User-Agent", userAgent());
        long start = System.currentTimeMillis();
        okhttp3.Response res = null;
        ZResponse r;
        double duration = (System.currentTimeMillis() - start) / 1000.0;
        IpTag tag = new IpTag();
        try {
            res = client.newCall(requestBuilder.tag(tag).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        r = new ZResponse(duration, res);
        if (r.statusCode >= 300) {
            throw new RuntimeException("Http Request Error.Error Code :" + r.statusCode);
        }

        return r;
    }

    private static class IpTag {
        public String ip = null;
    }
}
