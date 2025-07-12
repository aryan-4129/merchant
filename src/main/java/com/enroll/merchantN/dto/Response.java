package com.enroll.merchantN.dto;


import lombok.Data;

/**
 * @author raghav
 */
@Data
public class Response<T> {
    private String status;
    private ErrorInfo errorInfo;
    private T result;
}
