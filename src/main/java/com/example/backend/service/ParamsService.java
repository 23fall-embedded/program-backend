package com.example.backend.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.po.Content;
import com.example.backend.po.License;
import com.example.backend.po.Params;
import com.example.backend.po.Values;
import com.example.backend.repo.LicenseRepo;
import com.example.backend.repo.ParamsRepo;

@Service
public class ParamsService {
    @Autowired
    private ParamsRepo paramsRepo;
    @Autowired
    private LicenseRepo licenseRepo;

    private static int licenseCnt = 1;
    private static int tableRowCnt = 1;

    public void saveParamsAndLicenses(Content content) {
        Params params = new Params();
        License license = new License();

        params.setFire(content.getItems().get("fire").getValue()[0]);
        params.setHumidity(content.getItems().get("humidity").getValue()[0]);
        params.setImg(content.getItems().get("img").getValue()[0]);
        params.setLicId(licenseCnt);
        params.setLight(content.getItems().get("light").getValue()[0]);
        params.setMq3(content.getItems().get("mq3").getValue()[0]);
        params.setTemperature(content.getItems().get("temperature").getValue()[0]);
        params.setTs(new Timestamp(System.currentTimeMillis()));

        String[] sa = content.getItems().get("licenses").getValue();
        for (String s : sa) {
            license.setTableId(tableRowCnt);
            license.setLicId(licenseCnt);
            license.setLicNum(s);
            licenseRepo.save(license);
            tableRowCnt++;
        }

        paramsRepo.save(params);
        licenseCnt++;
    }

    public Content getAndTurnParamsLicenseIntoContent() {
        Params params = paramsRepo.getLatestParams();
        List<String> licenses = licenseRepo.getLicenseList(params.getLicId());

        Content content = new Content();
        Map<String, Values> map = new HashMap<>();
        map.put("fire", new Values(new String[]{params.getFire()}, null));
        map.put("humidity", new Values(new String[]{params.getHumidity()}, null));
        map.put("img", new Values(new String[]{params.getImg()}, null));
        map.put("light", new Values(new String[]{params.getLight()}, null));
        map.put("mq3", new Values(new String[]{params.getMq3()}, null));
        map.put("temperature", new Values(new String[]{params.getTemperature()}, null));

        String[] licenseTmpArray = licenses.toArray(new String[]{});
        map.put("licenses", new Values(licenseTmpArray, null));

        content.setItems(map);

        return content;
    }

    public Integer countParamsRows() {
        return paramsRepo.countByParamsRows();
    }
}
