package com.example.tranning.Consumer;

import com.example.tranning.model.SMS;
import com.example.tranning.repository.SMSRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class SMSMessageConsumer {
    private static final String SMS_API_URL = "https://smsgw.vnpaytest.vn/smsgw/sendSms";
    private static final String PARTNER_CODE = "950003";
    private static final String SECRET_KEY = "5c6b322e-5d49-4ac3-9fab-1f2d9f3322d1";

    private final RestTemplate restTemplate;
    private final SMSRepository smsRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public SMSMessageConsumer(RestTemplate restTemplate, SMSRepository smsRepository, RedisTemplate<String, String> redisTemplate) {
        this.restTemplate = restTemplate;
        this.smsRepository = smsRepository;
        this.redisTemplate = redisTemplate;
    }

    @RabbitListener(queues = "sms.mt.dev")
    public void processMessageFromRabbitMQ(@Payload SMS sms) {

        String cacheKey = sms.getMessageId() + sms.getShortMessage() + sms.getDestination();
        if (redisTemplate.opsForValue().get(cacheKey) != null) {
            System.out.println("Duplicate message: " + sms.getMessageId());
            return;
        }


        try {

            HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_JSON);
    
            Map<String, Object> payload = new HashMap<>();
            payload.put("messageId", sms.getMessageId());
            payload.put("keyword", sms.getKeyword());
            payload.put("sender", sms.getSender());
            payload.put("destination", sms.getDestination());
            payload.put("shortMessage", sms.getShortMessage());
            payload.put("encryptMessage", "");
            payload.put("isEncrypt", 0);
            payload.put("type", 0);
            payload.put("requestTime", Instant.now().getEpochSecond());
            payload.put("partnerCode", PARTNER_CODE);






             
            payload.put("sercretKey", SECRET_KEY);
    
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(SMS_API_URL, request, String.class);
    

            if (response.getStatusCode() == HttpStatus.OK) {
                String result = response.getBody();
                sms.setStatus("sent");
                sms.setDescription(result);
                sms.setSentTime(Instant.now());
                smsRepository.save(sms);
    

                redisTemplate.opsForValue().set(cacheKey, sms.getMessageId(), Duration.ofHours(24));
    
                System.out.println("Sent message: " + sms.getMessageId());
            } else {
                System.out.println("Failed to send message: " + sms.getMessageId());
            }
        } catch (Exception e) {
        }
    }
}