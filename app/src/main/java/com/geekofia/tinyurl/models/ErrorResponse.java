package com.geekofia.tinyurl.models;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("errorcode")
    private int errorCode;

    @SerializedName("errormessage")
    private String errorMessage;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "errorResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
