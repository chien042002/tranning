package com.example.tranning.component;

import com.example.tranning.model.SMS;
import com.example.tranning.repository.SMSRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class SMSMessageProducer {
    private final RabbitTemplate rabbitTemplate;
    private final SMSRepository smsRepository;

    public SMSMessageProducer(RabbitTemplate rabbitTemplate, SMSRepository smsRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.smsRepository = smsRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void sendMessageToRabbitMQ() {
        List<SMS> smsList = smsRepository.findAll();
        for (SMS sms : smsList) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String message = objectMapper.writeValueAsString(sms);
                rabbitTemplate.convertAndSend("sms.mt.dev", message);
                System.out.println("Sent message: " + message);
            } catch (JsonProcessingException e) {

            }
        }
    }
}
