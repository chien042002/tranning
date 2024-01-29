package com.example.tranning.controller;

import com.example.tranning.model.SMS;
import com.example.tranning.repository.SMSRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SMSController {
    private final SMSRepository smsRepository;

    public SMSController(SMSRepository smsRepository) {
        this.smsRepository = smsRepository;
    }
    @PostMapping("/sms")
    public ResponseEntity<String> receiveSMS(@Valid @RequestBody SMS sms) {
        smsRepository.save(sms);
        return ResponseEntity.ok("Successfully inserted SMS");
    }
}
