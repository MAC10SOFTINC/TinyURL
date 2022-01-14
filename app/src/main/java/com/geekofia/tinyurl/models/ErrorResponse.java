package com.geekofia.tinyurl.models;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("errorcode")
    private int errorcode;

    @SerializedName("errormessage")
    private String errormessage;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.errorcode = errorCode;
        this.errormessage = errorMessage;
    }

    public int getErrorCode() {
        return errorcode;
    }

    public String getErrorMessage() {
        return errormessage;
    }

    @Override
    public String toString() {
        return "errorResponse{" +
                "errorCode='" + errorcode + '\'' +
                ", errorMessage='" + errormessage + '\'' +
                '}';
    }
}
