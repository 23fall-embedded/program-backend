package com.example.backend.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.iot.model.v20180120.PubRequest;
import com.aliyuncs.iot.model.v20180120.PubResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.example.backend.controller.DataController;
import com.example.backend.po.Content;
import com.example.backend.po.Values;
import com.example.backend.service.ParamsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AmqpClient {
    private static DataController controller;
    private final static Logger logger = LoggerFactory.getLogger(AmqpClient.class);
    /**
     * 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例使用环境变量获取 AccessKey 的方式进行调用，仅供参考
     */

    private static Stack<Content> stackContent = new Stack<>();

    private static String accessKey = "LTAI5t5kCc7kgkAMzCADzUVX";
    private static String accessSecret = "LgO9jF1OG0qR3XuWM05a3RzpI2GdUd";
    private static String consumerGroupId = "DEFAULT_GROUP";

    //iotInstanceId：实例ID。若是2021年07月30日之前（不含当日）开通的公共实例，请填空字符串。
    private static String iotInstanceId = "iot-06z00c6qmr7jsch";

    //控制台服务端订阅中消费组状态页客户端ID一栏将显示clientId参数。
    //建议使用机器UUID、MAC地址、IP等唯一标识等作为clientId。便于您区分识别不同的客户端。
    private static String clientId = "pi-dom-192.168.31.173";

    //${YourHost}为接入域名，请参见AMQP客户端接入说明文档。
    private static String host = "iot-06z00c6qmr7jsch.amqp.iothub.aliyuncs.com";

    private static String productKey = "k0kh4u9Sfng";

    // 指定单个进程启动的连接数
    // 单个连接消费速率有限，请参考使用限制，最大64个连接
    // 连接数和消费速率及rebalance相关，建议每500QPS增加一个连接
    private static int connectionCount = 4;

    public void setStackContent(Stack<Content> content) {
        stackContent = content;
    }

    public Stack<Content> getStackContent() {
        return stackContent;
    }

    public AmqpClient(DataController controller) {
        try {
            this.controller = controller;
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //业务处理异步线程池，线程池参数可以根据您的业务特点调整，或者您也可以用其他异步方式处理接收到的消息。
    private final static ExecutorService executorService = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
        new LinkedBlockingQueue(50000));

    public static void run() throws Exception {
        List<Connection> connections = new ArrayList<>();

        //参数说明，请参见AMQP客户端接入说明文档。
        for (int i = 0; i < connectionCount; i++) {
            long timeStamp = System.currentTimeMillis();
            //签名方法：支持hmacmd5、hmacsha1和hmacsha256。
            String signMethod = "hmacsha1";

            //userName组装方法，请参见AMQP客户端接入说明文档。
            String userName = clientId + "-" + i + "|authMode=aksign"
                + ",signMethod=" + signMethod
                + ",timestamp=" + timeStamp
                + ",authId=" + accessKey
                + ",iotInstanceId=" + iotInstanceId
                + ",consumerGroupId=" + consumerGroupId
                + "|";
            //计算签名，password组装方法，请参见AMQP客户端接入说明文档。
            String signContent = "authId=" + accessKey + "&timestamp=" + timeStamp;
            String password = doSign(signContent, accessSecret, signMethod);
            String connectionUrl = "failover:(amqps://" + host + ":5671?amqp.idleTimeout=80000)"
                + "?failover.reconnectDelay=30";

            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("connectionfactory.SBCF", connectionUrl);
            hashtable.put("queue.QUEUE", "default");
            hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            Context context = new InitialContext(hashtable);
            ConnectionFactory cf = (ConnectionFactory)context.lookup("SBCF");
            Destination queue = (Destination)context.lookup("QUEUE");
            // 创建连接。
            Connection connection = cf.createConnection(userName, password);
            connections.add(connection);

            ((JmsConnection)connection).addConnectionListener(myJmsConnectionListener);
            // 创建会话。
            // Session.CLIENT_ACKNOWLEDGE: 收到消息后，需要手动调用message.acknowledge()。
            // Session.AUTO_ACKNOWLEDGE: SDK自动ACK（推荐）。
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            connection.start();
            // 创建Receiver连接。
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(messageListener);
        }

//        sendMessage("red");
        System.out.println("amqp demo is started successfully");

//        // 结束程序运行
//        Thread.sleep(60 * 1000);
//        System.out.println("run shutdown");
//
//        connections.forEach(c-> {
//            try {
//                c.close();
//            } catch (JMSException e) {
//                System.err.println("failed to close connection" + e);
//            }
//        });
//
//        executorService.shutdown();
//        if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
//            System.out.println("shutdown success");
//        } else {
//            System.out.println("failed to handle messages");
//        }
    }

    public static void sendMessage(Map<String, String> params) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        PubRequest request = new PubRequest();
        request.setIotInstanceId(iotInstanceId);
        request.setQos(0);

        request.setProductKey(productKey);
        request.setTopicFullName("/k0kh4u9Sfng/pi-1/user/get");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(Map.Entry<String, String> entry: params.entrySet()) {
            // System.out.println(entry.getKey() + " " + entry.getValue());
            sb.append("\"" + entry.getKey() + "\": \"" + entry.getValue() + "\", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");

        String s = sb.toString(); // {"led": "all"}
        System.out.println("sendMessage(): " + s);

        request.setMessageContent(Base64.encodeBase64String(s.getBytes()));

        try {
            PubResponse response = client.getAcsResponse(request);
            System.out.println("pub success?:" + response.getSuccess());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessage(final Message message) {
            try {
                //1.收到消息之后一定要ACK。
                // 推荐做法：创建Session选择Session.AUTO_ACKNOWLEDGE，这里会自动ACK。
                // 其他做法：创建Session选择Session.CLIENT_ACKNOWLEDGE，这里一定要调message.acknowledge()来ACK。
                // message.acknowledge();
                //2.建议异步处理收到的消息，确保onMessage函数里没有耗时逻辑。
                // 如果业务处理耗时过程过长阻塞住线程，可能会影响SDK收到消息后的正常回调。
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        processMessage(message);
                    }
                });
            } catch (Exception e) {
                System.err.println("submit task occurs exception " + e);
            }
        }
    };

    /**
     * 在这里处理您收到消息后的具体业务逻辑。
     */
    private static void processMessage(Message message) {
        try {
            byte[] body = message.getBody(byte[].class);
            String content = new String(body);
            String topic = message.getStringProperty("topic");
            String messageId = message.getStringProperty("messageId");
            long generateTime = message.getLongProperty("generateTime");
            ObjectMapper objectMapper = new ObjectMapper();
            Content json = objectMapper.readValue(content, Content.class);
            if(stackContent.size() > 100) {
                stackContent.clear();
            }
            stackContent.add(json);
            System.out.println(content);
            controller.paramsService.saveParamsAndLicenses(json);
            Map<String, Values> items = json.getItems();
            for (Map.Entry<String, Values> entry : items.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue().getValue());
            }
            System.out.println("receive message"
                + ",\ntopic = " + topic
                + ",\nmessageId = " + messageId
                + ",\ngenerateTime = " + generateTime
                + ",\ncontent = " + content);

        } catch (Exception e) {
            System.err.println("processMessage occurs error " + e);
        }
    }

    private static JmsConnectionListener myJmsConnectionListener = new JmsConnectionListener() {
        /**
         * 连接成功建立。
         */
        @Override
        public void onConnectionEstablished(URI remoteURI) {
            System.out.println("onConnectionEstablished, remoteUri: " + remoteURI);
        }

        /**
         * 尝试过最大重试次数之后，最终连接失败。
         */
        @Override
        public void onConnectionFailure(Throwable error) {
            System.err.println("onConnectionFailure, " + error.getMessage());
        }

        /**
         * 连接中断。
         */
        @Override
        public void onConnectionInterrupted(URI remoteURI) {
            System.out.println("onConnectionInterrupted, remoteUri: " + remoteURI);
        }

        /**
         * 连接中断后又自动重连上。
         */
        @Override
        public void onConnectionRestored(URI remoteURI) {
            System.out.println("onConnectionRestored, remoteUri: " + remoteURI);
        }

        @Override
        public void onInboundMessage(JmsInboundMessageDispatch envelope) {}

        @Override
        public void onSessionClosed(Session session, Throwable cause) {}

        @Override
        public void onConsumerClosed(MessageConsumer consumer, Throwable cause) {}

        @Override
        public void onProducerClosed(MessageProducer producer, Throwable cause) {}
    };

    /**
     * 计算签名，password组装方法，请参见AMQP客户端接入说明文档。
     */
    private static String doSign(String toSignString, String secret, String signMethod) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
        Mac mac = Mac.getInstance(signMethod);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(toSignString.getBytes());
        return Base64.encodeBase64String(rawHmac);
    }
}
