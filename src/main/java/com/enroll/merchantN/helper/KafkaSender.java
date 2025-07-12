package com.enroll.merchantN.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * @author raghav
 */
@Slf4j
@Service
public class KafkaSender {
    @Autowired
    private KafkaTemplate<String,Object> sendertemplate;
    @Value("@${spring.kafka.bootstrap-servers}")
    private String servers;

    public void sendMessage(String msisdn){
        log.info("sending message");
      //  if(isConnected(servers)) {
            CompletableFuture<SendResult<String, Object>> resultCompletableFuture =
                    sendertemplate.send("userMsisdn",2,null, msisdn);
//        CompletableFuture<SendResult<String, Object>> resultCompletableFuture =
//                    sendertemplate.send("userMsisdn",msisdn);
            resultCompletableFuture.whenComplete((result, ex) -> {
                if (ex == null) log.info("sent msisdn {}", msisdn);
                else log.info("unable to send msg");
            });
        //}else{
          //  log.info("connection failed");
        //}

    }
    public static boolean isConnected(String servers){
        Properties prop=new Properties();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,servers);
        try(AdminClient adminClient= AdminClient.create(prop)){
            adminClient.listTopics().names().get();
            return true;
        }catch(Exception e){
            log.info("failed to connect to server");
        }
        return false;
    }
}
