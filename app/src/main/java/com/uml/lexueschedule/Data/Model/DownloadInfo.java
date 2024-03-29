package com.uml.lexueschedule.Data.Model;

/**
 * Created by zs
 * Date：2018年 09月 12日
 * Time：13:50
 * —————————————————————————————————————
 * About: 下载管理
 * —————————————————————————————————————
 *
 * Modified by 郭晓凡
 * Date: 2019年 08月 09日
 * Time: 14:49
 */
public class DownloadInfo {

    /**
     * 下载状态
     */
    public static final String DOWNLOAD = "download";
    public static final String DOWNLOAD_PAUSE = "pause";
    public static final String DOWNLOAD_CANCEL = "cancel";
    public static final String DOWNLOAD_OVER = "over";
    public static final String DOWNLOAD_ERROR = "error";

    public static final long TOTAL_ERROR = -1;//获取进度失败

    private String url;
    private String fileName;
    private String downloadStatus;
    private long total;
    private long progress;
    private boolean clickStart = true;

    public DownloadInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public boolean isClickStart() {
        return clickStart;
    }

    public void setClickStart(boolean clickStart) {
        this.clickStart = clickStart;
    }
}