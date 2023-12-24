package com.example.backend;

import com.example.backend.po.Content;
import com.example.backend.po.Values;
import com.example.backend.utils.AmqpClient;

import jakarta.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.*;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class test {

    AmqpClient amqpClient = new AmqpClient();

    @GetMapping("/getData")
    public Map<String, String[]> getData() {
        while(amqpClient.getStatckContent().isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 取出 set 中最后插入的元素
        Content latestContent = amqpClient.getStatckContent().peek();
        Map<String, String[]> map = new HashMap<>();
        for (Map.Entry<String, Values> entry : latestContent.getItems().entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }

    @PostMapping("/sendData")
    public void sendData(HttpServletRequest request) {
        String led = "", adm = "", loc = "";
        if (request.getParameterMap().containsKey("led")) {
            led = request.getParameter("led");
            System.out.println("led: " + led);
        }
        if (request.getParameterMap().containsKey("adm")) {
            adm = request.getParameter("adm");
            System.out.println("adm: " + adm);
        }
        if (request.getParameterMap().containsKey("loc")) {
            loc = request.getParameter("loc");
            System.out.println("loc: " + loc);
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");

        if (led.length() != 0) {
            stringBuilder.append("\"led\": \"" + led + "\",");
        }
        if (adm.length() != 0) {
            stringBuilder.append("\"adm\": \"" + adm + "\",");
        }
        if (loc.length() != 0) {
            stringBuilder.append("\"loc\": \"" + loc + "\",");
        }

        stringBuilder.append("}");

        String jsonString = stringBuilder.toString();
        System.out.println(jsonString);

        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Map<String, String> map = JSONObject.parseObject(
            jsonObject.toJSONString(), new TypeReference<Map<String, String>>() {}
        );
        
        AmqpClient.sendMessage(map);
        // System.out.println(map);
    }

}
