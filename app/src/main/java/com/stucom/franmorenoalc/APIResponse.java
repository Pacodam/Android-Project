package com.stucom.franmorenoalc;



public class APIResponse<T> {
    private T data;
    private int count;
    private int errorCode;
    private String errorMsg;

    public APIResponse(T data, int count, int errorCode, String errorMsg) {
        this.data = data;
        this.count = count;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
}
