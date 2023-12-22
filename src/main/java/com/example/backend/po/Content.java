package com.example.backend.po;

import java.util.Map;

public class Content {
    private String deviceType;
    private String iotId;
    private String requestId;
    private Object checkFailedData;
    private String productKey;
    private String gmtCreate;
    private String deviceName;
    private Map<String, Values> items;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getCheckFailedData() {
        return checkFailedData;
    }

    public void setCheckFailedData(Object checkFailedData) {
        this.checkFailedData = checkFailedData;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Map<String, Values> getItems() {
        return items;
    }

    public void setItems(Map<String, Values> items) {
        this.items = items;
    }
}
