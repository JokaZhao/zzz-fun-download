package com.joka.zzzfun.http.listener;

import com.joka.zzzfun.http.OnDownloadListen;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultFileDownloanListener implements OnDownloadListen {

    private int process = 0;

    @Override
    public void onDownloadSuccess() {
        System.out.println("下载完成");
    }

    @Override
    public void onDownloading(int progress) {
        System.out.println("下载进度：" + progress);
    }

    @Override
    public void onDownloadFailed() {
        System.out.println("下载失败");
    }
}
