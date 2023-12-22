package com.example.backend;

import com.example.backend.po.Content;
import com.example.backend.po.Values;
import com.example.backend.utils.AmqpClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class test {

    AmqpClient amqpClient = new AmqpClient();

    @GetMapping("/getData")
    public Map<String, String> getData() {
        while(amqpClient.getContentSet().isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 取出 set 中最后插入的元素
        Content latestContent = amqpClient.getContentSet().iterator().next();
        Map<String, String> map = new HashMap<>();
        for(Map.Entry<String, Values> entry : latestContent.getItems().entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue().getValue());
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }

    @PostMapping("/sendData")
    public void sendData(@RequestParam String params) {
        // TODO: jsonify the params
        // TODO: transform it to Map<String, String> or sth else
//        amqpClient.sendMessage(params);
    }

}
