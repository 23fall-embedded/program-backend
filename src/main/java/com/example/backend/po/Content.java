package com.example.backend.po;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
