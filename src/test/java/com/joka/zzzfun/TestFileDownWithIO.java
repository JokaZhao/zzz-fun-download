package com.joka.zzzfun;

import com.joka.zzzfun.data.StringMap;
import com.joka.zzzfun.http.HttpClient;
import com.joka.zzzfun.http.OkClient;
import okhttp3.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created on 2019/7/3 18:54.
 *
 * @author zhaozengjie
 * Description : Download file with Java IO
 */

public class TestFileDownWithIO {

    private OkClient okClient;

    @Before
    public void before(){
        okClient = HttpClient.getHttpClient();
    }

    @Test
    public void downloadFileWithIO(){
        Request.Builder builder = new Request.Builder().url("http://down.kuwo.cn/mbox/kuwo_yy8844.exe").get();
        okClient.donwloanFile(builder, new StringMap());



    }

}
