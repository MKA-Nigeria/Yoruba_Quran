package com.alium.quran_app_data.model;


public class DataLoadState {

    private Status status;
    private String message;

    public enum Status {
        LOADING,
        SUCCESS,
        FAILED
    }

    public DataLoadState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataLoadState that = (DataLoadState) o;

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

    public DataLoadState(Status status) {
        this.status = status;
    }


    public Status getStatus() {
        return status;
    }

    public static DataLoadState FAILED(String message) {
        return new DataLoadState(Status.FAILED, message);
    }

    public static DataLoadState LOADING() {
        return new DataLoadState(Status.LOADING);
    }

    public static DataLoadState SUCCESS() {
        return new DataLoadState(Status.SUCCESS);
    }

}
