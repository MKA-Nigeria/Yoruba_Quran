package com.alium.yoruba_quran.ui.download;


public class DownloadState {

    private Status status;
    private String message;

    public enum Status {
        ONGOING,
        SUCCESS,
        FAILED
    }

    public DownloadState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadState that = (DownloadState) o;

        if (status != that.status) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }


    public String getMessage() {
        return message;
    }

    public DownloadState(Status status) {
        this.status = status;
    }


    public Status getStatus() {
        return status;
    }

    public static DownloadState FAILED(String message) {
        return new DownloadState(Status.FAILED, message);
    }

    public static DownloadState ONGOING() {
        return new DownloadState(Status.ONGOING);
    }

    public static DownloadState SUCCESS() {
        return new DownloadState(Status.SUCCESS);
    }

}
