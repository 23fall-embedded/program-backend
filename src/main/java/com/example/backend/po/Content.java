package com.example.backend.po;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content {
    private String deviceType;
    private String iotId;
    private String requestId;
    private Object checkFailedData;
    private String productKey;
    private String gmtCreate;
    private String deviceName;
    private Map<String, Values> items;
}
