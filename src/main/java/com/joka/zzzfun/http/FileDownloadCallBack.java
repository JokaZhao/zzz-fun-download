package com.joka.zzzfun.http;

import com.joka.zzzfun.constant.Environment;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class FileDownloadCallBack implements Callback {

    private OnDownloadListen listen;

    private String saveDir;

    public FileDownloadCallBack(OnDownloadListen listen, String saveDir) {
        this.listen = listen;
        this.saveDir = saveDir;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        listen.onDownloadFailed();
    }

    //todo 异步下载
    @Override
    public void onResponse(Call call, Response response) throws IOException {

        String savePath = isExistDir(saveDir);
        InputStream input = null;
        OutputStream out = null;
        try {
            input = response.body().byteStream();

            long totalContentLength = response.body().contentLength();

            File file = new File(savePath +File.separator+ getNameFromUrl(response.request().url().toString()));

            out = FileUtils.openOutputStream(file);

            byte[] buf = new byte[4096];

            int n;
            for (long count = 0L; -1 != (n = input.read(buf)); count += (long)n) {
                IOUtils.write(buf,out);
                int progress = (int) (count * 1.0f / totalContentLength * 100);
                listen.onDownloading(progress);
            }

            out.flush();

            listen.onDownloadSuccess();

        } catch (Exception e) {
            e.printStackTrace();
            listen.onDownloadFailed();
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                //todo
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                //todo
            }
        }
    }

    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getFilePath(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
