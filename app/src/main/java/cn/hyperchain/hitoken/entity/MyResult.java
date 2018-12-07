package cn.hyperchain.hitoken.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyResult<T> {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;

//    @SerializedName("msg")
//    @Expose
//    private String msg;

    @SerializedName("data")
    @Expose
    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}