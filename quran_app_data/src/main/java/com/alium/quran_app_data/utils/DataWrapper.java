package com.alium.quran_app_data.utils;

/**
 * Created by aliumujib on 18/03/2018.
 */

public class DataWrapper<T> {

    private T data;
    private Throwable error; //or A message String, Or whatever

    public DataWrapper(T data, Throwable error) {
        this.data = data;
        this.error = error;
    }

    public static <T> DataWrapper<T> createFromData(T data) {
        //requireNonNull(data, "No null values allowed");
        return new DataWrapper<>(data, null);
    }

    public Throwable getError() {
        return error;
    }

    public static <T> T requireNonNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public T getData() {
        return data;
    }

    public static <T> DataWrapper<T> createFromError(Throwable error) {
        //requireNonNull(error, "No null errors allowed");
        return new DataWrapper<>(null, error);
    }

    public boolean isError() {
        if (error != null) {
            return true;
        }

        return false;
    }

    public boolean hasData() {
        if (data != null) {
            return true;
        }

        return false;
    }

}