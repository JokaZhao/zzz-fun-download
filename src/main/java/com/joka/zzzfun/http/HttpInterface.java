package com.joka.zzzfun.http;

import com.joka.zzzfun.data.StringMap;

import java.io.File;

public interface HttpInterface {

    ZResponse get(String url);

    ZResponse get(String url, StringMap header);

    ZResponse delete(String url, StringMap header);

    ZResponse post(String url, byte[] body, StringMap header, String contentType);

    ZResponse post(String url, StringMap params, StringMap header);

    ZResponse put(String url, byte[] body, StringMap headers, String contentType);

    ZResponse multipartPost(String url, StringMap fields, String name, String fileName, byte[] fileBody, String mimeType, StringMap headers);

    ZResponse multipartPost(String url, StringMap fields, String name, String fileName, File fileBody, String mimeType, StringMap headers);


}
